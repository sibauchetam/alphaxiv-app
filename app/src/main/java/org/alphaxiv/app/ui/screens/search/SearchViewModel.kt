package org.alphaxiv.app.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.alphaxiv.app.data.model.Paper
import org.alphaxiv.app.data.repository.PaperRepository
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: PaperRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun search(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            try {
                val results = repository.searchPapers(query)
                _uiState.value = SearchUiState.Success(results)
            } catch (e: Exception) {
                _uiState.value = SearchUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Success(val results: List<Paper>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}
