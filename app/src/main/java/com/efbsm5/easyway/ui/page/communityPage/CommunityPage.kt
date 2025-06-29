package com.efbsm5.easyway.ui.page.communityPage

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.efbsm5.easyway.data.models.DynamicPost
import com.efbsm5.easyway.viewmodel.pageViewmodel.DetailPageViewModel
import com.efbsm5.easyway.viewmodel.pageViewmodel.NewPostPageViewModel
import com.efbsm5.easyway.viewmodel.pageViewmodel.ShowPageViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CommunityPage(back: () -> Unit) {
    var state: State by remember { mutableStateOf(State.Community) }
    Crossfade(targetState = state, modifier = Modifier.fillMaxSize()) { nowState ->
        when (nowState) {
            State.Community -> {
                val showPageViewModel: ShowPageViewModel = koinViewModel()
                ShowPage(
                    onChangeState = { state = it }, viewModel = showPageViewModel, back = back
                )
            }

            is State.Detail -> {
                val detailPageViewModel: DetailPageViewModel =
                    koinViewModel(parameters = { parametersOf(nowState.dynamicPost) })
                DetailPage(
                    onBack = { state = State.Community }, viewModel = detailPageViewModel
                )
            }

            State.NewPost -> {
                val newPostPageViewModel: NewPostPageViewModel = koinViewModel()
                NewDynamicPostPage(
                    back = { state = State.Community }, viewModel = newPostPageViewModel
                )
            }
        }
    }

}


sealed interface State {
    data object Community : State
    data object NewPost : State
    data class Detail(val dynamicPost: DynamicPost) : State
}