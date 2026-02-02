package org.alphaxiv.app.ui.screens.bookmarks

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
class BookmarkViewModel @Inject constructor(
    private val repository: PaperRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookmarkUiState>(BookmarkUiState.Loading)
    val uiState: StateFlow<BookmarkUiState> = _uiState.asStateFlow()

    fun loadBookmarks() {
        viewModelScope.launch {
            _uiState.value = BookmarkUiState.Loading
            try {
                val papers = repository.getBookmarks()
                _uiState.value = BookmarkUiState.Success(papers)
            } catch (e: Exception) {
                _uiState.value = BookmarkUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface BookmarkUiState {
    data object Loading : BookmarkUiState
    data class Success(val papers: List<Paper>) : BookmarkUiState
    data class Error(val message: String) : BookmarkUiState
}
