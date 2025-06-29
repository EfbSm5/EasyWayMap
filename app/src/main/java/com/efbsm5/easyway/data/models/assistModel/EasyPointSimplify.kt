package com.efbsm5.easyway.data.models.assistModel

import com.amap.api.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class EasyPointSimplify(
    @SerializedName("point_id") val pointId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double,
) {
    fun getLatlng(): LatLng {
        return LatLng(lat, lng)
    }
}
