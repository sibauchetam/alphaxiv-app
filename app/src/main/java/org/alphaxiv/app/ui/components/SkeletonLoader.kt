package org.alphaxiv.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedShimmer(content: @Composable (Brush) -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    val shimmerColors = listOf(
        colorScheme.surfaceContainer,
        colorScheme.surfaceContainerHighest,
        colorScheme.surfaceContainer
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 5000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2500,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnimation.value - 500, y = translateAnimation.value - 500),
        end = Offset(x = translateAnimation.value, y = translateAnimation.value)
    )

    content(brush)
}

@Composable
fun SkeletonPaperCard(brush: Brush) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        color = MaterialTheme.colorScheme.surfaceBright,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Spacer(
                    Modifier
                        .fillMaxWidth(0.4f)
                        .height(14.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(brush)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(brush)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Spacer(
                    Modifier
                        .fillMaxWidth(0.7f)
                        .height(24.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(brush)
                )
            }
            Spacer(
                Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(brush)
            )
        }
    }
}

@Composable
fun SkeletonHeroCard(brush: Brush) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        color = MaterialTheme.colorScheme.surfaceBright,
        shape = MaterialTheme.shapes.extraLarge,
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Spacer(
                Modifier
                    .fillMaxWidth(0.6f)
                    .height(32.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(18.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Spacer(
                Modifier
                    .fillMaxWidth(0.9f)
                    .height(18.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(brush)
            )
        }
    }
}

@Composable
fun FeedSkeleton(insets: PaddingValues = PaddingValues(0.dp)) {
    AnimatedShimmer { brush ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = insets,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                SkeletonHeroCard(brush)
            }
            items(5) { SkeletonPaperCard(brush) }
        }
    }
}
