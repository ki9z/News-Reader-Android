package com.viewmodel.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.model.Article
import com.data.repository.NewsRepository
import com.ui.model.NewsUiModel
import com.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookmarkViewModel(
    private val repository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<NewsUiModel>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<NewsUiModel>>> = _uiState.asStateFlow()

    fun loadBookmarks() {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            val bookmarks = repository.getBookmarks()
            val items = bookmarks.map { it.toUiModel(true) }

            _uiState.value = if (items.isEmpty()) {
                UiState.Empty
            } else {
                UiState.Success(items)
            }
        }
    }

    fun removeBookmark(articleUrl: String) {
        viewModelScope.launch {
            repository.removeBookmark(articleUrl)
            loadBookmarks()
        }
    }

    private fun Article.toUiModel(isBookmarked: Boolean = false): NewsUiModel {
        return NewsUiModel(
            title = title.orEmpty(),
            description = description,
            content = content,
            imageUrl = urlToImage,
            articleUrl = url,
            sourceName = source?.name.orEmpty(),
            author = author,
            publishedAt = publishedAt,
            isBookmarked = isBookmarked
        )
    }
}
