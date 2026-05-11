package com.data.mapper

import com.data.local.entity.ArticleEntity
import com.data.model.Article
import com.data.model.Source

object LocalMapper {
    fun Article.toEntity(): ArticleEntity = ArticleEntity(
        url = url.orEmpty(),
        city = null,
        sourceId = source?.id,
        sourceName = source?.name,
        author = author,
        title = title,
        description = description,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content
    )

    fun ArticleEntity.toArticle(): Article = Article(
        source = Source(id = sourceId, name = sourceName),
        author = author,
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content
    )
}
