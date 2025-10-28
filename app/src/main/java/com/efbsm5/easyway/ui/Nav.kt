package com.efbsm5.easyway.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.ui.components.AppTopBar
import com.efbsm5.easyway.ui.components.BottomNavigationBar
import com.efbsm5.easyway.ui.components.FloatingButton
import com.efbsm5.easyway.ui.page.communityPage.CommunitySquareRoute
import com.efbsm5.easyway.ui.page.communityPage.DetailRoute
import com.efbsm5.easyway.ui.page.communityPage.NewPostPage
import com.efbsm5.easyway.ui.page.homepage.HomePage
import com.efbsm5.easyway.ui.page.map.MapRoutePage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EasyWay() {
    val scaffoldController = remember { ScaffoldController() }
    val snackBarHostState = remember { SnackbarHostState() }
    val backStack = remember { mutableStateListOf<Route>(Route.MapRoute) }
    CompositionLocalProvider(LocalScaffoldController provides scaffoldController) {

        val fabConfig = scaffoldController.fabConfig
        val topBarConfig = scaffoldController.topBarConfig
//        val bottomBarConfig = scaffoldController.bottomBarConfig

        Scaffold(snackbarHost = { SnackbarHost(snackBarHostState) }, topBar = {
            if (topBarConfig.show && topBarConfig.title != null) {
                AppTopBar(
                    title = topBarConfig.title, onBack = topBarConfig.back,
                )
            }
        }, bottomBar = {
//            if (bottomBarConfig.show) {
//                bottomBarConfig.content?.invoke()
//            }
            BottomNavigationBar(
                now = backStack.last(), navigate = {}, refresh = {}
            )
        }, floatingActionButton = {
            FloatingButton(fabConfig)
        }, content = { innerPadding ->
            NavDisplay(
                modifier = Modifier.padding(innerPadding),
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = { key ->
                    when (key) {
                        is Route.MapRoute -> NavEntry(key) {
                            MapRoutePage()
                        }


                        is Route.Home -> NavEntry(key) {
                            HomePage()
                        }

                        is Route.Post -> NavEntry(key) {
                            val innerBackStack =
                                remember { mutableStateListOf<PostRoute>(PostRoute.Square) }
                            NavDisplay(
                                modifier = Modifier.fillMaxSize(),
                                backStack = innerBackStack,
                                onBack = {},
                                entryProvider = { key ->
                                    when (key) {
                                        is PostRoute.Detail -> NavEntry(key) {
                                            DetailRoute(
                                                postAndUser = key.post, onBack = {}, viewModel =
                                            )
                                        }

                                        PostRoute.NewPost -> NavEntry(key) {
                                            NewPostPage(

                                            )
                                        }

                                        PostRoute.Square -> NavEntry(key) {
                                            CommunitySquareRoute(
                                                back = TODO(),
                                                onSelectPost = TODO(),
                                                onCreateNew = TODO(),
                                                viewModel = TODO()
                                            )
                                        }
                                    }
                                })

                        }
                    }
                })
        })

    }
}


sealed class Route : NavKey {
    data object MapRoute : Route()
    data object Home : Route()
    data object Post : Route()
}

sealed class PostRoute : NavKey {
    object Square : PostRoute()
    data object NewPost : PostRoute()
    data class Detail(val post: PostAndUser) : PostRoute()
}


