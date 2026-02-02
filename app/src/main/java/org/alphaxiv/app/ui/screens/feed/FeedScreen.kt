package org.alphaxiv.app.ui.screens.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.alphaxiv.app.data.model.Paper

@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    onPaperClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedSort by remember { mutableStateOf("Hot") }

    Column(modifier = Modifier.fillMaxSize()) {
        SortSelector(
            selectedSort = selectedSort,
            onSortSelected = {
                selectedSort = it
                viewModel.loadFeed(it)
            }
        )

        when (val state = uiState) {
            is FeedUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is FeedUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortSelector(
    selectedSort: String,
    onSortSelected: (String) -> Unit
) {
    val options = listOf("Hot", "New", "Top")
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = selectedSort == option,
                onClick = { onSortSelected(option) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
            ) {
                Text(option)
            }
        }
    }
}

@Composable
fun PaperCard(
    paper: Paper,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            if (paper.thumbnailUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(paper.thumbnailUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp, 130.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = paper.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = paper.authors.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = paper.summary,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ThumbUp, contentDescription = null, size = 16.dp)
                    Text(text = " ${paper.upvoteCount}", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, size = 16.dp)
                    Text(text = " ${paper.commentCount}", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

// Extension to avoid error in the icon size
@Composable
fun Icon(imageVector: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String?, size: androidx.compose.ui.unit.Dp) {
    androidx.compose.material3.Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = Modifier.size(size)
    )
}
