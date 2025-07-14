package com.efbsm5.easyway

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Poi
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItemV2
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.User
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 腾讯地图和高德地图的经度纬度不用换算，只有百度地图需要转换一下
 * MapUtils
 * @author 被风吹过的夏天
 * @email developer_melody@163.com
 * @github: https://github.com/TheMelody/OmniMap
 * created 2022/10/08 16:20
 */

private const val GAO_DE_MAP_PACKAGE_NAME = "com.autonavi.minimap"

private const val BAIDU_MAP_PACKAGE_NAME = "com.baidu.BaiduMap"

private const val TAG = "MapUtils"

/**
 * 打开地图App
 */
fun Context.startMapApp(dstLat: Double, dstLon: Double, dstName: String) {
    if (isInstallApp(GAO_DE_MAP_PACKAGE_NAME)) {
        openGaoDeMap(dstLat, dstLon, dstName)
    } else if (isInstallApp(BAIDU_MAP_PACKAGE_NAME)) {
        openBaiduMap(dstLat, dstLon)
    } else {
        openTencentMap(dstLat, dstLon, dstName)
    }
}

fun convertToLatLonPoint(latLng: LatLng): LatLonPoint {
    return LatLonPoint(latLng.latitude, latLng.longitude)
}

fun convertToLatLng(latLonPoint: LatLonPoint): LatLng {
    return LatLng(latLonPoint.latitude, latLonPoint.longitude)
}

fun showMsg(text: String) {
    Toast.makeText(
        SDKUtils.getContext(), text, Toast.LENGTH_SHORT
    ).show()
}

fun getCurrentFormattedTime(): String {
    val currentTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return currentTime.format(formatter)
}

fun calculateDistance(point1: LatLng, point2: LatLng): Float {
    val location1 = Location("").apply {
        latitude = point1.latitude
        longitude = point1.longitude
    }
    val location2 = Location("").apply {
        latitude = point2.latitude
        longitude = point2.longitude
    }
    return location1.distanceTo(location2)
}

@SuppressLint("QueryPermissionsNeeded")
fun onNavigate(context: Context, latLng: LatLng) {
    val uri = "geo:${latLng.latitude},${latLng.longitude}".toUri()
    val intent = Intent(Intent.ACTION_VIEW, uri)
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        showMsg("未找到地图应用")
    }
}

fun locationToLatlng(location: Location): LatLng {
    return LatLng(location.latitude, location.longitude)
}

fun Float.formatDistance(): String {
    return if (this < 500) {
        "${this.toInt()} m"  // 转为整数并以 m 为单位
    } else {
        "%.2f km".format(this / 1000)  // 转为 km 并保留两位小数
    }
}


fun getInitPoint(latLng: LatLng = LatLng(30.507950, 114.413514)): EasyPoint {
    return EasyPoint(
        pointId = 0,
        name = "未找到的标点",
        type = "未知类别",
        info = "无信息",
        location = "无详细地址",
        photo = "https://27142293.s21i.faiusr.com/2/ABUIABACGAAg_I_bmQYokt25kQUwwAc4gAU.jpg",
        refreshTime = "2035.3.8",
        likes = 0,
        dislikes = 0,
        lat = latLng.latitude,
        lng = latLng.longitude,
        userId = 0,
    )
}

fun getInitUser(): User {
    return User(
        id = 0,
        name = "小明",
        avatar = null,
    )
}

fun getInitPost(): Post {
    return Post(
        title = "",
        date = "",
        like = 0,
        content = "",
        lat = 30.5155,
        lng = 114.4268,
        position = "",
        userId = 1,
        id = 1,
        type = 1,
        photo = emptyList()
    )
}

fun EasyPoint.getLatlng(): LatLng {
    return LatLng(this.lat, this.lng)
}

fun poiToEasyPoint(poi: Poi): EasyPoint {
    return EasyPoint(
        pointId = 0,
        name = poi.name,
        type = "一般点",
        info = "无详细描述",
        location = "无详细描述",
        photo = null,
        refreshTime = "未知",
        likes = 0,
        dislikes = 0,
        lat = poi.coordinate.latitude,
        lng = poi.coordinate.longitude,
        userId = 0,
    )
}

