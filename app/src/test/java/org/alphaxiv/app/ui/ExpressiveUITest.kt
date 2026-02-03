package org.alphaxiv.app.ui

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import org.alphaxiv.app.data.model.Paper
import org.alphaxiv.app.ui.theme.AlphaXivTheme
import org.alphaxiv.app.ui.screens.feed.PaperCard
import org.junit.Rule
import org.junit.Test
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class ExpressiveUITest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        theme = "android:Theme.Material.Light.NoActionBar"
    )

    @Test
    fun snapPaperCard() {
        val paper = Paper(
            id = "1",
            title = "Attention Is All You Need",
            authors = listOf("Ashish Vaswani", "Noam Shazeer"),
            summary = "The dominant sequence transduction models are based on complex recurrent or convolutional neural networks...",
            publishedDate = "12 Jun 2017",
            thumbnailUrl = null,
            categories = listOf("cs.CL", "cs.LG"),
            upvoteCount = 1337,
            commentCount = 42
        )

        paparazzi.snapshot {
            AlphaXivTheme(darkTheme = true, dynamicColor = false) {
                Box(modifier = Modifier.padding(16.dp)) {
                    PaperCard(paper = paper, onClick = {})
                }
            }
        }
    }
}
