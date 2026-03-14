package com.data.repository

import com.data.local.dao.ArticleDao
import com.data.mapper.LocalMapper.toArticle
import com.data.mapper.LocalMapper.toEntity
import com.data.model.Article
import com.data.remote.api.NewsApiService
import com.util.Constants
import com.util.NetworkResult

class NewsRepositoryImpl(
    private val apiService: NewsApiService,
    private val articleDao: ArticleDao
) : NewsRepository {

    override suspend fun getTopHeadlines(
        category: String?,
        page: Int,
        pageSize: Int
    ): NetworkResult<List<Article>> {
        return try {
            val response = apiService.getTopHeadlines(
                category = category,
                page = page,
                pageSize = pageSize,
                apiKey = Constants.API_KEY
            )
            if (response.isSuccessful) {
                val articles = response.body()?.articles?.filterNot {
                    it.title.isNullOrBlank() || it.title == "[Removed]"
                } ?: emptyList()
                NetworkResult.Success(articles)
            } else {
                NetworkResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun searchNews(
        query: String,
        page: Int,
        pageSize: Int
    ): NetworkResult<List<Article>> {
        return try {
            val response = apiService.searchNews(
                query = query,
                page = page,
                pageSize = pageSize,
                apiKey = Constants.API_KEY
            )
            if (response.isSuccessful) {
                val articles = response.body()?.articles?.filterNot {
                    it.title.isNullOrBlank() || it.title == "[Removed]"
                } ?: emptyList()
                NetworkResult.Success(articles)
            } else {
                NetworkResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun getBookmarks(): List<Article> {
        return articleDao.getAllBookmarks().map { it.toArticle() }
    }

    override suspend fun saveBookmark(article: Article) {
        articleDao.insertBookmark(article.toEntity())
    }

    override suspend fun removeBookmark(articleUrl: String) {
        articleDao.deleteBookmark(articleUrl)
    }

    override suspend fun isBookmarked(articleUrl: String): Boolean {
        return articleDao.isBookmarked(articleUrl) > 0
    }
}
