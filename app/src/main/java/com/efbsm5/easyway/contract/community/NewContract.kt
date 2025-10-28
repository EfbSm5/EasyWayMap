package com.efbsm5.easyway.contract.community

import android.net.Uri
import androidx.compose.runtime.Immutable
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.data.models.assistModel.PostCommentAndUser
import com.efbsm5.easyway.getInitPost
import com.efbsm5.easyway.state.IUiEffect
import com.efbsm5.easyway.state.IUiEvent
import com.efbsm5.easyway.state.IUiState

class NewContract {
    sealed class Event : IUiEvent {
        data class TabSelect(val int: Int) : Event()
        data class ClickPost(val postAndUser: PostAndUser) : Event()
        data class EditText(val string: String) : Event()
        data object Submit : Event()
        data class Load(val postAndUser: PostAndUser) : Event()
        data class ChangeInput(val value: String) : Event()
        data object SendComment : Event()
        data object ToggleLikePost : Event()
        data class ToggleLikeComment(val index: Int) : Event()
        data class ToggleDisLikeComment(val index: Int) : Event()
        data class ShowInput(val boolean: Boolean) : Event()
        data class EditTitle(val string: String) : Event()
        data class EditContent(val string: String) : Event()
        data class SelectedCategory(val int: Int) : Event()
        data class TitleChanged(val string: String) : Event()
        data object Publish : Event()
        data class PickPhotoDialogResult(val uri: Uri?) : Event()
    }

    @Immutable
    data class State(
        val filterPosts: List<PostAndUser> = emptyList(),
        val isRefreshing: Boolean = false,
        val selectedTab: Int = 0,
        val searchText: String = "",
        val loading: Boolean = true,
        val user: User? = null,
        val post: Post? = null,
        val comments: List<PostCommentAndUser> = emptyList(),
        val input: String = "",
        val sending: Boolean = false,
        val showTextField: Boolean = false,
        val error: String? = null,
        val newPost: Post = getInitPost(),
        val previewPhoto: Uri? = null,
        val onSelectedCategory: Int = 0
    ) : IUiState

    sealed class Effect : IUiEffect {
        internal data class SelectedPost(val postAndUser: PostAndUser) : Effect()
        internal object Back : Effect()
        internal class Toast(val string: String) : Effect()
        internal class Liked(val boolean: Boolean) : Effect()
        internal object GetPhoto : Effect()
        internal object GetLocation : Effect()
        internal object Upload : Effect()
    }

}