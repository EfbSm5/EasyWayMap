package com.efbsm5.easyway.contract.community

import androidx.compose.runtime.Immutable
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.state.IUiEffect
import com.efbsm5.easyway.state.IUiEvent
import com.efbsm5.easyway.state.IUiState

class CommunityContract {
    sealed class Event : IUiEvent {
        data class TabSelect(val int: Int) : Event()
        data class ClickPost(val postAndUser: PostAndUser) : Event()
        data class EditText(val string: String) : Event()
        data object Submit : Event()
    }

    @Immutable
    data class State(
        val filterPosts: List<PostAndUser> = emptyList(),
        val error: String? = null,
        val isRefreshing: Boolean = false,
        val selectedTab: Int = 0,
        val searchText: String = ""
    ) : IUiState

    sealed class Effect : IUiEffect {
        internal data class SelectedPost(val postAndUser: PostAndUser) : Effect()
        internal object Back : Effect()
    }
}