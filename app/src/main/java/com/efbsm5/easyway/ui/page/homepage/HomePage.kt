package com.efbsm5.easyway.ui.page.homepage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.efbsm5.easyway.contract.HomePageContract
import com.efbsm5.easyway.showMsg
import com.efbsm5.easyway.ui.components.melody.RedCenterLoading
import com.efbsm5.easyway.viewmodel.HomePageState
import com.efbsm5.easyway.viewmodel.HomePageViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun HomePage(viewModel: HomePageViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.onEach {
            when (it) {
                is HomePageContract.Effect.Toast -> showMsg(it.msg)
            }
        }.collect()
    }
    Box {
        when (state) {
            HomePageState.RegForActivity -> RegScreen()

            HomePageState.ShowPoint -> ShowPointScreen()

            HomePageState.ShowComment -> CommentPage()

            HomePageState.ShowPost -> {
                ShowPostPage(state.post)
            }

            HomePageState.Version -> VersionScreen()

            HomePageState.ShowVersionAndHelp -> InfoScreen()

            HomePageState.Settings -> SettingsScreen(changeState = {
                viewModel.handleEvents(
                    HomePageContract.Event.ChangeState(
                        it
                    )
                )
            })


            HomePageState.EditUser -> EditUserScreen()

            HomePageState.CommonSetting -> CommonSettingsScreen()
            HomePageState.InformSetting -> InformSettingScreen()
            HomePageState.Safety -> SafetyScreen()

            HomePageState.Declare -> DeclareScreen()

            HomePageState.Main -> MainPageScreen(state.user, changeState = {
                viewModel.handleEvents(
                    HomePageContract.Event.ChangeState(
                        it
                    )
                )
            })
        }
        if (state.isLoading) {
            RedCenterLoading()
        }
    }


    BackHandler(
        enabled = state != HomePageState.Main, onBack = {
            viewModel.handleEvents(HomePageContract.Event.ChangeState(HomePageState.Main))
        })

}





