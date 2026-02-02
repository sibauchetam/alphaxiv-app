package org.alphaxiv.app.data.remote

import org.alphaxiv.app.data.model.Paper

interface PaperService {
    suspend fun getFeed(sort: String): List<Paper>
    suspend fun getPaperDetails(id: String): Paper
    suspend fun searchPapers(query: String): List<Paper>
    suspend fun getBlog(id: String): String
}
