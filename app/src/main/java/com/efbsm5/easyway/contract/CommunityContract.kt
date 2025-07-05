package com.efbsm5.easyway.contract

import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.model.ImmutableListWrapper
import com.efbsm5.easyway.state.IUiEffect
import com.efbsm5.easyway.state.IUiEvent
import com.efbsm5.easyway.state.IUiState

class CommunityContract {
    sealed class Event : IUiEvent {
        object Loading : Event()
        object Click : Event()
    }

    data class State(
        val searchContent: String,
        val postItems: ImmutableListWrapper<PostAndUser>,
        val tab: Int,
        val isLoading: Boolean,
        val error: String?
    ) : IUiState

    sealed class Effect : IUiEffect
}