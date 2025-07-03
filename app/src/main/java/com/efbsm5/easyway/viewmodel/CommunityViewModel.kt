package com.efbsm5.easyway.viewmodel

import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.CommunityContract
import com.efbsm5.easyway.data.models.assistModel.PointCommentAndUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CommunityViewModel :
    BaseViewModel<CommunityContract.Event, CommunityContract.State, CommunityContract.Effect>() {
    private var _showPosts = MutableStateFlow<List<PointCommentAndUser>>(emptyList())
    val posts: StateFlow<List<PointCommentAndUser>> = _showPosts

    init {
        fetchPosts()
    }

    override fun createInitialState(): CommunityContract.State {
        return CommunityContract.State(
            searchContent = "",
            poiItems = emptyList()
        )
    }

    override fun handleEvents(event: CommunityContract.Event) {
        when (event) {
            CommunityContract.Event.Loaded -> TODO()
            CommunityContract.Event.Loading -> {
                
            }
        }
        TODO("Not yet implemented")
    }

}