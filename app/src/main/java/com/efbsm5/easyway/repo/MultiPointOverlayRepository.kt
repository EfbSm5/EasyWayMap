package com.efbsm5.easyway.repo

import android.graphics.BitmapFactory
import com.amap.api.maps.model.BitmapDescriptor
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MultiPointItem
import com.efbsm5.easyway.R
import com.efbsm5.easyway.SDKUtils
import com.melody.map.gd_compose.poperties.MapUiSettings
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * MultiPointOverlayRepository
 * @author 被风吹过的夏天
 * @email developer_melody@163.com
 * @github: https://github.com/TheMelody/OmniMap
 * created 2022/10/21 10:55
 */
object MultiPointOverlayRepository {

    fun initMapUiSettings(): MapUiSettings {
        return MapUiSettings(
            isZoomEnabled = true,
            isScrollGesturesEnabled = true,
            isZoomGesturesEnabled = true,
            isScaleControlsEnabled = true
        )
    }

    fun initMultiPointIcon(): BitmapDescriptor {
        return BitmapDescriptorFactory.fromBitmap(
            BitmapFactory.decodeResource(
                SDKUtils.getContext().resources,
                R.drawable.multi_point_blue
            )
        )
    }

    fun initMultiPointItemList(): List<MultiPointItem> {
        val list: MutableList<MultiPointItem> = mutableListOf()
        val outputStream: FileOutputStream? = null
        var inputStream: InputStream? = null
        try {
            inputStream =
                SDKUtils.getContext().resources.openRawResource(R.raw.point10w)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                val str = line?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }
                    ?.toTypedArray() ?: continue
                val lat = str[1].trim { it <= ' ' }.toDouble()
                val lon = str[0].trim { it <= ' ' }.toDouble()
                val latLng = LatLng(lat, lon, false) //保证经纬度没有问题的时候可以填false
                val multiPointItem = MultiPointItem(latLng)
                list.add(multiPointItem)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return list
    }
}