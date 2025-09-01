package com.efbsm5.easyway.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.efbsm5.easyway.ui.page.MapPage
import com.efbsm5.easyway.ui.page.communityPage.CommunityNav
import com.efbsm5.easyway.ui.page.homepage.HomePage


@Composable
fun EasyWay() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    Scaffold(
        bottomBar = {
            AppBar(
                modifier = Modifier.fillMaxWidth(),
                onNavigate = { navController.navigate(it) },
                currentDestination = navBackStackEntry?.id ?: "MapPage"
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
                    back = { navController.navigate("MapPage") }
                )
            }
            composable("Home") {
                HomePage()
            }
        }
    }
}

@Composable
private fun AppBar(
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit = {},
    currentDestination: String = ""
) {
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            onClick = { onNavigate("MapPage") },
            selected = currentDestination == "MapPage",
            icon = { Icon(Icons.Default.Place, contentDescription = "MapPage") })
        NavigationBarItem(
            onClick = { onNavigate("Community") },
            selected = currentDestination == "Community",
            icon = {
                Icon(
                    Icons.Default.AccountBox, contentDescription = "Community"
                )
            })
        NavigationBarItem(
            onClick = { onNavigate("home") },
            selected = currentDestination == "home",
            icon = {
                Icon(
                    Icons.Default.Home, contentDescription = "Home"
                )
            })
    }
}
