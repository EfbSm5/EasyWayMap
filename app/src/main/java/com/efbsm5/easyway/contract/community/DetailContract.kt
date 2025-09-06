package com.efbsm5.easyway.contract.community

import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.data.models.assistModel.PostCommentAndUser
import com.efbsm5.easyway.state.IUiEffect
import com.efbsm5.easyway.state.IUiEvent
import com.efbsm5.easyway.state.IUiState

class DetailContract {
    sealed class Event : IUiEvent {
        data class Load(val postAndUser: PostAndUser) : Event()
        data class ChangeInput(val value: String) : Event()
        data object SendComment : Event()
        data object ToggleLikePost : Event()
        data class ToggleLikeComment(val index: Int) : Event()
        data class ToggleDisLikeComment(val index: Int) : Event()
        data class ShowInput(val boolean: Boolean) : Event()
    }

    data class State(
        val loading: Boolean = true,
        val user: User? = null,
        val post: Post? = null,
        val comments: List<PostCommentAndUser> = emptyList(),
        val input: String = "",
        val sending: Boolean = false,
        val showTextField: Boolean = false,
        val error: String? = null
    ) : IUiState

    sealed class Effect : IUiEffect {
        object Back : Effect()
        internal class Toast(val string: String) : Effect()
    }
}