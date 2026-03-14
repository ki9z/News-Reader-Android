package com

import android.app.Application
import com.data.repository.MockNewsRepository
import com.data.repository.NewsRepository

class NewsApp : Application() {
    val repository: NewsRepository by lazy {
        MockNewsRepository()
    }
}
