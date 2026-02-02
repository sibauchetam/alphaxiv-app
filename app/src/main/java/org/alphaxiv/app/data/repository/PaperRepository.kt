package org.alphaxiv.app.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.alphaxiv.app.data.model.Paper
import org.alphaxiv.app.data.remote.PaperService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaperRepository @Inject constructor(
    private val paperService: PaperService
) {
    private val bookmarkedPaperIds = mutableSetOf<String>()

    suspend fun getFeed(sort: String): List<Paper> = paperService.getFeed(sort)
    suspend fun getPaperDetails(id: String): Paper = paperService.getPaperDetails(id)
    suspend fun searchPapers(query: String): List<Paper> = paperService.searchPapers(query)
    suspend fun getBlog(id: String): String = paperService.getBlog(id)

    fun toggleBookmark(id: String) {
        if (bookmarkedPaperIds.contains(id)) {
            bookmarkedPaperIds.remove(id)
        } else {
            bookmarkedPaperIds.add(id)
        }
    }

    fun isBookmarked(id: String): Boolean = bookmarkedPaperIds.contains(id)

    suspend fun getBookmarks(): List<Paper> = coroutineScope {
        bookmarkedPaperIds.map { id ->
            async { paperService.getPaperDetails(id) }
        }.awaitAll()
    }
}
