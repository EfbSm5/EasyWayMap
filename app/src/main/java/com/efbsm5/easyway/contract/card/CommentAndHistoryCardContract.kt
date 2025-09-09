package com.efbsm5.easyway.contract.card

import androidx.compose.runtime.Immutable
import com.amap.api.maps.model.LatLng
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.assistModel.PointCommentAndUser
import com.efbsm5.easyway.getInitPoint
import com.efbsm5.easyway.model.ImmutableListWrapper
import com.efbsm5.easyway.state.IUiEffect
import com.efbsm5.easyway.state.IUiEvent
import com.efbsm5.easyway.state.IUiState
import com.efbsm5.easyway.viewmodel.cardViewmodel.CommentCardScreen

class CommentAndHistoryCardContract {
    sealed class Event : IUiEvent {
        data class ChangeComment(val commentContent: String) : Event()
        data class Navigate(val latLng: LatLng) : Event()
        data class SelectTab(val int: Int) : Event()
        data object PublishComment : Event()
        data object Update : Event()
        data class LikePoint(val boolean: Boolean) : Event()
        data class DislikePoint(val boolean: Boolean) : Event()
        data class LikeComment(val int: Int, val boolean: Boolean) : Event()
        data class DislikeComment(val int: Int, val boolean: Boolean) : Event()
        data class ChangeState(val commentCardScreen: CommentCardScreen) : Event()
    }

    @Immutable
    data class State(
        val state: CommentCardScreen = CommentCardScreen.Comment,
        val point: EasyPoint = getInitPoint(),
        val pointComments: ImmutableListWrapper<PointCommentAndUser> = ImmutableListWrapper(
            emptyList()
        ),
        val commentContent: String = "",
        val loading: Boolean = true,
    ) : IUiState

    sealed class Effect : IUiEffect {
        internal object Back : Effect()
        internal object Update : Effect()
        internal object Comment : Effect()

    }
}