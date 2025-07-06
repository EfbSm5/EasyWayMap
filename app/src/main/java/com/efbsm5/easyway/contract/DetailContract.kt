package com.efbsm5.easyway.contract

import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.data.models.assistModel.PostCommentAndUser
import com.efbsm5.easyway.model.ImmutableListWrapper
import com.efbsm5.easyway.state.IUiEffect
import com.efbsm5.easyway.state.IUiEvent
import com.efbsm5.easyway.state.IUiState

class DetailContract {
    sealed class Event : IUiEvent {
        object Loading : Event()
        class Editing(val string: String) : Event()
        object Loaded : Event()
    }

    data class State(
        val post: Post,
        val user: User,
        val comments: ImmutableListWrapper<PostCommentAndUser>,
        val commentString: String?,
        val showTextField: Boolean,

        val error: String?
    ) : IUiState

    sealed class Effect : IUiEffect
}