fun addPoiItem(poiItemV2: PoiItemV2): EasyPoint {
    return EasyPoint(
        pointId = 0,
        name = poiItemV2.title,
        type = "一般点",
        info = poiItemV2.snippet,
        location = "无详细描述",
        photo = poiItemV2.photos.firstOrNull()?.url,
        refreshTime = "未知",
        likes = 0,
        dislikes = 0,
        lat = poiItemV2.latLonPoint.latitude,
        lng = poiItemV2.latLonPoint.longitude,
        userId = 0,
    )
}

/*
 * 打开高德地图
 * @param dstLat  终点纬度
 * @param dstLon  终点经度
 * @param dstName 终点名称
 */
private fun Context.openGaoDeMap(dstLat: Double, dstLon: Double, dstName: String) {
    val intent = Intent().apply {
        action = Intent.ACTION_VIEW
        addCategory(Intent.CATEGORY_DEFAULT)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        // 将功能Scheme以URI的方式传入data
        data = String.format(
            "androidamap://route?" + "sourceApplication=%s" + "&sname=我的位置&dlat=%s" + "&dlon=%s" + "&dname=%s&dev=0&m=0&t=1",
            getAppName(packageName),
            dstLat,
            dstLon,
            dstName
        ).toUri()
    }
    applicationContext.safeStartActivity(intent)
}

/**
 * 打开百度地图
 */
private fun Context.openBaiduMap(dstLat: Double, dstLon: Double) {
    val intent = Intent().apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val latLonArray = convertGTMapLatLonToBaidu(dstLat, dstLon)
        data = String.format(
            "baidumap://map/direction?region=0" + "&destination=%s,%s" + "&mode=transit&src=andr.waimai.%s",
            latLonArray.getOrNull(0) ?: 0.0,
            latLonArray.getOrNull(1) ?: 0.0,
            getAppName(packageName)
        ).toUri()
    }
    applicationContext.safeStartActivity(intent)
}

/*
 * 打开腾讯地图，腾讯地图和高德地图的经度纬度不用换算
 * @param dstLat  终点纬度
 * @param dstLon  终点经度
 * @param dstName 终点名称
 */
private fun Context.openTencentMap(dstLat: Double, dstLon: Double, dstName: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        data = String.format(
            "qqmap://map/routeplan?type=bus&from=我的位置&fromcoord=0,0" + "&to=%s" + "&tocoord=%s,%s" + "&policy=1" + "&referer=%s",
            dstName,
            dstLat,
            dstLon,
            getAppName(packageName)
        ).toUri()
    }
    applicationContext.safeStartActivity(intent)
}

/**
 * 高德地图|腾讯地图（火星坐标）转换成百度地图坐标
 */
private fun convertGTMapLatLonToBaidu(lat: Double, lon: Double): Array<Double> {
    val x: Double = lon
    val y: Double = lat
    val z: Double = sqrt(x * x + y * y) + 0.00002 * sin(y * Math.PI * 3000.0 / 180.0)
    val theta: Double = atan2(y, x) + 0.000003 * cos(x * Math.PI * 3000.0 / 180.0)

    val convertLon =
        BigDecimal(z * cos(theta) + 0.0065).setScale(6, RoundingMode.HALF_UP).toDouble()

    val convertLat = BigDecimal(z * sin(theta) + 0.006).setScale(6, RoundingMode.HALF_UP).toDouble()

    return arrayOf(convertLat, convertLon)
}

internal fun Context.safeStartActivity(intent: Intent) {
    val result = runCatching {
        startActivity(intent)
    }
    if (result.isFailure) {
        Log.e(TAG, result.exceptionOrNull()?.message ?: "")
    }
}

internal fun Context.getApplicationInfo(packageName: String): ApplicationInfo {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.getApplicationInfo(
            packageName, PackageManager.ApplicationInfoFlags.of(
                PackageManager.GET_META_DATA.toLong()
            )
        )
    } else {
        packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
    }
}

internal fun Context.isInstallApp(packageName: String): Boolean {
    return runCatching {
        getApplicationInfo(packageName)
    }.isSuccess
}

internal fun Context.getAppName(packageName: String): CharSequence {
    val applicationResult = runCatching {
        getApplicationInfo(packageName)
    }
    return applicationResult.getOrNull()?.loadLabel(packageManager) ?: packageName
}