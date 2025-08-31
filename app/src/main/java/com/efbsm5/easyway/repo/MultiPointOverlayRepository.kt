package com.efbsm5.easyway.repo

import android.graphics.BitmapFactory
import com.amap.api.maps.model.BitmapDescriptor
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MultiPointItem
import com.efbsm5.easyway.R
import com.efbsm5.easyway.SDKUtils
import com.melody.map.gd_compose.poperties.MapUiSettings
import java.io.IOException

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

    suspend fun initMultiPointItemList(): List<MultiPointItem> {
        val list: MutableList<MultiPointItem> = mutableListOf()
        DataRepository.getAllPoints().collect {

        }
        try {

            val lat = str[1].trim { it <= ' ' }.toDouble()
            val lon = str[0].trim { it <= ' ' }.toDouble()
            val latLng = LatLng(lat, lon, false) //保证经纬度没有问题的时候可以填false
            val multiPointItem = MultiPointItem(latLng)
            list.add(multiPointItem)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return list
    }
}