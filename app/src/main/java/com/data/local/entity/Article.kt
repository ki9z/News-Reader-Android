package com.example.myapplication.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.myapplication.data.domain.*
@Entity(
    "articles",
    foreignKeys = [
        ForeignKey(
        entity = Category::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value=["remoteId"],unique=true)]
)
data class Article(
    @PrimaryKey(autoGenerate= true) val id:Int = 0,
    val remoteId: String,
    val title: String,
    val author: String?,
    val source: String?,
    val categoryId: Long,
    val publishAt: Long,
    val createdAt: Long,
    val view: Long?,
    val status: ArticleStatus = ArticleStatus.PUBLISHED,
    val url: String
)
