package com.efbsm5.easyway.viewmodel

import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.HomePageContract
import com.efbsm5.easyway.getInitUser
import com.efbsm5.easyway.repo.HomePageRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@OptIn(ExperimentalCoroutinesApi::class)
class HomePageViewModel(
    private val repository: HomePageRepository,
) :
    BaseViewModel<HomePageContract.Event, HomePageContract.State, HomePageContract.Effect>() {

    init {
        asyncLaunch {
            repository.userIds
                .distinctUntilChanged()
                .flatMapLatest { userId ->
                    repository.observeHomePage(userId)
                        .map { snapshot -> Result.success(snapshot) }
                        .catch { throwable -> emit(Result.failure(throwable)) }
                        .onStart {
                            setState { copy(isLoading = true, error = null) }
                        }
                }
                .collect { result ->
                    result.onSuccess { snapshot ->
                        setState {
                            copy(
                                isLoading = false,
                                user = snapshot.user,
                                points = snapshot.points,
                                post = snapshot.posts,
                                error = null,
                            )
                        }
                    }.onFailure { throwable ->
                        setState {
                            copy(
                                isLoading = false,
                                error = throwable.message?.takeIf { it.isNotBlank() }
                                    ?: "个人页数据加载失败",
                            )
                        }
                    }
                }
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
