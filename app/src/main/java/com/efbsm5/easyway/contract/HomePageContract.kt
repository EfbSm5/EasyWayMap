package com.efbsm5.easyway.contract

import androidx.compose.runtime.Immutable
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.data.models.assistModel.PointWithComments
import com.efbsm5.easyway.data.models.assistModel.PostWithComments
import com.efbsm5.easyway.state.IUiEffect
import com.efbsm5.easyway.state.IUiEvent
import com.efbsm5.easyway.state.IUiState
import com.efbsm5.easyway.viewmodel.HomePageState

class HomePageContract {
    sealed class Event : IUiEvent {
        data class ChangeState(val state: HomePageState) : Event()
        data object UpdateData : Event()
    }

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val points: List<PointWithComments>,
        val post: List<PostWithComments>,
        val content: HomePageState,
        val user: User,
        val error: String? = null
    ) : IUiState

    sealed class Effect : IUiEffect {
        data class Toast(val msg: String) : Effect()
    }
}