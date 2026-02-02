package org.alphaxiv.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FeedResponseDto(
    @SerializedName("papers") val papers: List<PaperDto>
)

data class PaperDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("abstract") val abstract: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("universal_paper_id") val universalPaperId: String?,
    val authors: List<String>?,
    @SerializedName("paper_summary") val paperSummary: PaperSummaryDto?,
    val metrics: MetricsDto?,
    @SerializedName("version_id") val versionId: String?,
    @SerializedName("canonical_id") val canonicalId: String?,
    @SerializedName("publication_date") val publicationDate: String?,
    @SerializedName("first_publication_date") val firstPublicationDate: String?
)

data class PaperSummaryDto(
    @SerializedName("summary") val summary: String?
)

data class MetricsDto(
    @SerializedName("public_total_votes") val upvoteCount: Int?,
    @SerializedName("total_votes") val totalVotes: Int?,
    @SerializedName("visits_count") val visits: VisitsDto?
)

data class VisitsDto(
    @SerializedName("all") val all: Int?
)

data class OverviewResponseDto(
    @SerializedName("title") val title: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("summary") val summary: PaperSummaryDto?
)
