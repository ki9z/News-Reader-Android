package com.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="sources")
data class Source(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String
)
