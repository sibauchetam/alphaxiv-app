package org.alphaxiv.app.ui.screens.bookmarks

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.alphaxiv.app.ui.screens.feed.PaperCard

@Composable
fun BookmarksScreen(
    viewModel: BookmarkViewModel,
    onPaperClick: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.loadBookmarks()
    }

    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Your Bookmarks", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {
            is BookmarkUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is BookmarkUiState.Success -> {
                if (state.papers.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No bookmarks yet", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.papers) { paper ->
                            PaperCard(paper = paper, onClick = { onPaperClick(paper.id) })
                        }
                    }
                }
            }
            is BookmarkUiState.Error -> {
                Text(text = state.message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
