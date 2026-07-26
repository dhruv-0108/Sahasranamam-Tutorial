package com.stotra.sahasranamam.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stotra.sahasranamam.core.result.Resource
import com.stotra.sahasranamam.domain.model.Stotra
import com.stotra.sahasranamam.domain.repository.StotraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val stotras: List<Stotra> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val recentSelection: com.stotra.sahasranamam.domain.model.RecentSelection? = null
) {
    val categorizedStotras: Map<String, List<Stotra>>
        get() {
            val filtered = if (searchQuery.isBlank()) {
                stotras
            } else {
                stotras.filter { 
                    it.titleEnglish.contains(searchQuery, ignoreCase = true) || 
                    it.titleDevanagari.contains(searchQuery, ignoreCase = true)
                }
            }
            return filtered.groupBy { it.category }
        }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: StotraRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadStotras()
        loadRecentSelection()
    }

    fun loadRecentSelection() {
        viewModelScope.launch {
            val result = repository.getRecentSelection()
            if (result is Resource.Success) {
                _uiState.value = _uiState.value.copy(recentSelection = result.data)
            }
        }
    }

    private fun loadStotras() {
        viewModelScope.launch {
            repository.getAllStotras().collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            stotras = result.data ?: emptyList()
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

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }
}
