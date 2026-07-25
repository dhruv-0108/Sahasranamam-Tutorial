package com.stotra.sahasranamam.presentation.category

import androidx.lifecycle.SavedStateHandle
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

data class CategoryUiState(
    val isLoading: Boolean = false,
    val stotras: List<Stotra> = emptyList(),
    val error: String? = null
) {
    val groupedByDeity: Map<String, List<Stotra>>
        get() = stotras.groupBy { it.deity }
}

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: StotraRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init {
        val categoryName = savedStateHandle.get<String>("categoryName")
        if (categoryName != null) {
            loadStotrasForCategory(categoryName)
        }
    }

    private fun loadStotrasForCategory(categoryName: String) {
        viewModelScope.launch {
            repository.getAllStotras().collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                    is Resource.Success -> {
                        val filteredStotras = result.data?.filter { it.category.equals(categoryName, ignoreCase = true) } ?: emptyList()
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            stotras = filteredStotras
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
