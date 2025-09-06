package com.efbsm5.easyway.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.efbsm5.easyway.ui.page.communityPage.CommunityNav
import com.efbsm5.easyway.ui.page.homepage.HomePage
import com.efbsm5.easyway.ui.page.map.MapPage

@Preview
@Composable
fun EasyWay() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController, currentDestination = navBackStackEntry?.destination
            )
        }, modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "MapPage",
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            composable("MapPage") {
                MapPage()
            }
            composable("Community") {
                CommunityNav(
                    back = { navController.navigate("MapPage") })
            }
            composable("Home") {
                HomePage()
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    navController: NavController, currentDestination: NavDestination?
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
    ) {
        BottomBarItems.forEach { dest ->
            val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true

            NavigationBarItem(selected = selected, onClick = {
                if (!selected) {
                    navController.navigateBottom(dest)
                } else {
//                        navController.handleReselect(dest.route)
                }
            }, icon = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(dest.icon, contentDescription = dest.label)
                    Text(dest.label, style = MaterialTheme.typography.labelSmall)
                }
            })
        }
    }
}

fun NavController.navigateBottom(destination: BottomDestination) {
    this.navigate(destination.route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(this@navigateBottom.graph.startDestinationId) {
            saveState = true
        }
    }
}

sealed class BottomDestination(
    val route: String, val icon: ImageVector, val label: String
) {
    data object Map : BottomDestination("map", Icons.Default.Place, "Map")
    data object Community :
        BottomDestination("community_root", Icons.Default.AccountBox, "Community")

    data object Home : BottomDestination("home", Icons.Default.Home, "Home")
}

val BottomBarItems = listOf(
    BottomDestination.Map, BottomDestination.Community, BottomDestination.Home
)

