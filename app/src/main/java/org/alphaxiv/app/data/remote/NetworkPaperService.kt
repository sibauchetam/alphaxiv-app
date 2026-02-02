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

        // CRITICAL: We use universalPaperId as the primary domain ID because it's what
        // the API endpoints (/preview, /overview) and web links expect.
        // Falling back to id (UUID) if universalPaperId is missing.
        val domainId = universalPaperId ?: id

        return Paper(
            id = domainId,
            title = title,
            authors = authors ?: emptyList(),
            summary = summaryText,
            publishedDate = publicationDate?.toString() ?: firstPublicationDate?.toString() ?: "",
            thumbnailUrl = fullThumbnailUrl,
            categories = emptyList(),
            upvoteCount = metrics?.upvoteCount ?: 0,
            commentCount = 0
        )
    }
}
