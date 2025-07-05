package com.efbsm5.easyway.viewmodel

import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.CommunityContract
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.model.ImmutableListWrapper
import com.efbsm5.easyway.repo.CommunityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow

class CommunityViewModel :
    BaseViewModel<CommunityContract.Event, CommunityContract.State, CommunityContract.Effect>() {
    private var _showPosts =
        MutableStateFlow<ImmutableListWrapper<PostAndUser>>(ImmutableListWrapper(emptyList()))

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
                    _showPosts.value = CommunityRepository.fetchPosts()
                    setState {
                        copy(
                            isLoading = false,
                            postItems = _showPosts.value
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

}