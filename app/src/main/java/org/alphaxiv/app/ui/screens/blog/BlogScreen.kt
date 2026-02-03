package org.alphaxiv.app.ui.screens.blog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.alphaxiv.app.ui.components.LatexMarkdownText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogScreen(
    id: String,
    viewModel: BlogViewModel,
    onBack: () -> Unit
) {
    LaunchedEffect(id) {
        viewModel.loadBlog(id)
    }

    val uiState by viewModel.uiState.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    var showLanguageMenu by remember { mutableStateOf(false) }

    val languages = listOf(
        "en" to "English",
        "zh" to "Chinese",
        "ru" to "Russian",
        "fr" to "French",
        "ja" to "Japanese",
        "es" to "Spanish",
        "ko" to "Korean",
        "hi" to "Hindi",
        "de" to "German"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Blog") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showLanguageMenu = true }) {
                        Icon(Icons.Default.Language, contentDescription = "Language")
                    }
                    DropdownMenu(
                        expanded = showLanguageMenu,
                        onDismissRequest = { showLanguageMenu = false }
                    ) {
                        languages.forEach { (code, name) ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(selected = selectedLanguage == code, onClick = null)
                                        Spacer(Modifier.width(8.dp))
                                        Text(name)
                                    }
                                },
                                onClick = {
                                    viewModel.changeLanguage(code)
                                    showLanguageMenu = false
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val state = uiState) {
                is BlogUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is BlogUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        LatexMarkdownText(
                            markdown = state.content,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                is BlogUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
