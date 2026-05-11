package com.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.data.repository.NewsRepository
import com.data.repository.ProfileRepository

class HomeViewModelFactory(
    private val repository: NewsRepository,
    private val profileRepository: ProfileRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository, profileRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}