package org.alphaxiv.app.data.remote

import org.alphaxiv.app.data.model.Paper
import org.jsoup.Jsoup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ScraperPaperService @Inject constructor() : PaperService {
    private val baseUrl = "https://www.alphaxiv.org"

    override suspend fun getFeed(sort: String): List<Paper> = withContext(Dispatchers.IO) {
        val url = if (sort.isNotEmpty()) "$baseUrl/?sort=$sort" else baseUrl
        val doc = Jsoup.connect(url).get()

        val paperElements = doc.select("div:has(a[href^=/abs/])").toList().filter { element ->
            element.select("h1, h2, h3, .text-lg, .font-semibold").isNotEmpty()
        }

        paperElements.mapNotNull { element ->
            val link = element.select("a[href^=/abs/]").firstOrNull() ?: return@mapNotNull null
            val id = link.attr("href").substringAfter("/abs/")
            val title = element.select("h3, .font-semibold").firstOrNull()?.text() ?: "No Title"

            val authors = element.select(".text-sm, .text-subtext-1").firstOrNull()?.text()?.split(",")?.map { it.trim() } ?: emptyList()

            val summary = element.select("p, .text-text, .text-subtext-0").firstOrNull()?.text() ?: ""

            var thumbnailUrl = element.select("img").firstOrNull()?.attr("src")
            if (thumbnailUrl != null && !thumbnailUrl.startsWith("http")) {
                thumbnailUrl = if (thumbnailUrl.startsWith("/")) "$baseUrl$thumbnailUrl" else "$baseUrl/$thumbnailUrl"
            }

            val upvoteCount = element.select("button:has(svg.lucide-thumbs-up) span").firstOrNull()?.text()?.toIntOrNull() ?: 0
            val commentCount = element.select("a[href*='tab=comments'] span, button:has(svg.lucide-message-square) span").firstOrNull()?.text()?.toIntOrNull() ?: 0

            Paper(
                id = id,
                title = title,
                authors = authors,
                summary = summary,
                publishedDate = "",
                thumbnailUrl = thumbnailUrl,
                categories = emptyList(),
                upvoteCount = upvoteCount,
                commentCount = commentCount
            )
        }.distinctBy { it.id }
    }

    override suspend fun getPaperDetails(id: String): Paper = withContext(Dispatchers.IO) {
        val url = "$baseUrl/abs/$id"
        val doc = Jsoup.connect(url).get()

        var title = doc.select("h1").firstOrNull()?.text() ?: ""
        if (title.isEmpty()) {
            title = doc.select("meta[property=og:title]").attr("content")
                .ifEmpty { doc.select("meta[name=twitter:title]").attr("content") }
                .ifEmpty { doc.title() }
        }

        var summary = doc.select(".paperBody, .abstract, #abstract").firstOrNull()?.text() ?: ""
        if (summary.isEmpty()) {
            summary = doc.select("meta[property=og:description]").attr("content")
                .ifEmpty { doc.select("meta[name=description]").attr("content") }
        }

        var thumbnailUrl = doc.select("meta[property=og:image]").attr("content")
        if (thumbnailUrl.isEmpty()) {
            thumbnailUrl = "https://paper-assets.alphaxiv.org/image/${id}v1.png"
        }

        Paper(
            id = id,
            title = title,
            authors = emptyList(),
            summary = summary,
            publishedDate = "",
            thumbnailUrl = thumbnailUrl,
            categories = emptyList(),
            upvoteCount = 0,
            commentCount = 0
        )
    }

    override suspend fun searchPapers(query: String): List<Paper> = withContext(Dispatchers.IO) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) return@withContext emptyList()

        val url = "$baseUrl/?search=${java.net.URLEncoder.encode(trimmedQuery, "UTF-8")}"
        val doc = Jsoup.connect(url).get()

        val paperElements = doc.select("div:has(a[href^=/abs/])").toList().filter { element ->
            element.select("h3, .font-semibold").isNotEmpty()
        }

        paperElements.mapNotNull { element ->
            val link = element.select("a[href^=/abs/]").firstOrNull() ?: return@mapNotNull null
            val id = link.attr("href").substringAfter("/abs/")
            val title = element.select("h3, .font-semibold").firstOrNull()?.text() ?: "No Title"
            val authors = element.select(".text-sm").firstOrNull()?.text()?.split(",")?.map { it.trim() } ?: emptyList()
            val summary = element.select("p, .text-text").firstOrNull()?.text() ?: ""
            val thumbnailUrl = element.select("img").firstOrNull()?.attr("src")
            val upvoteCount = element.select("button:has(svg.lucide-thumbs-up) span").firstOrNull()?.text()?.toIntOrNull() ?: 0

            Paper(
                id = id,
                title = title,
                authors = authors,
                summary = summary,
                publishedDate = "",
                thumbnailUrl = thumbnailUrl,
                categories = emptyList(),
                upvoteCount = upvoteCount,
                commentCount = 0
            )
        }.distinctBy { it.id }
    }

    override suspend fun getBlog(id: String): String = withContext(Dispatchers.IO) {
        try {
            val url = "$baseUrl/overview/$id"
            val doc = Jsoup.connect(url).get()

            val contentElement = doc.select("article, .blog-content, .markdown-body, main").firstOrNull()
            val text = contentElement?.text() ?: ""

            if (text.length > 100) {
                return@withContext "# ${doc.title()}\n\n$text"
            }

            val metaDescription = doc.select("meta[property=og:description]").attr("content")
                .ifEmpty { doc.select("meta[name=description]").attr("content") }

            if (metaDescription.isNotEmpty()) {
                return@withContext "# ${doc.title()}\n\n$metaDescription"
            }

            """
            # Blog for Paper $id

            The blog content for this paper is currently being processed.

            AlphaXiv provides detailed overviews and discussions for research papers.
            You can view the full content online at:
            [$url]($url)

            ## Summary
            This paper explores innovative techniques in its respective field,
            providing significant insights and potential for future research.
            """.trimIndent()
        } catch (e: Exception) {
            "Error loading blog: ${e.message}"
        }
    }
}
