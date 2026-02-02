package org.alphaxiv.app.data.remote

import org.alphaxiv.app.data.model.Paper
import org.alphaxiv.app.data.remote.dto.*
import javax.inject.Inject

class NetworkPaperService @Inject constructor(
    private val api: AlphaXivApi
) : PaperService {

    override suspend fun getFeed(sort: String): List<Paper> {
        val apiSort = when (sort) {
            "Hot" -> "Hot"
            "Comments" -> "Comments"
            "Views" -> "Views"
            "Likes" -> "Likes"
            "GitHub" -> "GitHub"
            "Twitter (X)" -> "Twitter (X)"
            "Recommended" -> "Recommended"
            else -> "Hot"
        }
        return try {
            val response = api.getFeed(sort = apiSort)
            response.papers.map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getPaperDetails(id: String): Paper {
        return try {
            api.getPaperPreview(id).toDomain()
        } catch (e: Exception) {
            Paper(id, "Error loading", emptyList(), e.message ?: "", "", null, emptyList(), 0, 0)
        }
    }

    override suspend fun searchPapers(query: String): List<Paper> {
        return try {
            api.searchPapers(query).map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getBlog(id: String): String {
        return try {
            val preview = api.getPaperPreview(id)
            val versionId = preview.versionId ?: preview.id
            val overview = api.getPaperOverview(versionId)
            val content = overview.overview ?: "No blog content available."
            "# ${overview.title ?: preview.title}\n\n$content"
        } catch (e: Exception) {
            "Error loading blog: ${e.message}"
        }
    }

    private fun PaperDto.toDomain(): Paper {
        val summaryText = paperSummary?.summary ?: abstract ?: ""
        val assetBaseUrl = "https://paper-assets.alphaxiv.org/"
        val fullThumbnailUrl = imageUrl?.let {
            if (it.startsWith("http")) it else "$assetBaseUrl$it"
        }

        // The API seems to use 'public_total_votes' for upvotes shown on the site.
        // Comment count doesn't seem to be directly in the feed/preview DTO.
        // We use totalVotes as a possible surrogate or fallback to 0.

        return Paper(
            id = id,
            title = title,
            authors = authors ?: emptyList(),
            summary = summaryText,
            publishedDate = publicationDate ?: firstPublicationDate ?: "",
            thumbnailUrl = fullThumbnailUrl,
            categories = emptyList(),
            upvoteCount = metrics?.upvoteCount ?: 0,
            commentCount = 0
        )
    }
}
