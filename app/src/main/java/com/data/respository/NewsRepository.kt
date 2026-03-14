package com.data.repository

import com.data.model.Article
import com.util.NetworkResult

interface NewsRepository {
    suspend fun getTopHeadlines(
        category: String?,
        page: Int,
        pageSize: Int
    ): NetworkResult<List<Article>>

    suspend fun searchNews(
        query: String,
        page: Int,
        pageSize: Int
    ): NetworkResult<List<Article>>

    suspend fun getBookmarks(): List<Article>

    suspend fun saveBookmark(article: Article)

    suspend fun removeBookmark(articleUrl: String)

    suspend fun isBookmarked(articleUrl: String): Boolean
}