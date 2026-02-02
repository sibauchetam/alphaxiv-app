package org.alphaxiv.app.data.remote

import org.alphaxiv.app.data.remote.dto.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AlphaXivApi {
    @GET("papers/v3/feed")
    suspend fun getFeed(
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("interval") interval: String = "All time",
        @Query("sort") sort: String = "Hot"
    ): FeedResponseDto

    @GET("v1/search/paper")
    suspend fun searchPapers(
        @Query("q") query: String
    ): List<PaperDto>

    @GET("papers/v3/{id}/preview")
    suspend fun getPaperPreview(
        @Path("id") id: String
    ): PaperDto

    @GET("papers/v3/{versionId}/overview/en")
    suspend fun getPaperOverview(
        @Path("versionId") versionId: String
    ): OverviewResponseDto
}
