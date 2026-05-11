package com.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE isSignedIn = 1 LIMIT 1")
    fun observeSignedInUser(): Flow<UserEntity?>

    @Query("UPDATE users SET isSignedIn = CASE WHEN id = :userId THEN 1 ELSE 0 END, updatedAt = :updatedAt")
    suspend fun markSingleSignedIn(userId: String, updatedAt: Long)

    @Query("UPDATE users SET isSignedIn = 0, updatedAt = :updatedAt")
    suspend fun signOutAll(updatedAt: Long)
}

