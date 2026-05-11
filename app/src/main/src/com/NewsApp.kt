package com

import android.app.Application
import com.data.local.db.AppDatabase
import com.data.local.seed.PreCrawledNewsDataSource
import com.data.local.source.LocalNewsDataSource
import com.data.local.source.LocalNewsDataSourceImpl
import com.data.local.source.OfflineNewsDataSource
import com.data.local.source.OfflineNewsDataSourceImpl
import com.data.remote.client.RetrofitClient
import com.data.remote.source.RemoteNewsDataSource
import com.data.remote.source.RemoteNewsDataSourceImpl
import com.data.repository.ProfileRepository
import com.data.repository.NewsRepository
import com.data.repository.NewsRepositoryImpl
import com.data.security.TokenManager
import com.data.settings.LocalCityStatsStore
import com.data.settings.UserSettingsRepository

class NewsApp : Application() {
    val appDatabase: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    private val preCrawledNewsDataSource: PreCrawledNewsDataSource by lazy {
        PreCrawledNewsDataSource(this)
    }

    val tokenManager: TokenManager by lazy {
        TokenManager(this)
    }

    private val remoteNewsDataSource: RemoteNewsDataSource by lazy {
        RetrofitClient.initialize(this)
        RemoteNewsDataSourceImpl(RetrofitClient.newsApiService)
    }

    private val localNewsDataSource: LocalNewsDataSource by lazy {
        LocalNewsDataSourceImpl(
            articleDao = appDatabase.articleDao(),
            localNewsCacheDao = appDatabase.localNewsCacheDao()
        )
    }

    private val offlineNewsDataSource: OfflineNewsDataSource by lazy {
        OfflineNewsDataSourceImpl(preCrawledNewsDataSource)
    }

    val repository: NewsRepository by lazy {
        NewsRepositoryImpl(
            remoteDataSource = remoteNewsDataSource,
            localDataSource = localNewsDataSource,
            offlineDataSource = offlineNewsDataSource,
            articleDao = appDatabase.articleDao(),
            bookmarkDao = appDatabase.bookmarkDao(),
            userDao = appDatabase.userDao(),
            preCrawledNewsDataSource = preCrawledNewsDataSource
        )
    }

    val profileRepository: ProfileRepository by lazy {
        ProfileRepository(appDatabase)
    }

    val userSettingsRepository: UserSettingsRepository by lazy {
        UserSettingsRepository(this)
    }

    val localCityStatsStore: LocalCityStatsStore by lazy {
        LocalCityStatsStore(this)
    }

    override fun onCreate() {
        super.onCreate()
        com.util.CrashLogger.install(this)
    }
}
