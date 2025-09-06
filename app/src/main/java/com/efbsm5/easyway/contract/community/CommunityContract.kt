package com.efbsm5.easyway.contract.community

import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.state.IUiEffect
import com.efbsm5.easyway.state.IUiEvent
import com.efbsm5.easyway.state.IUiState

class CommunityContract {
    sealed class Event : IUiEvent {
        data object Loading : Event()
        data class TabSelect(val int: Int) : Event()
        data class ClickPost(val postAndUser: PostAndUser) : Event()
        data class EditText(val string: String) : Event()
        data object Submit : Event()
    }

    data class State(
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val error: String? = null,
        val rawPosts: List<PostAndUser> = emptyList(),
        val filteredPosts: List<PostAndUser> = emptyList(), // 根据 tab / 搜索
        val selectedTab: Int = 0,
        val searchText: String = ""
    ) : IUiState

    sealed class Effect : IUiEffect {
        internal data class SelectedPost(val postAndUser: PostAndUser) : Effect()
        internal object Back : Effect()
    }
}