package com.efbsm5.easyway.viewmodel.pageViewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.efbsm5.easyway.data.UserManager
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.data.models.assistModel.PointCommentAndUser
import com.efbsm5.easyway.data.network.IntentRepository
import com.efbsm5.easyway.data.repository.DataRepository
import com.efbsm5.easyway.getInitUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomePageViewModel(
    val repository: DataRepository,
    val userManager: UserManager,
    val intentRepository: IntentRepository
) : ViewModel() {
    private val _user = MutableStateFlow(getInitUser())
    private val _points = MutableStateFlow(emptyList<EasyPoint>())
    private val _post = MutableStateFlow(emptyList<PointCommentAndUser>())
    private val _content = MutableStateFlow<HomePageState>(HomePageState.Main)
    val points: StateFlow<List<EasyPoint>> = _points
    val post: StateFlow<List<PointCommentAndUser>> = _post
    val content: StateFlow<HomePageState> = _content
    val user: StateFlow<User> = _user

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _user.value = repository.getUserById(userManager.userId)
        }
    }

    fun getUserPoint() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPointByUserId(_user.value.id).collect {
                _points.value = it
            }
        }
    }

    fun getUserPost() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllDynamicPosts().collect { dynamicPosts ->
                val list = emptyList<PointCommentAndUser>().toMutableList()
                dynamicPosts.forEach { post ->
                    repository.getCommentCount(post.commentId).collect {
                        list.add(
                            PointCommentAndUser(
                                dynamicPost = post,
                                user = repository.getUserById(post.userId),
                                commentCount = it,
                            )
                        )
                        _post.value = list.toList()
                    }
                }
            }
        }
    }

    fun editUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
//            repository.
        }
    }

    fun updateData() {
        viewModelScope.launch(Dispatchers.IO) {
            intentRepository.syncData()
        }
    }

    fun changeState(homePageState: HomePageState) {
        _content.value = homePageState
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
}