package org.alphaxiv.app.data.remote

import org.alphaxiv.app.data.model.Paper
import javax.inject.Inject

class MockPaperService @Inject constructor() : PaperService {
    override suspend fun getFeed(sort: String): List<Paper> {
        return listOf(
            Paper(
                id = "2601.20802",
                title = "Reinforcement Learning via Self-Distillation",
                authors = listOf("Jonas Hübotter", "Frederike Lübeck", "Lejs Behric"),
                summary = "The paper introduces Self-Distillation Policy Optimization (SDPO), an on-policy reinforcement learning algorithm...",
                publishedDate = "28 Jan 2026",
                thumbnailUrl = "https://paper-assets.alphaxiv.org/image/2601.20802v1.png",
                categories = listOf("agents", "computer-science", "artificial-intelligence"),
                upvoteCount = 148,
                commentCount = 8
            ),
            Paper(
                id = "2601.22158",
                title = "One-step Latent-free Image Generation with Pixel Mean Flows",
                authors = listOf("Yiyang Lu", "Susie Lu", "Qiao Sun"),
                summary = "Researchers from MIT and CMU introduce Pixel MeanFlow (pMF), a generative model capable of producing high-fidelity images...",
                publishedDate = "29 Jan 2026",
                thumbnailUrl = "https://paper-assets.alphaxiv.org/image/2601.22158v1.png",
                categories = listOf("computer-science", "computer-vision-and-pattern-recognition", "generative-models"),
                upvoteCount = 44,
                commentCount = 0
            )
        )
    }

    override suspend fun getPaperDetails(id: String): Paper {
        return getFeed("").first { it.id == id }
    }

    override suspend fun searchPapers(query: String): List<Paper> {
        return getFeed("").filter { it.title.contains(query, ignoreCase = true) }
    }
}
