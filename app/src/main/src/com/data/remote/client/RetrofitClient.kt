package com.data.remote.client

import android.content.Context
import com.BuildConfig
import com.data.remote.api.NewsApiService
import com.util.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    /**
     * Giữ hàm initialize để NewsApp gọi không bị lỗi.
     * Khi app gọi trực tiếp NewsAPI thì hàm này không cần xử lý gì thêm.
     */
    fun initialize(context: Context) = Unit

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val newsApiKeyInterceptor = okhttp3.Interceptor { chain ->
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        val hasApiKeyAlready = originalUrl.queryParameter("apiKey").isNullOrBlank().not()
        val shouldAttachApiKey = Constants.NEWS_API_KEY.isNotBlank() && !hasApiKeyAlready

        val request = if (shouldAttachApiKey) {
            val urlWithApiKey = originalUrl.newBuilder()
                .addQueryParameter("apiKey", Constants.NEWS_API_KEY)
                .build()

            originalRequest.newBuilder()
                .url(urlWithApiKey)
                .build()
        } else {
            originalRequest
        }

        chain.proceed(request)
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(newsApiKeyInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    val newsApiService: NewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }
}