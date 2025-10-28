package com.efbsm5.easyway.ui

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.ui.components.AppTopBar
import com.efbsm5.easyway.ui.components.FloatingButton
import com.efbsm5.easyway.ui.page.communityPage.CommunitySquareRoute
import com.efbsm5.easyway.ui.page.communityPage.DetailRoute
import com.efbsm5.easyway.ui.page.communityPage.NewPostPage
import com.efbsm5.easyway.ui.page.homepage.HomePage
import com.efbsm5.easyway.ui.page.map.MapRoutePage
import com.efbsm5.easyway.viewmodel.communityViewModel.CommunityViewModel
import com.efbsm5.easyway.viewmodel.communityViewModel.DetailViewModel
import com.efbsm5.easyway.viewmodel.communityViewModel.NewPostViewModel

//@SuppressLint("UnrememberedGetBackStackEntry")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun EasyWay() {
//    val navController = rememberNavController()
//    val nav = navController.currentBackStackEntryAsState().value?.destination
//    val scaffoldController = remember { ScaffoldController() }
//    val snackBarHostState = remember { SnackbarHostState() }
//    LaunchedEffect(nav?.route) {
//        scaffoldController.refresh()
//    }
//
//    CompositionLocalProvider(LocalScaffoldController provides scaffoldController) {
//
//        val fabConfig = scaffoldController.fabConfig
//        val topBarConfig = scaffoldController.topBarConfig
////        val bottomBarConfig = scaffoldController.bottomBarConfig
//
//        Scaffold(snackbarHost = { SnackbarHost(snackBarHostState) }, topBar = {
//            if (topBarConfig.show && topBarConfig.title != null) {
//                AppTopBar(
//                    title = topBarConfig.title, onBack = topBarConfig.back,
//                )
//            }
//        }, bottomBar = {
////            if (bottomBarConfig.show) {
////                bottomBarConfig.content?.invoke()
////            }
//            BottomNavigationBar(
//                navController = navController, currentDestination = nav
//            )
//        }, floatingActionButton = {
//            FloatingButton(fabConfig)
//        }) { innerPadding ->
//            NavHost(
//                navController = navController,
//                startDestination = RootRoute.Map.route,
//                modifier = Modifier
//                    .padding(innerPadding)
//                    .fillMaxSize()
//            ) {
//                composable(RootRoute.Map.route) {
//                    MapRoutePage()
//                }
//                composable(RootRoute.Home.route) {
//                    HomePage()
//                }
//                navigation(
//                    startDestination = CommunityRoute.Square.route,
//                    route = RootRoute.CommunityGraph.route
//                ) {
//                    composable(CommunityRoute.Square.route) { squareEntry ->
//                        val shared = rememberCommunitySharedViewModel(navController)
//                        val viewModel: CommunityViewModel = viewModel(squareEntry)
//                        LaunchedEffect(Unit) {
//                            shared.loadInitialIfEmpty()
//                        }
//                        val posts by shared.posts.collectAsState()
//                        val loading by shared.loading.collectAsState()
//
//                        CommunitySquareRoute(
//                            back = { navController.popBackStack() },
//                            onSelectPost = { pu ->
//                                shared.select(pu)
//                                navController.navigate(CommunityRoute.Detail)
//                            },
//                            onCreateNew = { navController.navigate(CommunityRoute.NewPost.route) },
//                            viewModel = viewModel,
//                            posts = posts,
//                            loading = loading
//                        )
//                    }
//
//                    composable(
//                        route = CommunityRoute.Detail.route,
//                    ) { detailEntry ->
//                        val shared = rememberCommunitySharedViewModel(navController)
//                        val viewModel: DetailViewModel = viewModel()
//                        val current by shared.currentPost.collectAsState()
//                        DetailRoute(
//                            onBack = { navController.popBackStack() },
//                            postAndUser = current!!,
//                            viewModel = viewModel,
//                            onLikeUpdated = { updated ->
//                                shared.updateLike(
//                                    updated.post.id, updated.post.likedByMe, updated.post.like
//                                )
//                            })
//                    }
//
//                    composable(CommunityRoute.NewPost.route) { newEntry ->
//                        val shared = rememberCommunitySharedViewModel(navController)
//                        val viewModel: NewPostViewModel = viewModel(newEntry)
//
//                        NewPostPage(
//                            back = { navController.popBackStack() },
//                            onPostSuccess = { newPost ->
//                                shared.insertNewPost(newPost)
//                                navController.popBackStack()
//                            },
//                            viewModel = viewModel
//                        )
//                    }
//                }
//
//            }
//        }
//    }
//}


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

        Scaffold(
            snackbarHost = { SnackbarHost(snackBarHostState) },
            topBar = {
                if (topBarConfig.show && topBarConfig.title != null) {
                    AppTopBar(
                        title = topBarConfig.title, onBack = topBarConfig.back,
                    )
                }
            },
            bottomBar = {
//            if (bottomBarConfig.show) {
//                bottomBarConfig.content?.invoke()
//            }
//                BottomNavigationBar(
//
//                )
            }, floatingActionButton = {
                FloatingButton(fabConfig)
            },
            content = { innerPadding ->
                NavDisplay(
                    modifier = Modifier.padding(innerPadding),
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = { key ->
                        when (key) {
                            is Route.MapRoute -> NavEntry(key) {
                                MapRoutePage()
                            }

                            is Route.Square -> NavEntry(key) {
                                val viewModel: CommunityViewModel = viewModel()
                                CommunitySquareRoute(
                                    back = { },
                                    onSelectPost = {},
                                    onCreateNew = { },
                                    viewModel = viewModel,
                                )
                            }

                            is Route.Home -> NavEntry(key) {
                                HomePage()
                            }

                            is Route.Detail -> NavEntry(key) {
                                val viewModel: DetailViewModel = viewModel()
                                DetailRoute(
                                    onBack = { },
                                    postAndUser = key.post,
                                    viewModel = viewModel,
                                    onLikeUpdated = { updated ->
                                        shared.updateLike(
                                            updated.post.id,
                                            updated.post.likedByMe,
                                            updated.post.like
                                        )
                                    })
                            }

                            Route.NewPost -> NavEntry(key) {
                                val viewModel: NewPostViewModel = viewModel()
                                NewPostPage(
                                    back = {},
                                    onPostSuccess = {},
                                    viewModel = viewModel
                                )
                            }
                        }
                    })
            }

    }
}


sealed class Route : NavKey {
    data object MapRoute : Route()
    data object Home : Route()
    data object Square : Route()
    data object NewPost : Route()
    data class Detail(val post: PostAndUser) : Route()
}


