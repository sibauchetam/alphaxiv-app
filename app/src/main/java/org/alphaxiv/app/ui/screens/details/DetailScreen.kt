package org.alphaxiv.app.ui.screens.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.alphaxiv.app.data.model.Paper

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetailScreen(
    id: String,
    viewModel: DetailViewModel,
    onBack: () -> Unit,
    onViewBlog: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(id) {
        viewModel.loadPaper(id)
    }

    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = if (uiState is DetailUiState.Success) (uiState as DetailUiState.Success).paper.title else "Paper Details",
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
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
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            if (uiState is DetailUiState.Success) {
                val paper = (uiState as DetailUiState.Success).paper
                ExtendedFloatingActionButton(
                    text = { Text("Read Paper") },
                    icon = { Icon(Icons.Default.Description, contentDescription = null) },
                    onClick = {
                        val pdfUrl = "https://www.alphaxiv.org/pdf/${paper.id}.pdf"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
                        context.startActivity(intent)
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = MaterialTheme.shapes.large
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val state = uiState) {
                is DetailUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        LoadingIndicator()
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
                                    .height(280.dp)
                                    .clip(MaterialTheme.shapes.extraLarge),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        Text(
                            text = paper.authors.joinToString(", "),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Published: ${paper.publishedDate}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.large,
                            modifier = Modifier.fillMaxWidth(),
                            tonalElevation = 1.dp
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Abstract",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = paper.summary,
                                    style = MaterialTheme.typography.bodyLarge,
                                    lineHeight = 24.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        OutlinedButton(
                            onClick = onViewBlog,
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Text("Join Discussion", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(80.dp)) // FAB spacing
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
