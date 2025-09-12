package com.efbsm5.easyway.viewmodel

import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.HomePageContract
import com.efbsm5.easyway.data.UserManager
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.data.network.IntentRepository
import com.efbsm5.easyway.getInitUser
import com.efbsm5.easyway.repo.DataRepository
import kotlinx.coroutines.Dispatchers

class HomePageViewModel :
    BaseViewModel<HomePageContract.Event, HomePageContract.State, HomePageContract.Effect>() {

    init {
        getUser()
        getUserPoint()
        getUserPost()
    }

    fun getUser() {
        asyncLaunch(Dispatchers.IO) {
            val r = DataRepository.getUserById(UserManager.userId)
            r.onSuccess {
                setState { copy(user = it, isLoading = false) }
            }.onFailure {
                setState { copy(isLoading = false, error = it.message) }
            }
        }
    }

    fun getUserPoint() {
        asyncLaunch(Dispatchers.IO) {
            val r = DataRepository.getPointAndCommentByUserId(UserManager.userId)
            r.onSuccess {
                setState { copy(points = it, isLoading = false) }
            }.onFailure {
                setState { copy(isLoading = false, error = it.message) }
            }
        }
    }

    fun getUserPost() {
        asyncLaunch(Dispatchers.IO) {
            val r = DataRepository.getPostAndCommentsByUserId(currentState.user.id)
            r.onSuccess {
                setState { copy(isLoading = false, post = it) }
            }.onFailure {
                setState { copy(isLoading = false, error = it.message) }
            }
        }
    }

    fun editUser(user: User) {
        asyncLaunch(Dispatchers.IO) {

        }
    }

    fun updateData() {
        asyncLaunch(Dispatchers.IO) {
            IntentRepository.syncData()
        }
    }

    private fun changeState(homePageState: HomePageState) {
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
        when (event) {
            is HomePageContract.Event.ChangeState -> changeState(event.state)
            HomePageContract.Event.UpdateData -> updateData()
        }
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