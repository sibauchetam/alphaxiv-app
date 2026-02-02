package org.alphaxiv.app.ui.screens.details

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
class DetailViewModel @Inject constructor(
    private val repository: PaperRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadPaper(id: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                val paper = repository.getPaperDetails(id)
                _uiState.value = DetailUiState.Success(paper, repository.isBookmarked(id))
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun toggleBookmark(id: String) {
        repository.toggleBookmark(id)
        val currentState = _uiState.value
        if (currentState is DetailUiState.Success) {
            _uiState.value = currentState.copy(isBookmarked = repository.isBookmarked(id))
        }
    }
}

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val paper: Paper, val isBookmarked: Boolean) : DetailUiState
    data class Error(val message: String) : DetailUiState
}
