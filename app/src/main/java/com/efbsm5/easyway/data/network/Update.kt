package com.efbsm5.easyway.data.network

//import com.alibaba.idst.nui.BuildConfig
import android.content.Intent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.efbsm5.easyway.BuildConfig
import com.efbsm5.easyway.data.models.assistModel.UpdateInfo

private const val TAG = "Update"

@Composable
fun CheckUpdate() {
    //    LaunchedEffect(Unit) {
//        HttpClient().checkForUpdate { info ->
//            Log.e(TAG, "CheckUpdate: ${info.toString()}")
//            if (info != null && shouldUpdate(info.versionCode)) {
//                updateInfo = info
//            }
//        }
//    }
//    updateInfo?.let {
//        UpdateDialog(it) {
//            updateInfo = if (it) null
//            else null
//        }
//    }
}


@Composable
private fun UpdateDialog(updateInfo: UpdateInfo, callback: (Boolean) -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = { callback(false) },
        title = { Text(text = "发现新版本 ${updateInfo.versionName}") },
        text = { Text(text = updateInfo.updateMessage) },
        confirmButton = {
            Button(onClick = {
                val intent = Intent(Intent.ACTION_VIEW, updateInfo.apkUrl.toUri())
                context.startActivity(intent)
                callback(true)
            }) {
                Text("立即更新")
            }
        },
        dismissButton = {
            Button(onClick = { callback(false) }) {
                Text("稍后再说")
            }
        })
}

private fun shouldUpdate(latestVersionCode: Int): Boolean {
    return latestVersionCode > BuildConfig.VERSION_CODE
}