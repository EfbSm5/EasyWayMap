package com.efbsm5.easyway.repo

import com.amap.api.maps.AMapException
import com.amap.api.services.core.PoiItemV2
import com.amap.api.services.core.ServiceSettings
import com.amap.api.services.poisearch.PoiResultV2
import com.amap.api.services.poisearch.PoiSearchV2
import com.amap.api.services.poisearch.PoiSearchV2.OnPoiSearchListener
import com.efbsm5.easyway.SDKUtils

suspend fun searchForPoi(keyword: String, onPoiSearched: OnPoiSearched) {
//    val result = MarkersInSchool.searchFromMarkers(keyword = keyword)
//    if (result != null) {
//        onPoiSearched(arrayListOf(result))
//    }
    ServiceSettings.updatePrivacyShow(SDKUtils.getContext(), true, true)
    ServiceSettings.updatePrivacyAgree(SDKUtils.getContext(), true)
    val query: PoiSearchV2.Query = PoiSearchV2.Query(keyword, "", "027")
    query.pageSize = 5
    query.pageNum = 1
    try {
        val poiSearch = PoiSearchV2(SDKUtils.getContext(), query)
        poiSearch.setOnPoiSearchListener(object : OnPoiSearchListener {
            override fun onPoiSearched(
                p0: PoiResultV2?, p1: Int
            ) {
                p0?.pois?.let { onPoiSearched.onPoiSearched(it) }
            }

            override fun onPoiItemSearched(
                p0: PoiItemV2?, p1: Int
            ) {
            }
        })
        poiSearch.searchPOIAsyn()
    } catch (e: AMapException) {
        throw RuntimeException(e)
    }
}

interface OnPoiSearched {
    fun onPoiSearched(result: List<PoiItemV2>)
}