package com.viewmodel.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.model.Article
import com.data.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: NewsRepository
) : ViewModel() {

    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked: StateFlow<Boolean> = _isBookmarked.asStateFlow()

    fun checkBookmarkStatus(articleUrl: String) {
        viewModelScope.launch {
            _isBookmarked.value = repository.isBookmarked(articleUrl)
        }
    }

    fun toggleBookmark(articleUrl: String, article: Article) {
        viewModelScope.launch {
            if (_isBookmarked.value) {
                repository.removeBookmark(articleUrl)
                _isBookmarked.value = false
            } else {
                repository.saveBookmark(article)
                _isBookmarked.value = true
            }
        }
    }
}
