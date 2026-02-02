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
}

sealed interface FeedUiState {
    data object Loading : FeedUiState
    data class Success(val papers: List<Paper>) : FeedUiState
    data class Error(val message: String) : FeedUiState
}
