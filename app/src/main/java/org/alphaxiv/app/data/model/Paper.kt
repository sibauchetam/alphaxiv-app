package org.alphaxiv.app.data.model

data class Paper(
    val id: String,
    val title: String,
    val authors: List<String>,
    val summary: String,
    val publishedDate: String,
    val thumbnailUrl: String?,
    val categories: List<String>,
    val upvoteCount: Int,
    val commentCount: Int
)
