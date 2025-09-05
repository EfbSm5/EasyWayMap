package com.efbsm5.easyway.ui.page.communityPage

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.showMsg
import com.efbsm5.easyway.viewmodel.pageViewmodel.DetailViewModel

@Composable
fun CommunityNav(back: () -> Unit) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "CommunitySquare",
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        composable("CommunitySquare") {
            CommunitySquarePage(
                back = back, onChangeState = {
                    navController.navigate("Detail")
                    navController.currentBackStackEntry?.savedStateHandle?.set("postAndUser", it)
                })
        }
        composable("Detail") { backStackEntry ->
            val data = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<PostAndUser>("postAndUser")
            if (data != null) {
                val viewModel: DetailViewModel = viewModel()
                DetailRoute(
                    onBack = { navController.navigate("CommunitySquare") }, postAndUser = data,
                    viewModel = viewModel
                )
            } else showMsg("error")
        }
        composable("New") {
            NewPostPage(
                back = { navController.navigate("CommunitySquare") }
            )
        }
    }


}