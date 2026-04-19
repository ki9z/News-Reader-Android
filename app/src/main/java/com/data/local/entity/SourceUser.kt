package com.data.local.entity
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.data.local.entity.*
@Entity(
    tableName = "source_user",
    primaryKeys = ["sourceId", "userId"],
    foreignKeys = [
        ForeignKey(
            entity = Source::class,
            parentColumns = ["id"],
            childColumns = ["sourceId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sourceId"), Index("userId")]
)
data class SourceUser(
    val sourceId: Long,
    val userId: Long,
    val followedAt: Long = System.currentTimeMillis()
)
