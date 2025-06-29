package com.efbsm5.easyway.repo

import android.graphics.BitmapFactory
import android.graphics.Color
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.model.BitmapDescriptor
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MultiPointItem
import com.amap.api.maps.model.MyLocationStyle
import com.efbsm5.easyway.R
import com.efbsm5.easyway.SDKUtils
import com.melody.map.gd_compose.poperties.MapProperties
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
            isScaleControlsEnabled = true,
            myLocationButtonEnabled = true,
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

    fun initMapProperties(): MapProperties {
        // 注意：这里不要用BitmapDescriptorFactory.fromResource(你的新图)，不然会出现不生效的情况
        val iconBitmap = BitmapFactory.decodeResource(
            SDKUtils.getContext().resources,
            R.drawable.ic_map_location_self
        )
        val locationIcon = BitmapDescriptorFactory.fromBitmap(iconBitmap)
        return MapProperties(
            isMyLocationEnabled = true,
            myLocationStyle = MyLocationStyle().apply {
                // 设置小蓝点的图标
                myLocationIcon(locationIcon)
                // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
                myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE)
                // 设置圆形的边框颜色
                strokeColor(Color.BLACK)
                // 设置圆形的填充颜色
                radiusFillColor(Color.argb(100, 0, 0, 180))
                // 设置圆形的边框粗细
                strokeWidth(0.1f)
            }
        )
    }


    inline fun initAMapLocationClient(
        locationClient: AMapLocationClient?,
        listener: AMapLocationListener,
        block: (AMapLocationClient, AMapLocationClientOption) -> Unit
    ) {
        if (null == locationClient) {
            val newLocationClient = AMapLocationClient(SDKUtils.getContext())
            //初始化定位参数
            val locationClientOption = AMapLocationClientOption()
            //设置定位回调监听
            newLocationClient.setLocationListener(listener)
            //设置为高精度定位模式
            locationClientOption.locationMode =
                AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            //设置定位参数
            newLocationClient.setLocationOption(locationClientOption)
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            block.invoke(newLocationClient.apply {
                //启动定位
                startLocation()
            }, locationClientOption)
        }
    }

    inline fun handleLocationChange(
        amapLocation: AMapLocation?,
        block: (AMapLocation?, String?) -> Unit
    ) {
        if (amapLocation != null) {
            if (amapLocation.errorCode == 0) {
                // 显示系统小蓝点
                block.invoke(amapLocation, null)
            } else {
                block.invoke(
                    null,
                    "定位失败," + amapLocation.errorCode + ": " + amapLocation.errorInfo
                )
            }
        }
    }
}