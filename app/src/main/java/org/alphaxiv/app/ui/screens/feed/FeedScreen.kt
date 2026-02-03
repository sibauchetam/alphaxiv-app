package org.alphaxiv.app.ui.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.alphaxiv.app.data.model.Paper

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    onPaperClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedSort by remember { mutableStateOf("Hot") }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var searchQuery by remember { mutableStateOf("") }
    var searchActive by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)) {
                MediumTopAppBar(
                    title = {
                        Text(
                            "alphaXiv",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    actions = {
                        IconButton(onClick = { /* TODO: Profile or Settings */ }) {
                            Surface(
                                modifier = Modifier.size(32.dp),
                                shape = androidx.compose.foundation.shape.CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("A", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                )

                SearchBar(
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onSearch = { searchActive = false },
                            expanded = searchActive,
                            onExpandedChange = { searchActive = it },
                            placeholder = { Text("Search scientific papers...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                        )
                    },
                    expanded = searchActive,
                    onExpandedChange = { searchActive = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = SearchBarDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    tonalElevation = 2.dp
                ) {
                    // Search results or history could go here
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            SortSelector(
                selectedSort = selectedSort,
                onSortSelected = {
                    selectedSort = it
                    viewModel.loadFeed(it)
                }
            )

            PullToRefreshBox(
                isRefreshing = uiState is FeedUiState.Loading,
                onRefresh = { viewModel.loadFeed(selectedSort) },
                modifier = Modifier.fillMaxSize()
            ) {
                when (val state = uiState) {
                    is FeedUiState.Loading -> {
                        if ((uiState as? FeedUiState.Success)?.papers?.isEmpty() != false) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularWavyProgressIndicator()
                            }
                        }
                    }
                    is FeedUiState.Success -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(1.dp) // Tight but clean
                        ) {
                            items(state.papers, key = { it.id }) { paper ->
                                PaperCard(paper = paper, onClick = { onPaperClick(paper.id) })
                            }
                        }
                    }
                    is FeedUiState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = state.message, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortSelector(
    selectedSort: String,
    onSortSelected: (String) -> Unit
) {
    val options = listOf("Hot", "New", "Top")
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(options) { option ->
            FilterChip(
                selected = selectedSort == option,
                onClick = { onSortSelected(option) },
                label = { Text(option, fontWeight = FontWeight.Bold) },
                shape = MaterialTheme.shapes.medium
            )
        }
    }
}

@Composable
fun PaperCard(
    paper: Paper,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = paper.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 26.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = paper.authors.joinToString(", "),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (paper.thumbnailUrl != null) {
                    Spacer(modifier = Modifier.width(16.dp))
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(paper.thumbnailUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(70.dp, 94.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            if (paper.categories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(paper.categories) { category ->
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            shape = MaterialTheme.shapes.extraSmall
                        ) {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = paper.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${paper.upvoteCount}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${paper.commentCount}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = paper.publishedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        }
    }
}

@Composable
fun Icon(
    imageVector: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: androidx.compose.ui.graphics.Color = LocalContentColor.current
) {
    androidx.compose.material3.Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint
    )
}
