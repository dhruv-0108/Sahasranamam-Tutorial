package com.stotra.sahasranamam.presentation.study

import androidx.lifecycle.SavedStateHandle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stotra.sahasranamam.core.result.Resource
import com.stotra.sahasranamam.domain.repository.StotraRepository
import com.stotra.sahasranamam.core.audio.AudioPlayerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StotraStudyViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: StotraRepository,
    private val audioPlayerHelper: AudioPlayerHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(StotraStudyUiState())
    val uiState: StateFlow<StotraStudyUiState> = _uiState.asStateFlow()

    init {
        val stotraId = savedStateHandle.get<String>("stotraId")
        if (stotraId != null) {
            loadStotra(stotraId)
        }
    }

    fun loadStotra(stotraId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getShlokasForStotra(stotraId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                shlokas = resource.data,
                                currentShlokaIndex = 0
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                error = resource.message ?: "Failed to load Sri Suktam"
                            )
                        }
                    }
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun toggleSandhiSplit() {
        _uiState.update { it.copy(isSandhiSplitEnabled = !it.isSandhiSplitEnabled) }
    }

    fun toggleLanguageMeaning() {
        _uiState.update { it.copy(showHindiMeaning = !it.showHindiMeaning) }
    }

    fun selectShloka(index: Int) {
        if (index in 0 until _uiState.value.shlokas.size) {
            audioPlayerHelper.stop()
            _uiState.update { it.copy(currentShlokaIndex = index, isCardFlipped = false, isPlayingAudio = false) }
        }
    }

    fun nextShloka() {
        val nextIndex = _uiState.value.currentShlokaIndex + 1
        if (nextIndex < _uiState.value.shlokas.size) {
            audioPlayerHelper.stop()
            _uiState.update { it.copy(currentShlokaIndex = nextIndex, isCardFlipped = false, isPlayingAudio = false) }
        }
    }

    fun previousShloka() {
        val prevIndex = _uiState.value.currentShlokaIndex - 1
        if (prevIndex >= 0) {
            audioPlayerHelper.stop()
            _uiState.update { it.copy(currentShlokaIndex = prevIndex, isCardFlipped = false, isPlayingAudio = false) }
        }
    }

    fun toggleAudioPlayback() {
        val state = _uiState.value
        val currentShloka = state.shlokas.getOrNull(state.currentShlokaIndex) ?: return

        if (state.isPlayingAudio) {
            audioPlayerHelper.stop()
            _uiState.update { it.copy(isPlayingAudio = false) }
        } else {
            val textToRead = if (state.isSandhiSplitEnabled) currentShloka.sandhiSplitSanskrit else currentShloka.fullSanskrit
            audioPlayerHelper.playVerse(
                assetPath = "audios/sri_suktam_full.wav",
                sanskritText = textToRead,
                audioStartMs = currentShloka.audioStartMs,
                audioEndMs = currentShloka.audioEndMs,
                speed = state.playbackSpeed
            ) { isPlaying ->
                _uiState.update { it.copy(isPlayingAudio = isPlaying) }
            }
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        _uiState.update { it.copy(playbackSpeed = speed) }
        if (_uiState.value.isPlayingAudio) {
            toggleAudioPlayback()
            toggleAudioPlayback()
        }
    }

    fun flipCard() {
        _uiState.update { it.copy(isCardFlipped = !it.isCardFlipped) }
    }

    fun submitSrsRating(rating: Int) {
        val currentShloka = _uiState.value.shlokas.getOrNull(_uiState.value.currentShlokaIndex) ?: return
        viewModelScope.launch {
            repository.submitSrsReview(currentShloka.id, rating)
            nextShloka()
        }
    }

    fun playPadaAudio(pada: com.stotra.sahasranamam.domain.model.Pada) {
        val wordToSpeak = pada.sanskritCombined
        audioPlayerHelper.playVerse(
            assetPath = null,
            sanskritText = wordToSpeak,
            audioStartMs = pada.audioStartMs,
            audioEndMs = pada.audioEndMs,
            speed = _uiState.value.playbackSpeed
        ) { _ -> }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayerHelper.release()
    }
}
