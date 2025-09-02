package com.efbsm5.easyway.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.amap.api.maps.model.LatLng
import com.efbsm5.easyway.SDKUtils

object LocationSaver {
    private val sharedPreferences: SharedPreferences =
        SDKUtils.Companion.getContext().getSharedPreferences("MapPreferences", Context.MODE_PRIVATE)

    var location: LatLng
        get() = with(sharedPreferences) {
            LatLng(
                getFloat("last_lat", 30.512537.toFloat()).toDouble(),
                getFloat("last_lng", 114.413622.toFloat()).toDouble()
            )
        }
        set(value) {
            sharedPreferences.edit {
                putFloat("last_lat", value.latitude.toFloat())
                putFloat("last_lng", value.longitude.toFloat())
            }
        }
    var cityCode: String
        get() = sharedPreferences.getString("citycode", "027") ?: "027"
        set(value) {
            sharedPreferences.edit {
                putString("citycode", value)
            }
        }
    var locationDetail: String
        get() = sharedPreferences.getString("detail", "武汉市") ?: "定位失败"
        set(value) {
            sharedPreferences.edit {
                putString("detail", value)
            }
        }
}