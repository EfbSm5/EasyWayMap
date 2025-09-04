package com.efbsm5.easyway.contract

import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.assistModel.PointCommentAndUser
import com.efbsm5.easyway.model.ImmutableListWrapper
import com.efbsm5.easyway.state.IUiEffect
import com.efbsm5.easyway.state.IUiEvent
import com.efbsm5.easyway.state.IUiState
import com.efbsm5.easyway.viewmodel.componentsViewmodel.CommentCardScreen

class CommentAndHistoryCardContract {
    sealed class Event : IUiEvent {
        data class ChangeComment(val commentContent: String) : Event()
    }

    data class State(
        val state: CommentCardScreen,
        val point: EasyPoint,
        val pointComments: ImmutableListWrapper<PointCommentAndUser>,
        val commentContent: String
    ) : IUiState

    sealed class Effect : IUiEffect {
        internal object Back : Effect()
        internal object Update : Effect()
        internal object Comment : Effect()

    }
}