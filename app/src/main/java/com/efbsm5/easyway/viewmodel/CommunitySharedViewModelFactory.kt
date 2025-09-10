package com.efbsm5.easyway.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.efbsm5.easyway.repo.CommunityRepository

class CommunitySharedViewModelFactory(
    private val repo: CommunityRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val handle = extras.createSavedStateHandle()
        return CommunitySharedViewModel(repo, handle) as T
    }
}
