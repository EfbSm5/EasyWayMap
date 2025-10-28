package com.efbsm5.easyway.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.navigation.NavDestination.Companion.hierarchy
import com.efbsm5.easyway.ui.Route

@Composable
fun BottomNavigationBar(
    navigate: (Route) -> Unit,
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
