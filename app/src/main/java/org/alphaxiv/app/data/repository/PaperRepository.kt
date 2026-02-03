package org.alphaxiv.app.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Collections
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.alphaxiv.app.data.model.Paper
import org.alphaxiv.app.data.remote.PaperService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaperRepository @Inject constructor(
    private val paperService: PaperService,
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("bookmarks_prefs", Context.MODE_PRIVATE)
    private val bookmarkedPaperIds = Collections.synchronizedSet(mutableSetOf<String>())

    init {
        val savedIds = prefs.getStringSet("bookmarked_ids", emptySet()) ?: emptySet()
        bookmarkedPaperIds.addAll(savedIds)
    }

    suspend fun getFeed(sort: String): List<Paper> = paperService.getFeed(sort)
    suspend fun getPaperDetails(id: String): Paper = paperService.getPaperDetails(id)
    suspend fun searchPapers(query: String): List<Paper> = paperService.searchPapers(query)
    suspend fun getBlog(id: String, lang: String): String = paperService.getBlog(id, lang)

    fun getOverviewLanguage(): String = prefs.getString("overview_language", "en") ?: "en"
    fun setOverviewLanguage(lang: String) = prefs.edit().putString("overview_language", lang).apply()

    fun toggleBookmark(id: String) {
        synchronized(bookmarkedPaperIds) {
            if (bookmarkedPaperIds.contains(id)) {
                bookmarkedPaperIds.remove(id)
            } else {
                bookmarkedPaperIds.add(id)
            }
            prefs.edit().putStringSet("bookmarked_ids", bookmarkedPaperIds.toSet()).apply()
        }
    }

    fun isBookmarked(id: String): Boolean = bookmarkedPaperIds.contains(id)

    suspend fun getBookmarks(): List<Paper> = coroutineScope {
        val ids = synchronized(bookmarkedPaperIds) { bookmarkedPaperIds.toSet() }
        ids.map { id ->
            async { paperService.getPaperDetails(id) }
        }.awaitAll()
    }
}
