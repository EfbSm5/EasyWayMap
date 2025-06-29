package com.efbsm5.easyway.ui.page.homepage

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.efbsm5.easyway.viewmodel.pageViewmodel.HomePageState
import com.efbsm5.easyway.viewmodel.pageViewmodel.HomePageViewModel

@Composable
fun HomePage(viewModel: HomePageViewModel) {
    val posts by viewModel.post.collectAsState()
    val user by viewModel.user.collectAsState()
    val state by viewModel.content.collectAsState()
    when (state) {
        HomePageState.RegForActivity -> RegScreen()

        HomePageState.ShowPoint -> ShowPointScreen()

        HomePageState.ShowComment -> CommentPage()

        HomePageState.ShowPost -> {
            viewModel.getUserPost()
            ShowPostPage(posts)
        }

        HomePageState.Version -> VersionScreen()

        HomePageState.ShowVersionAndHelp -> InfoScreen()

        HomePageState.Settings -> SettingsScreen(viewModel::changeState)


        HomePageState.EditUser -> EditUserScreen()

        HomePageState.CommonSetting -> CommonSettingsScreen()
        HomePageState.InformSetting -> InformSettingScreen()
        HomePageState.Safety -> SafetyScreen()

        HomePageState.Declare -> DeclareScreen()

        HomePageState.Main -> MainPageScreen(user, viewModel::changeState)
    }
    BackHandler(
        enabled = state != HomePageState.Main, onBack = {
            viewModel.changeState(HomePageState.Main)
        })

}





