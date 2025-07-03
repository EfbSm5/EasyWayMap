package com.efbsm5.easyway.contract

import com.efbsm5.easyway.data.models.assistModel.PointCommentAndUser
import com.efbsm5.easyway.state.IUiEffect
import com.efbsm5.easyway.state.IUiEvent
import com.efbsm5.easyway.state.IUiState

class CommunityContract {
    sealed class Event : IUiEvent {
        object Loading : Event()
        object Loaded : Event()
    }

    data class State(
        val searchContent: String,
        val poiItems: List<PointCommentAndUser>?,
        val tab: Int
    ) : IUiState

    sealed class Effect : IUiEffect {
        internal data class Toast(val msg: String?) : Effect()
    }
}