package com.data.repository

import com.data.model.Article
import com.util.NetworkResult

interface NewsRepository {
    suspend fun getTopHeadlines(
        category: String?,
        country: String = "us",
        query: String? = null,
        sources: String? = null,
        page: Int,
        pageSize: Int
    ): NetworkResult<List<Article>>

    suspend fun searchNews(
        query: String,
        sortBy: String? = null,
        sources: String? = null,
        page: Int,
        pageSize: Int
    ): NetworkResult<List<Article>>

    suspend fun getLocalNews(
        locationQuery: String?,
        country: String,
        page: Int,
        pageSize: Int
    ): NetworkResult<List<Article>>

    suspend fun getCachedLocalNews(
        locationQuery: String?,
        country: String
    ): List<Article>

    suspend fun saveCachedLocalNews(
        locationQuery: String?,
        country: String,
        cityTitle: String,
        articles: List<Article>
    )

    suspend fun getBookmarks(): List<Article>

    suspend fun saveBookmark(article: Article)

    suspend fun removeBookmark(articleUrl: String)

    suspend fun isBookmarked(articleUrl: String): Boolean
}
