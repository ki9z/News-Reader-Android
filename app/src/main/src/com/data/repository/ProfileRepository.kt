package com.data.repository

import android.os.Build
import com.data.local.db.AppDatabase
import com.data.local.db.DbMigrations
import com.data.local.entity.CategoryEntity
import com.data.local.entity.ReadingHistoryEntity
import com.data.local.entity.UserDownloadEntity
import com.data.local.entity.UserEntity
import com.data.local.entity.UserFollowedCategoryEntity
import com.data.local.entity.UserSearchHistoryEntity
import com.data.mapper.LocalMapper.toArticle
import com.data.mapper.LocalMapper.toEntity
import com.data.model.Article
import com.ui.model.FollowTopicUiModel
import com.ui.model.NewsUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class ProfileRepository(
    private val database: AppDatabase
) {

    private val articleDao = database.articleDao()
    private val readingHistoryDao = database.readingHistoryDao()
    private val downloadDao = database.downloadDao()
    private val categoryDao = database.categoryDao()
    private val searchHistoryDao = database.searchHistoryDao()
    private val userDao = database.userDao()

    suspend fun bootstrap() {
        ensureDefaultUser()
        seedCategoriesIfNeeded()
    }

    fun observeReadingHistoryItems(): Flow<List<NewsUiModel>> {
        return readingHistoryDao.observeByUserId(DEFAULT_USER_ID).map { historyItems ->
            val orderedUrls = historyItems.map { it.articleUrl }.distinct()
            if (orderedUrls.isEmpty()) return@map emptyList<NewsUiModel>()

            val articleMap = articleDao
                .getArticlesByUrls(orderedUrls)
                .associateBy { it.url }

            historyItems.mapNotNull { entry ->
                val article = articleMap[entry.articleUrl]?.toArticle() ?: return@mapNotNull null

                NewsUiModel(
                    title = article.title.orEmpty(),
                    description = article.description,
                    content = article.content,
                    imageUrl = article.urlToImage,
                    articleUrl = article.url,
                    sourceName = article.source?.name.orEmpty(),
                    author = article.author,
                    publishedAt = article.publishedAt,
                    itemId = entry.id,
                    eventTimeMillis = entry.openedAt,
                    completionPercent = entry.completionPercent,
                    readSeconds = entry.readSeconds,
                    canResume = entry.completionPercent in 1..99
                )
            }
        }
    }

    fun observeDownloadedItems(): Flow<List<NewsUiModel>> {
        return downloadDao.observeByUserId(DEFAULT_USER_ID).map { downloads ->
            val orderedUrls = downloads.map { it.articleUrl }.distinct()
            if (orderedUrls.isEmpty()) return@map emptyList<NewsUiModel>()

            val articleMap = articleDao
                .getArticlesByUrls(orderedUrls)
                .associateBy { it.url }

            downloads.mapNotNull { entry ->
                val article = articleMap[entry.articleUrl]?.toArticle() ?: return@mapNotNull null

                NewsUiModel(
                    title = article.title.orEmpty(),
                    description = article.description,
                    content = article.content,
                    imageUrl = article.urlToImage,
                    articleUrl = article.url,
                    sourceName = article.source?.name.orEmpty(),
                    author = article.author,
                    publishedAt = article.publishedAt,
                    itemId = entry.id,
                    eventTimeMillis = entry.downloadedAt,
                    status = entry.status,
                    fileSizeBytes = entry.fileSizeBytes
                )
            }
        }
    }

    fun observeFollowTopics(): Flow<List<FollowTopicUiModel>> {
        return combine(
            categoryDao.observeAllActiveCategories(),
            categoryDao.observeFollowedCategoryIds(DEFAULT_USER_ID)
        ) { categories, followedIds ->
            val followedSet = followedIds.toSet()

            categories.map { category ->
                FollowTopicUiModel(
                    id = category.id,
                    name = category.name,
                    isFollowed = followedSet.contains(category.id),
                    newTodayCount = ((category.name.length * 7) % 12) + 1,
                    notificationsEnabled = true
                )
            }
        }
    }

    suspend fun toggleFollowTopic(item: FollowTopicUiModel) {
        ensureDefaultUser()

        if (item.isFollowed) {
            categoryDao.unfollow(DEFAULT_USER_ID, item.id)
        } else {
            categoryDao.follow(
                UserFollowedCategoryEntity(
                    userId = DEFAULT_USER_ID,
                    categoryId = item.id,
                    followedAt = System.currentTimeMillis(),
                    notificationsEnabled = true
                )
            )
        }
    }

    suspend fun recordReading(article: Article, fromScreen: String?): Long? {
        val articleUrl = article.url.orEmpty()
        if (articleUrl.isBlank()) return null

        ensureDefaultUser()

        articleDao.insertBookmark(article.toEntity())

        return readingHistoryDao.insert(
            ReadingHistoryEntity(
                userId = DEFAULT_USER_ID,
                articleUrl = articleUrl,
                openedAt = System.currentTimeMillis(),
                readSeconds = 0,
                completionPercent = 1,
                fromScreen = fromScreen,
                deviceInfo = Build.MODEL
            )
        )
    }

    suspend fun finishReading(
        historyId: Long,
        readSeconds: Int,
        completionPercent: Int
    ) {
        if (historyId <= 0L) return

        readingHistoryDao.finish(
            historyId = historyId,
            closedAt = System.currentTimeMillis(),
            readSeconds = readSeconds.coerceAtLeast(1),
            completionPercent = completionPercent.coerceIn(1, 100)
        )
    }

    suspend fun recordSearch(query: String) {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isBlank()) return

        ensureDefaultUser()

        searchHistoryDao.insert(
            UserSearchHistoryEntity(
                userId = DEFAULT_USER_ID,
                query = normalizedQuery,
                searchedAt = System.currentTimeMillis()
            )
        )
    }

    fun observeSearchHistoryQueries(limit: Int = 8): Flow<List<String>> {
        return searchHistoryDao.observeByUserId(DEFAULT_USER_ID, limit)
            .map { historyItems ->
                historyItems
                    .map { it.query.trim() }
                    .filter { it.isNotBlank() }
                    .distinct()
                    .take(limit)
            }
    }

    suspend fun getFollowingQuery(): String {
        bootstrap()

        val followedNames = categoryDao
            .getFollowedCategories(DEFAULT_USER_ID)
            .map { it.name.trim() }
            .filter { it.isNotBlank() }
            .filterNot { it.equals("Top News", ignoreCase = true) }

        val fallback = listOf(
            "technology",
            "business",
            "science",
            "health",
            "sports"
        )

        val selected = followedNames.ifEmpty { fallback }

        return selected.joinToString(" OR ") { name ->
            val safeName = name.replace("\"", "")
            if (safeName.contains(" ")) "\"$safeName\"" else safeName
        }
    }

    suspend fun addDownload(article: Article) {
        val articleUrl = article.url.orEmpty()
        if (articleUrl.isBlank()) return

        ensureDefaultUser()

        articleDao.insertBookmark(article.toEntity())

        downloadDao.upsert(
            UserDownloadEntity(
                userId = DEFAULT_USER_ID,
                articleUrl = articleUrl,
                downloadedAt = System.currentTimeMillis(),
                localPath = "offline://article/${articleUrl.hashCode()}",
                fileSizeBytes = estimateArticleSizeBytes(article),
                status = UserDownloadEntity.STATUS_DONE,
                expiresAt = null
            )
        )
    }

    suspend fun removeDownload(articleUrl: String) {
        if (articleUrl.isBlank()) return
        downloadDao.delete(DEFAULT_USER_ID, articleUrl)
    }

    suspend fun removeHistoryItem(historyId: Long) {
        readingHistoryDao.deleteById(historyId)
    }

    suspend fun clearReadingHistory() {
        readingHistoryDao.clearByUserId(DEFAULT_USER_ID)
    }

    suspend fun clearDownloads() {
        downloadDao.clearByUserId(DEFAULT_USER_ID)
    }

    suspend fun clearSearchHistory() {
        searchHistoryDao.clearByUserId(DEFAULT_USER_ID)
    }

    suspend fun resetFollowingTopics() {
        categoryDao.clearFollowedByUserId(DEFAULT_USER_ID)
    }

    private suspend fun ensureDefaultUser() {
        if (userDao.getById(DEFAULT_USER_ID) != null) return

        val now = System.currentTimeMillis()

        userDao.upsert(
            UserEntity(
                id = DEFAULT_USER_ID,
                email = "guest@newsreader.app",
                fullName = "Guest User",
                role = "guest",
                isSignedIn = false,
                createdAt = now,
                updatedAt = now
            )
        )
    }

    private fun estimateArticleSizeBytes(article: Article): Long {
        val text = buildString {
            append(article.title.orEmpty())
            append(article.description.orEmpty())
            append(article.content.orEmpty())
            append(article.url.orEmpty())
            append(article.urlToImage.orEmpty())
            append(article.source?.name.orEmpty())
            append(article.author.orEmpty())
            append(article.publishedAt.orEmpty())
        }

        return text.toByteArray(Charsets.UTF_8).size.toLong()
    }

    private suspend fun seedCategoriesIfNeeded() {
        if (categoryDao.getCategoryCount() > 0) return

        val now = System.currentTimeMillis()

        categoryDao.upsertCategories(
            defaultCategories.mapIndexed { index, name ->
                CategoryEntity(
                    id = name.lowercase().replace(" ", "_"),
                    name = name,
                    sortOrder = index,
                    createdAt = now,
                    updatedAt = now
                )
            }
        )
    }

    companion object {
        private val defaultCategories = listOf(
            "Top News",
            "Politics",
            "Business",
            "Technology",
            "Sports",
            "Entertainment",
            "Health",
            "Science",
            "Travel",
            "Lifestyle"
        )

        const val DEFAULT_USER_ID: String = DbMigrations.DEFAULT_USER_ID
    }
}