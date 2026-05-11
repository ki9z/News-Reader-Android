package com.viewmodel.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.model.Article
import com.data.repository.NewsRepository
import com.data.repository.ProfileRepository
import com.data.settings.UserSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: NewsRepository,
    private val profileRepository: ProfileRepository,
    private val userSettingsRepository: UserSettingsRepository
) : ViewModel() {

    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked: StateFlow<Boolean> = _isBookmarked.asStateFlow()

    private val _readingHistoryId = MutableStateFlow<Long?>(null)
    val readingHistoryId: StateFlow<Long?> = _readingHistoryId.asStateFlow()

    fun checkBookmarkStatus(articleUrl: String) {
        val safeUrl = articleUrl.trim()
        if (safeUrl.isBlank()) return

        viewModelScope.launch {
            _isBookmarked.value = repository.isBookmarked(safeUrl)
        }
    }

    fun toggleBookmark(articleUrl: String, article: Article) {
        val safeUrl = articleUrl.trim()
        if (safeUrl.isBlank()) return

        viewModelScope.launch {
            if (_isBookmarked.value) {
                repository.removeBookmark(safeUrl)
                _isBookmarked.value = false
            } else {
                repository.saveBookmark(article)
                _isBookmarked.value = true
            }
        }
    }

    fun recordReading(article: Article, fromScreen: String) {
        viewModelScope.launch {
            val settings = userSettingsRepository.userSettingsFlow.first()
            if (!settings.trackReadingHistory) return@launch

            val id = profileRepository.recordReading(
                article = article,
                fromScreen = fromScreen.takeIf { it.isNotBlank() }
            )

            _readingHistoryId.value = id
        }
    }

    fun finishReading(readSeconds: Int, completionPercent: Int) {
        val historyId = _readingHistoryId.value ?: return

        viewModelScope.launch {
            profileRepository.finishReading(
                historyId = historyId,
                readSeconds = readSeconds,
                completionPercent = completionPercent
            )
        }
    }

    fun downloadArticle(article: Article) {
        viewModelScope.launch {
            profileRepository.addDownload(article)
        }
    }
}