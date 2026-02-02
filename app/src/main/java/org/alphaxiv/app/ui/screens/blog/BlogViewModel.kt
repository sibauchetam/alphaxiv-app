package org.alphaxiv.app.ui.screens.blog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.alphaxiv.app.data.repository.PaperRepository
import javax.inject.Inject

@HiltViewModel
class BlogViewModel @Inject constructor(
    private val repository: PaperRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BlogUiState>(BlogUiState.Loading)
    val uiState: StateFlow<BlogUiState> = _uiState.asStateFlow()

    fun loadBlog(id: String) {
        viewModelScope.launch {
            _uiState.value = BlogUiState.Loading
            try {
                val content = repository.getBlog(id)
                _uiState.value = BlogUiState.Success(content)
            } catch (e: Exception) {
                _uiState.value = BlogUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface BlogUiState {
    data object Loading : BlogUiState
    data class Success(val content: String) : BlogUiState
    data class Error(val message: String) : BlogUiState
}
