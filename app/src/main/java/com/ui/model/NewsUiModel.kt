package com.ui.model

data class NewsUiModel(
    val title: String,
    val description: String?,
    val content: String?,
    val imageUrl: String?,
    val articleUrl: String?,
    val sourceName: String,
    val author: String?,
    val publishedAt: String?,
    val isBookmarked: Boolean = false
)