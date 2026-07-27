package com.stotra.sahasranamam.presentation.study

import com.stotra.sahasranamam.domain.model.Shloka

data class StotraStudyUiState(
    val isLoading: Boolean = true,
    val shlokas: List<Shloka> = emptyList(),
    val currentShlokaIndex: Int = 0,
    val isSandhiSplitEnabled: Boolean = true,
    val showHindiMeaning: Boolean = false,
    val isPlayingAudio: Boolean = false,
    val playbackSpeed: Float = 0.85f,
    val isCardFlipped: Boolean = false,
    val error: String? = null
)
