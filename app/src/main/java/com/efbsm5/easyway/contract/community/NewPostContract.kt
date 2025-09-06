package com.efbsm5.easyway.contract.community

import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.state.IUiEffect
import com.efbsm5.easyway.state.IUiEvent
import com.efbsm5.easyway.state.IUiState

class NewPostContract {
    sealed class Event : IUiEvent {
        object Loading : Event()
        class EditTitle(val string: String) : Event()
        class EditContent(val string: String) : Event()
        class ChangeDialogData(val data: String) : Event()

        object Upload : Effect()

    }

    data class State(
        val post: Post,
        val dialogData: String?,
        val error: String?
    ) : IUiState

    sealed class Effect : IUiEffect {
        object GetPhoto : Effect()
        object GetLocation : Effect()
        object Back : Effect()
    }
}