package com.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.local.dao.UserDao
import com.data.local.entity.UserEntity
import com.data.settings.AuthProvider
import com.data.settings.UserSettings
import com.data.settings.UserSettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userSettingsRepository: UserSettingsRepository,
    private val userDao: UserDao
) : ViewModel() {

    val settings: StateFlow<UserSettings> = userSettingsRepository.userSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserSettings()
        )

    val currentUser: StateFlow<UserEntity?> = userDao.observeSignedInUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun setDarkModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userSettingsRepository.setDarkModeEnabled(enabled)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userSettingsRepository.setNotificationsEnabled(enabled)
        }
    }

    fun setLanguage(languageCode: String) {
        viewModelScope.launch {
            userSettingsRepository.setLanguage(languageCode)
        }
    }

    fun setTextSize(textSize: String) {
        viewModelScope.launch {
            userSettingsRepository.setTextSize(textSize)
        }
    }

    fun setDataSaverEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userSettingsRepository.setDataSaverEnabled(enabled)
        }
    }

    fun setTrackReadingHistory(enabled: Boolean) {
        viewModelScope.launch {
            userSettingsRepository.setTrackReadingHistory(enabled)
        }
    }

    fun setPersonalizationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userSettingsRepository.setPersonalizationEnabled(enabled)
        }
    }

    fun setRegionCountry(countryCode: String) {
        viewModelScope.launch {
            userSettingsRepository.setRegionCountry(countryCode)
        }
    }

    fun setDefaultStartTab(tab: String) {
        viewModelScope.launch {
            userSettingsRepository.setDefaultStartTab(tab)
        }
    }

    fun setArticleStyle(style: String) {
        viewModelScope.launch {
            userSettingsRepository.setArticleStyle(style)
        }
    }

    fun setBreakingNewsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userSettingsRepository.setBreakingNewsEnabled(enabled)
        }
    }

    fun setDailyDigestEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userSettingsRepository.setDailyDigestEnabled(enabled)
        }
    }

    fun setSyncHistoryEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userSettingsRepository.setSyncHistoryEnabled(enabled)
        }
    }

    fun clearLocalAccountData() {
        viewModelScope.launch {
            userSettingsRepository.clearLocalAccountData()
        }
    }

    fun updateProfile(
        displayName: String,
        email: String,
        avatarUrl: String,
        occupation: String,
        location: String,
        birthday: String,
        bio: String,
        interests: String
    ) {
        viewModelScope.launch {
            userSettingsRepository.updateProfile(
                displayName = displayName,
                email = email,
                avatarUrl = avatarUrl,
                occupation = occupation,
                location = location,
                birthday = birthday,
                bio = bio,
                interests = interests
            )
        }
    }

    fun signInWithProvider(provider: AuthProvider, phone: String? = null) {
        viewModelScope.launch {
            userSettingsRepository.signInWithProvider(provider, phone)
        }
    }

    fun linkProvider(provider: AuthProvider) {
        viewModelScope.launch {
            userSettingsRepository.linkProvider(provider)
        }
    }

    fun unlinkProvider(provider: AuthProvider) {
        viewModelScope.launch {
            userSettingsRepository.unlinkProvider(provider)
        }
    }

    fun logout() {
        viewModelScope.launch {
            userSettingsRepository.logout()
        }
    }

    fun setAuthType(authType: String) {
        viewModelScope.launch {
            userSettingsRepository.setAuthType(authType)
        }
    }
}