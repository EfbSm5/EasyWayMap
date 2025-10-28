package com.efbsm5.easyway.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import com.efbsm5.easyway.ui.Route

@Composable
fun BottomNavigationBar(
    now: Route,
    navigate: (Route) -> Unit,
    refresh: () -> Unit
) {
    val BottomBarItems = listOf(
        BottomDestination.Map, BottomDestination.Community, BottomDestination.Home
    )
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
    ) {
        BottomBarItems.forEach { dest ->
            val selected = now == dest
            NavigationBarItem(selected = selected, onClick = {
                if (!selected) {
                    navigate(dest.route)
                } else {
                    refresh()
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

sealed class BottomDestination(
    val route: Route, val icon: ImageVector, val label: String
) {
    data object Map : BottomDestination(Route.MapRoute, Icons.Default.Place, "Map")
    data object Community :
        BottomDestination(Route.Post, Icons.Default.AccountBox, "Community")

    data object Home : BottomDestination(Route.Home, Icons.Default.Home, "Home")
}

