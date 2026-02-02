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

        // This is a simplified selector, need to refine based on actual HTML
        // From curl, papers seem to be in a list.
        // Let's look for elements that have a link to /abs/
        val paperElements = doc.select("div:has(a[href^=/abs/])").toList().filter { element ->
            element.select("h1, h2, h3, .text-lg, .font-semibold").isNotEmpty()
        }

        paperElements.mapNotNull { element ->
            val link = element.select("a[href^=/abs/]").firstOrNull() ?: return@mapNotNull null
            val id = link.attr("href").substringAfter("/abs/", "").takeIf { it.isNotEmpty() } ?: return@mapNotNull null
            val title = element.select("h3, .font-semibold").firstOrNull()?.text()?.takeIf { it.isNotBlank() } ?: "No Title"

            // Authors usually in a list or specific class
            val authors = element.select(".text-sm").firstOrNull()?.text()
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() } ?: emptyList()

            val summary = element.select("p, .text-text").firstOrNull()?.text() ?: ""

            val thumbnailUrl = element.select("img").firstOrNull()?.attr("src")

            val upvoteCount = element.select("button:has(svg.lucide-thumbs-up) span").firstOrNull()?.text()?.toIntOrNull() ?: 0
            val commentCount = 0 // Harder to get without deep parsing

            Paper(
                id = id,
                title = title,
                authors = authors,
                summary = summary,
                publishedDate = "", // Need to parse from text
                thumbnailUrl = thumbnailUrl,
                categories = emptyList(), // Need to parse tags
                upvoteCount = upvoteCount,
                commentCount = commentCount
            )
        }.distinctBy { it.id }
    }

    override suspend fun getPaperDetails(id: String): Paper = withContext(Dispatchers.IO) {
        val url = "$baseUrl/abs/$id"
        val doc = Jsoup.connect(url).get()

        val title = doc.select("h1").firstOrNull()?.text()?.takeIf { it.isNotBlank() }
            ?: doc.select("meta[property=og:title]").firstOrNull()?.attr("content")
            ?: ""

        var summary = doc.select(".paperBody").firstOrNull()?.text()
        if (summary.isNullOrBlank() || summary.contains("loader")) {
            summary = doc.select("meta[property=og:description]").firstOrNull()?.attr("content")
                ?.substringAfter("Abstract: ")
                ?.substringAfter("View recent discussion. ")
        }

        val authors = doc.select("meta[name=twitter:image]").firstOrNull()?.attr("content")
            ?.substringAfter("authors=", "")
            ?.split("%2C", ",")
            ?.map { java.net.URLDecoder.decode(it.trim(), "UTF-8") }
            ?.filter { it.isNotBlank() } ?: emptyList()

        Paper(
            id = id,
            title = title,
            authors = authors,
            summary = summary ?: "",
            publishedDate = "",
            thumbnailUrl = "https://paper-assets.alphaxiv.org/image/${id}v1.png",
            categories = emptyList(),
            upvoteCount = 0,
            commentCount = 0
        )
    }

    override suspend fun searchPapers(query: String): List<Paper> = withContext(Dispatchers.IO) {
        val url = "$baseUrl/?search=${java.net.URLEncoder.encode(query, "UTF-8")}"
        val doc = Jsoup.connect(url).get()

        val paperElements = doc.select("div:has(a[href^=/abs/])").toList().filter { element ->
            element.select("h3, .font-semibold").isNotEmpty()
        }

        paperElements.mapNotNull { element ->
            val link = element.select("a[href^=/abs/]").firstOrNull() ?: return@mapNotNull null
            val id = link.attr("href").substringAfter("/abs/", "").takeIf { it.isNotEmpty() } ?: return@mapNotNull null
            val title = element.select("h3, .font-semibold").firstOrNull()?.text()?.takeIf { it.isNotBlank() } ?: "No Title"
            val authors = element.select(".text-sm").firstOrNull()?.text()
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() } ?: emptyList()
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

            // Try to find the blog content.
            // Some newer pages use specific article tags or markdown classes
            val contentElement = doc.select("article, .blog-content, .markdown-body, main section").firstOrNull {
                it.text().length > 200
            }

            if (contentElement != null) {
                val text = contentElement.text()
                return@withContext "# ${doc.title()}\n\n$text"
            }

            // Better Fallback: try to get details from /abs/ page
            val details = getPaperDetails(id)
            val authorsStr = if (details.authors.isNotEmpty()) "\n\n**Authors:** ${details.authors.joinToString(", ")}" else ""

            """
            # ${details.title.ifBlank { "Paper $id" }}
            $authorsStr

            ## Abstract
            ${details.summary.ifBlank { "No summary available." }}

            ---
            *The full blog content for this paper is currently being rendered on alphaXiv. You can view it online at:*
            [$url]($url)
            """.trimIndent()
        } catch (e: Exception) {
            "Error loading blog: ${e.message}"
        }
    }
}
