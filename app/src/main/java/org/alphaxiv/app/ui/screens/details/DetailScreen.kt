package org.alphaxiv.app.ui.screens.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.alphaxiv.app.data.model.Paper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    id: String,
    viewModel: DetailViewModel,
    onBack: () -> Unit,
    onViewBlog: () -> Unit
) {
    LaunchedEffect(id) {
        viewModel.loadPaper(id)
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paper Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    val state = uiState
                    if (state is DetailUiState.Success) {
                        IconButton(onClick = { viewModel.toggleBookmark(id) }) {
                            Icon(
                                if (state.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark"
                            )
                        }
                    }
                    val context = LocalContext.current
                    IconButton(onClick = {
                        if (state is DetailUiState.Success) {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Check out this paper on alphaXiv: ${state.paper.title}\nhttps://www.alphaxiv.org/abs/${state.paper.id}")
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val state = uiState) {
                is DetailUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is DetailUiState.Success -> {
                    val paper = state.paper
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        if (paper.thumbnailUrl != null) {
                            AsyncImage(
                                model = paper.thumbnailUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                                    .clip(MaterialTheme.shapes.extraLarge),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        Text(
                            text = paper.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = paper.authors.joinToString(", "),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Abstract",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = paper.summary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        val context = LocalContext.current
                        Button(
                            onClick = {
                                val pdfUrl = "https://www.alphaxiv.org/pdf/${paper.id}.pdf"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.extraLarge
                        ) {
                            Text("View PDF")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = onViewBlog,
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.extraLarge
                        ) {
                            Text("View Blog")
                        }
                    }
                }
                is DetailUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
