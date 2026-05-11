package com.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.data.local.dao.AdminArticleDao
import com.data.local.dao.AdminUserDao
import com.data.local.dao.ArticleBlockDao
import com.data.local.dao.ArticleDao
import com.data.local.dao.BookmarkDao
import com.data.local.dao.CategoryDao
import com.data.local.dao.DownloadDao
import com.data.local.dao.LocalNewsCacheDao
import com.data.local.dao.OtpTokenDao
import com.data.local.dao.ReadingHistoryDao
import com.data.local.dao.SearchHistoryDao
import com.data.local.dao.SyncOutboxDao
import com.data.local.dao.UserAuthProviderDao
import com.data.local.dao.UserDao
import com.data.local.dao.UserSettingsDao
import com.data.local.entity.AdminArticleEntity
import com.data.local.entity.AdminUserEntity
import com.data.local.entity.ArticleBlockEntity
import com.data.local.entity.ArticleEntity
import com.data.local.entity.CategoryEntity
import com.data.local.entity.LocalNewsCacheEntity
import com.data.local.entity.OtpTokenEntity
import com.data.local.entity.ReadingHistoryEntity
import com.data.local.entity.SyncOutboxEntity
import com.data.local.entity.UserAuthProviderEntity
import com.data.local.entity.UserBookmarkEntity
import com.data.local.entity.UserDownloadEntity
import com.data.local.entity.UserEntity
import com.data.local.entity.UserFollowedCategoryEntity
import com.data.local.entity.UserSearchHistoryEntity
import com.data.local.entity.UserSettingsEntity

@Database(
    entities = [
        ArticleEntity::class,
        UserEntity::class,
        UserAuthProviderEntity::class,
        UserSettingsEntity::class,
        CategoryEntity::class,
        UserFollowedCategoryEntity::class,
        UserBookmarkEntity::class,
        ArticleBlockEntity::class,
        LocalNewsCacheEntity::class,
        ReadingHistoryEntity::class,
        UserDownloadEntity::class,
        UserSearchHistoryEntity::class,
        SyncOutboxEntity::class,
        // Admin entities
        AdminUserEntity::class,
        OtpTokenEntity::class,
        AdminArticleEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    // Consumer DAOs
    abstract fun articleDao(): ArticleDao
    abstract fun userDao(): UserDao
    abstract fun userAuthProviderDao(): UserAuthProviderDao
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun categoryDao(): CategoryDao
    abstract fun articleBlockDao(): ArticleBlockDao
    abstract fun localNewsCacheDao(): LocalNewsCacheDao
    abstract fun readingHistoryDao(): ReadingHistoryDao
    abstract fun downloadDao(): DownloadDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun syncOutboxDao(): SyncOutboxDao

    // Admin DAOs
    abstract fun adminUserDao(): AdminUserDao
    abstract fun otpTokenDao(): OtpTokenDao
    abstract fun adminArticleDao(): AdminArticleDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "news_reader_db"
                )
                    .addMigrations(
                        DbMigrations.MIGRATION_1_2,
                        DbMigrations.MIGRATION_2_3,
                        DbMigrations.MIGRATION_3_4,
                        DbMigrations.MIGRATION_4_5,
                        DbMigrations.MIGRATION_5_6
                    )
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
