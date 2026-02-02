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

    private val _selectedLanguage = MutableStateFlow(repository.getOverviewLanguage())
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private var currentId: String? = null

    fun loadBlog(id: String) {
        currentId = id
        viewModelScope.launch {
            _uiState.value = BlogUiState.Loading
            try {
                val content = repository.getBlog(id, _selectedLanguage.value)
                _uiState.value = BlogUiState.Success(content)
            } catch (e: Exception) {
                _uiState.value = BlogUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun changeLanguage(lang: String) {
        if (_selectedLanguage.value == lang) return
        _selectedLanguage.value = lang
        repository.setOverviewLanguage(lang)
        currentId?.let { loadBlog(it) }
    }
}

sealed interface BlogUiState {
    data object Loading : BlogUiState
    data class Success(val content: String) : BlogUiState
    data class Error(val message: String) : BlogUiState
}
