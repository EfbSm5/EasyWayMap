package com.efbsm5.easyway.data

import com.amap.api.maps.model.LatLng
import kotlin.random.Random

object WuhanBoxGenerator {
    private const val MIN_LAT = 29.95
    private const val MAX_LAT = 31.40
    private const val MIN_LNG = 113.60
    private const val MAX_LNG = 115.10

    fun random(random: Random = Random): LatLng {
        val lat = random.nextDouble(MIN_LAT, MAX_LAT)
        val lng = random.nextDouble(MIN_LNG, MAX_LNG)
        return LatLng(lat, lng)
    }
}