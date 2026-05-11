package com.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.repository.ProfileRepository
import com.ui.model.FollowTopicUiModel
import com.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FollowingTopicsViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    enum class FollowTab { TOPICS, SOURCES, KEYWORDS }

    private val allTopics = MutableStateFlow<List<FollowTopicUiModel>>(emptyList())
    private val query = MutableStateFlow("")
    private val mutedIds = MutableStateFlow<Set<String>>(emptySet())
    private val blockedIds = MutableStateFlow<Set<String>>(emptySet())
    private val followTab = MutableStateFlow(FollowTab.TOPICS)

    private val sourceItems = MutableStateFlow(
        listOf(
            FollowTopicUiModel(id = "source_bbc", name = "BBC News", isFollowed = false, type = "source", newTodayCount = 8),
            FollowTopicUiModel(id = "source_cnn", name = "CNN", isFollowed = false, type = "source", newTodayCount = 5),
            FollowTopicUiModel(id = "source_reuters", name = "Reuters", isFollowed = false, type = "source", newTodayCount = 10)
        )
    )

    private val keywordItems = MutableStateFlow(
        listOf(
            FollowTopicUiModel(id = "kw_ai", name = "AI", isFollowed = false, type = "keyword", newTodayCount = 6),
            FollowTopicUiModel(id = "kw_startup", name = "Startup", isFollowed = false, type = "keyword", newTodayCount = 3),
            FollowTopicUiModel(id = "kw_education", name = "Education", isFollowed = false, type = "keyword", newTodayCount = 4),
            FollowTopicUiModel(id = "kw_vietnam", name = "Vietnam", isFollowed = false, type = "keyword", newTodayCount = 7),
            FollowTopicUiModel(id = "kw_football", name = "Football", isFollowed = false, type = "keyword", newTodayCount = 9)
        )
    )

    private val mergedTopicData = combine(allTopics, sourceItems) { topics, sources ->
        topics to sources
    }

    private val groupedTopicData = combine(mergedTopicData, keywordItems) { topicAndSource, keywords ->
        Triple(topicAndSource.first, topicAndSource.second, keywords)
    }

    private val baseItems = combine(groupedTopicData, followTab) { grouped, tab ->
        when (tab) {
            FollowTab.TOPICS -> grouped.first
            FollowTab.SOURCES -> grouped.second
            FollowTab.KEYWORDS -> grouped.third
        }
    }

    private val queriedItems = combine(baseItems, query) { base, keyword ->
        base.filter { keyword.isBlank() || it.name.contains(keyword, ignoreCase = true) }
    }

    private val mutedDecorated = combine(queriedItems, mutedIds) { items, muted ->
        items.map { it.copy(muted = muted.contains(it.id)) }
    }

    val uiState: StateFlow<UiState<List<FollowTopicUiModel>>> = combine(mutedDecorated, blockedIds) { items, blocked ->
        val decorated = items
            .map { it.copy(blocked = blocked.contains(it.id)) }
            .filterNot { it.blocked }

        if (decorated.isEmpty()) UiState.Empty else UiState.Success(decorated)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading
    )

    init {
        observeTopics()
    }

    fun toggleTopic(item: FollowTopicUiModel) {
        viewModelScope.launch {
            if (item.type == "topic") {
                profileRepository.toggleFollowTopic(item)
            } else {
                toggleLocalFollow(item)
            }
        }
    }

    fun setTab(tab: FollowTab) {
        followTab.value = tab
    }

    fun setQuery(value: String) {
        query.value = value.trim()
    }

    fun toggleMute(item: FollowTopicUiModel) {
        mutedIds.value = mutedIds.value.toMutableSet().also {
            if (it.contains(item.id)) it.remove(item.id) else it.add(item.id)
        }
    }

    fun blockTopic(item: FollowTopicUiModel) {
        blockedIds.value = blockedIds.value + item.id
    }

    fun resetRecommendations() {
        viewModelScope.launch {
            blockedIds.value = emptySet()
            mutedIds.value = emptySet()
            profileRepository.resetFollowingTopics()
            sourceItems.value = sourceItems.value.map { it.copy(isFollowed = false) }
            keywordItems.value = keywordItems.value.map { it.copy(isFollowed = false) }
        }
    }

    private fun observeTopics() {
        viewModelScope.launch {
            profileRepository.bootstrap()
            profileRepository.observeFollowTopics().collect { topics ->
                allTopics.value = topics
            }
        }
    }

    private fun toggleLocalFollow(item: FollowTopicUiModel) {
        if (item.type == "source") {
            sourceItems.value = sourceItems.value.map {
                if (it.id == item.id) it.copy(isFollowed = !it.isFollowed) else it
            }
        } else {
            keywordItems.value = keywordItems.value.map {
                if (it.id == item.id) it.copy(isFollowed = !it.isFollowed) else it
            }
        }
    }
}

