package com.efbsm5.easyway.ui.components

import android.content.Context
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.amap.api.maps.model.LatLng
import com.efbsm5.easyway.startMapApp

@Composable
fun NavigationDialog(location: LatLng, name: String, navigate: (LatLng) -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = {
            navigate(context, location, name)
        },
        title = { Text(text = "注意") },
        text = { Text("提供两种导航方式,确认则使用本软件进行导航,否则使用手机自带的app进行导航") },
        confirmButton = {
            TextButton(onClick = { navigate(location) }) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(onClick = { navigate(context, location, name) }) {
                Text("取消")
            }
        })
}

fun navigate(context: Context, location: LatLng, name: String) {
    context.startMapApp(
        dstLat = location.latitude, dstLon = location.longitude, dstName = name
    )
}