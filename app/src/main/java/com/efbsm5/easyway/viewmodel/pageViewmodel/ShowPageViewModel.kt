package com.efbsm5.easyway.viewmodel.pageViewmodel

import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.ShowPageContract
import com.efbsm5.easyway.getInitUser
import com.efbsm5.easyway.model.ImmutableListWrapper

class ShowPageViewModel :
    BaseViewModel<ShowPageContract.Event, ShowPageContract.State, ShowPageContract.Effect>() {

    private fun fetchPosts() {
//        asyncLaunch(Dispatchers.IO) {
//            DataRepository.getAllPosts().forEach { post ->
//                DataRepository.g(post.commentId).collect {
//                    list.add(
//                        PointCommentAndUser(
//                            pointComment = TODO(),
//                            user = TODO()
//                        )
//                    )
//                    allPosts.value = list.toList()
//                }
//                emptyList<PointCommentAndUser>().toMutableList()
//            }
//        }

    }

    fun changeTab(int: Int) {

    }

    fun search(string: String) {

    }

    override fun createInitialState(): ShowPageContract.State {
        return ShowPageContract.State(
            points = ImmutableListWrapper(emptyList()),
            post = ImmutableListWrapper(emptyList()),
            content = HomePageState.Loading,
            user = getInitUser()
        )
    }

    override fun handleEvents(event: ShowPageContract.Event) {
        TODO("Not yet implemented")
    }
}

