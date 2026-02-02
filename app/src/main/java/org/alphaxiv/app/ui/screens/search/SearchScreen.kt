package org.alphaxiv.app.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import org.alphaxiv.app.ui.screens.feed.PaperCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onPaperClick: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        SearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = { viewModel.search(query) },
            active = false,
            onActiveChange = { },
            placeholder = { Text("Search papers...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        ) { }

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {
            is SearchUiState.Idle -> {
                Text(text = "Enter a query to search", style = MaterialTheme.typography.bodyMedium)
            }
            is SearchUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is SearchUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.results) { paper ->
                        PaperCard(paper = paper, onClick = { onPaperClick(paper.id) })
                    }
                }
            }
            is SearchUiState.Error -> {
                Text(text = state.message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
