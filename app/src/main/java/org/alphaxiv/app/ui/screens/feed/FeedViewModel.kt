package org.alphaxiv.app.ui.screens.feed

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
class FeedViewModel @Inject constructor(
    private val repository: PaperRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private val _searchState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val searchState: StateFlow<SearchUiState> = _searchState.asStateFlow()

    init {
        loadFeed("Hot")
    }

    fun loadFeed(sort: String) {
        viewModelScope.launch {
            _uiState.value = FeedUiState.Loading
            try {
                val papers = repository.getFeed(sort)
                _uiState.value = FeedUiState.Success(papers)
            } catch (e: Exception) {
                _uiState.value = FeedUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun search(query: String) {
        if (query.isBlank()) {
            _searchState.value = SearchUiState.Idle
            return
        }
        viewModelScope.launch {
            _searchState.value = SearchUiState.Loading
            try {
                val results = repository.searchPapers(query)
                _searchState.value = SearchUiState.Success(results)
            } catch (e: Exception) {
                _searchState.value = SearchUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun clearSearch() {
        _searchState.value = SearchUiState.Idle
    }
}

sealed interface FeedUiState {
    data object Loading : FeedUiState
    data class Success(val papers: List<Paper>) : FeedUiState
    data class Error(val message: String) : FeedUiState
}

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Success(val results: List<Paper>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}
