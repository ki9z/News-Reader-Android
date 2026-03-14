package com.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.model.Article
import com.data.repository.NewsRepository
import com.ui.model.NewsUiModel
import com.util.Constants
import com.util.NetworkResult
import com.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<NewsUiModel>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<NewsUiModel>>> = _uiState.asStateFlow()

    private var currentPage = Constants.INITIAL_PAGE
    private var selectedCategory: String? = Constants.CATEGORY_GENERAL
    private var currentItems: MutableList<NewsUiModel> = mutableListOf()
    private var isLoadingMore = false
    private var canLoadMore = true

    fun loadInitialNews() {
        currentPage = Constants.INITIAL_PAGE
        canLoadMore = true
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            when (val result = repository.getTopHeadlines(selectedCategory, currentPage, Constants.DEFAULT_PAGE_SIZE)) {
                is NetworkResult.Success -> {
                    val items = result.data.map { it.toUiModel() }
                    currentItems = items.toMutableList()

                    _uiState.value = if (items.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(items)
                    }
                }

                is NetworkResult.Error -> {
                    _uiState.value = UiState.Error(result.message)
                }
            }
        }
    }

    fun refreshNews() {
        loadInitialNews()
    }

    fun selectCategory(category: String) {
        selectedCategory = category
        loadInitialNews()
    }

    fun loadMoreNews() {
        if (isLoadingMore || !canLoadMore) return

        isLoadingMore = true
        currentPage++

        viewModelScope.launch {
            when (val result = repository.getTopHeadlines(selectedCategory, currentPage, Constants.DEFAULT_PAGE_SIZE)) {
                is NetworkResult.Success -> {
                    val newItems = result.data.map { it.toUiModel() }
                    if (newItems.isEmpty()) {
                        canLoadMore = false
                    } else {
                        currentItems.addAll(newItems)
                        _uiState.value = UiState.Success(currentItems.toList())
                    }
                }

                is NetworkResult.Error -> {
                    currentPage--
                }
            }

            isLoadingMore = false
        }
    }

    private fun Article.toUiModel(): NewsUiModel {
        return NewsUiModel(
            title = title.orEmpty(),
            description = description,
            content = content,
            imageUrl = urlToImage,
            articleUrl = url,
            sourceName = source?.name.orEmpty(),
            author = author,
            publishedAt = publishedAt
        )
    }
}