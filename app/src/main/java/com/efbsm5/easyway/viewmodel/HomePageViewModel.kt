package com.efbsm5.easyway.viewmodel

import androidx.lifecycle.viewModelScope
import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.HomePageContract
import com.efbsm5.easyway.data.UserManager
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.getInitUser
import com.efbsm5.easyway.repo.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomePageViewModel :
    BaseViewModel<HomePageContract.Event, HomePageContract.State, HomePageContract.Effect>() {

    init {
        asyncLaunch(Dispatchers.IO) {
            val r = DataRepository.getUserById(UserManager.userId)
            r.onSuccess {
                setState { copy(user = it) }
            }.onFailure {
                setState { copy(user = getInitUser()) }
            }
        }
    }


    fun getUserPoint() {
        asyncLaunch(Dispatchers.IO) {
//            DataRepository.getPointByUserId()
//            repository.getPointByUserId(_user.value.id).collect {
//                _points.value = it
//            }
        }
    }

    fun getUserPost() {
        asyncLaunch(Dispatchers.IO) {
//            repository.getAllDynamicPosts().collect { dynamicPosts ->
//                val list = emptyList<PointCommentAndUser>().toMutableList()
//                dynamicPosts.forEach { post ->
//                    repository.getCommentCount(post.commentId).collect {
//                        list.add(
//                            PointCommentAndUser(
//                                dynamicPost = post,
//                                user = repository.getUserById(post.userId),
//                                commentCount = it,
//                            )
//                        )
//                        _post.value = list.toList()
//                    }
//                }
//            }
        }
    }

    fun editUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
//            repository.
        }
    }

    fun updateData() {
        asyncLaunch(Dispatchers.IO) {
//            intentRepository.syncData()
        }
    }

    fun changeState(homePageState: HomePageState) {
        setState { copy(content = homePageState) }
    }

    override fun createInitialState(): HomePageContract.State {
        return HomePageContract.State(
            points = emptyList(),
            post = emptyList(),
            content = HomePageState.Main,
            user = getInitUser()
        )
    }

    override fun handleEvents(event: HomePageContract.Event) {
    }
}

sealed interface HomePageState {
    data object Main : HomePageState
    data object ShowPost : HomePageState
    data object ShowPoint : HomePageState
    data object ShowComment : HomePageState
    data object EditUser : HomePageState
    data object RegForActivity : HomePageState
    data object Version : HomePageState
    data object ShowVersionAndHelp : HomePageState
    data object Settings : HomePageState
    data object Safety : HomePageState
    data object Declare : HomePageState
    data object CommonSetting : HomePageState
    data object InformSetting : HomePageState
    data object Loading : HomePageState
}