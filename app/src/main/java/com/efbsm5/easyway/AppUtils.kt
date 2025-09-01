package com.efbsm5.easyway

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.net.toUri

fun <T : Any> ActivityResultLauncher<T>.safeLaunch(input: T?) {
    if (null == input) {
        Log.e("AppUtils", "safeLaunch(T): input = null")
        return
    }
    val launchResult = runCatching {
        launch(input)
    }
    if (launchResult.isFailure) {
        Log.e("AppUtils", "safeLaunch(T),Exception:${launchResult.exceptionOrNull()?.message}")
    }
}

/**
 * 打开App权限设置页面
 */
fun openAppPermissionSettingPage() {
    val packageName = SDKUtils.getContext().packageName
    try {
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = "package:$packageName".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        SDKUtils.getContext().startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        try {
            // 往设置页面跳
            SDKUtils.getContext().startActivity(Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (ignore: ActivityNotFoundException) {
            // 有些手机跳系统设置也会崩溃
        }
    }
}

fun showToast(message: String?) {
    if (message.isNullOrBlank()) return
    val showToastResult = runCatching {
        Toast.makeText(SDKUtils.getContext(), message, Toast.LENGTH_SHORT).show()
    }
    if (showToastResult.isFailure) {
        Log.e("AppUtils", "showToastFailed:" + showToastResult.exceptionOrNull()?.message)
    }
}

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

@Composable
fun SelectPoint() {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.getStringExtra("result_key")
        }
    }
    val intent = Intent(SDKUtils.getContext(), LocationPoiActivity::class.java)
    launcher.launch(intent)
}

