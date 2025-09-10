package com.efbsm5.easyway.viewmodel.communityViewModel

import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.community.CommunityContract
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import kotlinx.coroutines.Dispatchers

class CommunityViewModel :
    BaseViewModel<CommunityContract.Event, CommunityContract.State, CommunityContract.Effect>() {
    override fun createInitialState(): CommunityContract.State = CommunityContract.State()


    override fun handleEvents(event: CommunityContract.Event) {
        when (event) {

            is CommunityContract.Event.ClickPost -> {
                setEffect { CommunityContract.Effect.SelectedPost(event.postAndUser) }
            }

            is CommunityContract.Event.EditText -> {
                setState { copy(searchText = event.string) }
            }

            CommunityContract.Event.Submit -> submit()
            is CommunityContract.Event.TabSelect -> {
                setState { copy(selectedTab = event.int) }
            }
        }
    }

    fun updateSingle(updated: PostAndUser, insertIfMissing: Boolean = false) {
        setState {
            copy(filterPosts = change(updated = updated, insertIfMissing = insertIfMissing))
        }
    }

    fun selectPost(posts: List<PostAndUser>) {
        setState { copy(filterPosts = posts) }
    }

    private fun submit() {
        asyncLaunch(Dispatchers.IO) {

        }
    }


    private fun change(updated: PostAndUser, insertIfMissing: Boolean = false): List<PostAndUser> {
        val old = currentState.filterPosts
        val idx = old.indexOfFirst { it.post.id == updated.post.id }
        if (idx == -1) {
            if (!insertIfMissing) return old
            return listOf(updated) + old
        }
        // 不做无变化替换
        val current = old[idx]
        if (current == updated) return old
        val list = old.toMutableList()
        list[idx] = updated
        return list
    }

    fun back() {
        setEffect { CommunityContract.Effect.Back }
    }
}