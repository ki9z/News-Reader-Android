package com.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "article_blocks",
    foreignKeys = [
        ForeignKey(
            entity = ArticleEntity::class,
            parentColumns = ["url"],
            childColumns = ["articleUrl"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["articleUrl", "blockOrder"])
    ]
)
data class ArticleBlockEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val articleUrl: String,
    val type: String,
    val content: String? = null,
    val imageUrl: String? = null,
    val caption: String? = null,
    val blockOrder: Int = 0,
    val metadataJson: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)

