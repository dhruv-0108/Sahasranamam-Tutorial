package com.stotra.sahasranamam.presentation.remedies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stotra.sahasranamam.core.result.Resource
import com.stotra.sahasranamam.domain.model.RemedyCategory
import com.stotra.sahasranamam.domain.model.Stotra
import com.stotra.sahasranamam.domain.repository.RemedyRepository
import com.stotra.sahasranamam.domain.repository.StotraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RemediesUiState(
    val isLoading: Boolean = false,
    val categories: List<RemedyCategory> = emptyList(),
    val stotras: List<Stotra> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class RemediesViewModel @Inject constructor(
    private val remedyRepository: RemedyRepository,
    private val stotraRepository: StotraRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RemediesUiState())
    val uiState: StateFlow<RemediesUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            stotraRepository.getAllStotras().collectLatest { stotraResult ->
                if (stotraResult is Resource.Success) {
                    _uiState.value = _uiState.value.copy(stotras = stotraResult.data)
                }
            }
        }

        viewModelScope.launch {
            remedyRepository.getRemedyCategories().collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            categories = result.data
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
}
