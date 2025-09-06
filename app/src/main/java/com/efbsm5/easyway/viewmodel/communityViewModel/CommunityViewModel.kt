package com.efbsm5.easyway.viewmodel.communityViewModel

import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.community.CommunityContract
import com.efbsm5.easyway.model.ImmutableListWrapper
import com.efbsm5.easyway.repo.CommunityRepository
import kotlinx.coroutines.Dispatchers

class CommunityViewModel :
    BaseViewModel<CommunityContract.Event, CommunityContract.State, CommunityContract.Effect>() {

    init {
        setEvent(CommunityContract.Event.Loading)
    }

    override fun createInitialState(): CommunityContract.State = CommunityContract.State()


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

            is CommunityContract.Event.ClickPost -> TODO()
            is CommunityContract.Event.EditText -> TODO()
            CommunityContract.Event.Submit -> TODO()
            is CommunityContract.Event.TabSelect -> TODO()
        }
    }

    fun onEvent(event: CommunityContract.Event) {
        setEvent(event)
    }

    fun back() {
        setEffect { CommunityContract.Effect.Back }
    }
}