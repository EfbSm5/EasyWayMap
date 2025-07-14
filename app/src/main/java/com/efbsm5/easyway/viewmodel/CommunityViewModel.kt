package com.efbsm5.easyway.viewmodel

import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.CommunityContract
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.model.ImmutableListWrapper
import com.efbsm5.easyway.repo.CommunityRepository
import kotlinx.coroutines.Dispatchers

class CommunityViewModel :
    BaseViewModel<CommunityContract.Event, CommunityContract.State, CommunityContract.Effect>() {

    init {
        setEvent(CommunityContract.Event.Loading)
    }

    override fun createInitialState(): CommunityContract.State {

        return CommunityContract.State(
            searchContent = "",
            postItems = ImmutableListWrapper(emptyList()),
            tab = 0,
            isLoading = true,
            error = null
        )
    }

    override fun handleEvents(event: CommunityContract.Event) {
        when (event) {
            CommunityContract.Event.Loading -> {
                asyncLaunch(Dispatchers.IO) {
                    val posts = CommunityRepository.fetchPosts()
                    setState {
                        copy(
                            isLoading = false,
                            postItems = ImmutableListWrapper(posts)
                        )
                    }
                }
            }

            CommunityContract.Event.Click -> {

            }
        }
    }

    fun search(text: String) {

    }

    fun select(int: Int) {

    }

    fun selectPost(postAndUser: PostAndUser) {
        setEffect { CommunityContract.Effect.SelectedPost(postAndUser) }
    }

    fun back() {
        setEffect { CommunityContract.Effect.Back }
    }
}