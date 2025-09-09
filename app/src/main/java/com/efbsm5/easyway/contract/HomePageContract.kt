package com.efbsm5.easyway.contract

import androidx.compose.runtime.Immutable
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.data.models.assistModel.PointCommentAndUser
import com.efbsm5.easyway.state.IUiEffect
import com.efbsm5.easyway.state.IUiEvent
import com.efbsm5.easyway.state.IUiState
import com.efbsm5.easyway.viewmodel.HomePageState

class HomePageContract {
    sealed class Event : IUiEvent

    @Immutable
    data class State(
        val points: List<EasyPoint>,
        val post: List<PointCommentAndUser>,
        val content: HomePageState,
        val user: User
    ) : IUiState

    sealed class Effect : IUiEffect
}