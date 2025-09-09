package com.efbsm5.easyway.contract.community

import android.net.Uri
import androidx.compose.runtime.Immutable
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.getInitPost
import com.efbsm5.easyway.state.IUiEffect
import com.efbsm5.easyway.state.IUiEvent
import com.efbsm5.easyway.state.IUiState

class NewPostContract {
    sealed class Event : IUiEvent {
        data object Loading : Event()
        data class EditTitle(val string: String) : Event()
        data class EditContent(val string: String) : Event()
        data class ChangeDialogData(val data: String) : Event()

        data class SelectedCategory(val int: Int) : Event()
        data class TitleChanged(val string: String) : Event()
        data object Publish : Event()
        data class PickPhotoDialogResult(val uri: Uri?) : Event()

    }

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val post: Post = getInitPost(),
        val dialogData: String? = null,
        val error: String? = null,
        val previewPhoto: Uri? = null,
        val onSelectedCategory: Int = 0
    ) : IUiState

    sealed class Effect : IUiEffect {
        object GetPhoto : Effect()
        object GetLocation : Effect()
        object Back : Effect()
        object Upload : Effect()
    }
}