package com.efbsm5.easyway.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.ui.components.AppTopBar
import com.efbsm5.easyway.ui.components.BottomNavigationBar
import com.efbsm5.easyway.ui.page.communityPage.CommunitySquareRoute
import com.efbsm5.easyway.ui.page.communityPage.DetailRoute
import com.efbsm5.easyway.ui.page.communityPage.NewPostPage
import com.efbsm5.easyway.ui.page.homepage.HomePage
import com.efbsm5.easyway.ui.page.map.MapPage
import com.efbsm5.easyway.viewmodel.communityViewModel.CommunityViewModel
import com.efbsm5.easyway.viewmodel.communityViewModel.DetailViewModel
import com.efbsm5.easyway.viewmodel.communityViewModel.NewPostViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EasyWay() {
    val navController = rememberNavController()
    val nav = navController.currentBackStackEntryAsState().value?.destination
    val scaffoldController = remember { ScaffoldController() }
    val snackBarHostState = remember { SnackbarHostState() }
    LaunchedEffect(nav?.route) {
        scaffoldController.refresh()
    }
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
                navController = navController, currentDestination = nav
            )
        }, floatingActionButton = {
            AnimatedContent(
                targetState = fabConfig, label = "fabChange"
            ) { cfg ->
                if (cfg != null && cfg.visible) {
                    if (cfg.text != null) {
                        ExtendedFloatingActionButton(
                            onClick = cfg.onClick,
                            expanded = cfg.extended,
                            icon = { Icon(cfg.icon, contentDescription = cfg.text) },
                            text = { Text(cfg.text) },
                            containerColor = cfg.containerColor
                                ?: FloatingActionButtonDefaults.containerColor,
                            contentColor = cfg.contentColor
                                ?: FloatingActionButtonDefaults.containerColor,
                        )
                    } else {
                        FloatingActionButton(
                            onClick = cfg.onClick,
                            containerColor = cfg.containerColor
                                ?: FloatingActionButtonDefaults.containerColor,
                            contentColor = cfg.contentColor
                                ?: FloatingActionButtonDefaults.containerColor,
                            modifier = Modifier.offset(y = -cfg.offset)
                        ) {
                            Icon(cfg.icon, contentDescription = null)
                        }
                    }
                }
            }
        }) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = RootRoute.Map.route,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                composable(RootRoute.Map.route) {
                    MapPage()
                }
                composable(RootRoute.Home.route) {
                    HomePage()
                }

                // 嵌套社区图
                navigation(
                    startDestination = CommunityRoute.Square.route,
                    route = RootRoute.CommunityGraph.route
                ) {
                    composable(CommunityRoute.Square.route) { entry ->
                        val viewModel: CommunityViewModel = viewModel(entry)
                        CommunitySquareRoute(
                            back = { navController.popBackStack() },
                            onSelectPost = { postAndUser ->
                                entry.savedStateHandle["postAndUser"] = postAndUser
                                // 跳转 Detail，采用方式 A 传 ID
                                navController.navigate(
                                    CommunityRoute.Detail.build(postAndUser.post.id)
                                )
                            },
                            onCreateNew = {
                                navController.navigate(CommunityRoute.NewPost.route)
                            },
                            viewModel = viewModel
                        )

                        // 监听 Detail 回传的更新（例如点赞数变了）
                        val updated by entry.savedStateHandle.getStateFlow<PostAndUser?>(
                            "updatedPost", null
                        ).collectAsState()

                        LaunchedEffect(updated) {
                            if (updated != null) {
                                viewModel.updateSingle(updated!!)
                                entry.savedStateHandle["updatedPost"] = null
                            }
                        }
                    }

                    composable(
                        route = CommunityRoute.Detail.route, arguments = listOf(
                            navArgument(CommunityRoute.Detail.ARG_POST_ID) {
                                type = NavType.IntType
                            })
                    ) { detailEntry ->
                        val squareEntry =
                            navController.getBackStackEntry(CommunityRoute.Square.route)
                        val cached = squareEntry.savedStateHandle.get<PostAndUser>("postAndUser")
                        val postId =
                            detailEntry.arguments?.getInt(CommunityRoute.Detail.ARG_POST_ID)
                        val viewModel: DetailViewModel = viewModel(detailEntry)

                        // 如果缓存对象不存在，可用 postId 去仓库再拉
                        DetailRoute(
                            onBack = { navController.popBackStack() },
                            postAndUser = cached,
                            postId = postId,
                            viewModel = viewModel,
                            onLikeUpdated = { updated ->
                                squareEntry.savedStateHandle["updatedPost"] = updated
                            })
                    }

                    composable(CommunityRoute.NewPost.route) { newEntry ->
                        val viewModel: NewPostViewModel = viewModel()
                        NewPostPage(
                            back = { navController.popBackStack() },
                            onPostSuccess = { newPostAndUser ->
                                // 通知 Square 刷新或直接插入
                                val squareEntry =
                                    navController.getBackStackEntry(CommunityRoute.Square.route)
                                squareEntry.savedStateHandle["newPost"] = newPostAndUser
                                navController.popBackStack()
                            },
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}


sealed class RootRoute(val route: String) {
    data object Map : RootRoute("map")
    data object Home : RootRoute("home")
    data object CommunityGraph : RootRoute("community_graph")
}

sealed class CommunityRoute(val route: String) {
    data object Square : CommunityRoute("community_square")
    data object NewPost : CommunityRoute("community_new")
    data object Detail : CommunityRoute("community_detail/{postId}") {
        fun build(postId: Int) = "community_detail/$postId"
        const val ARG_POST_ID = "postId"
    }
}

