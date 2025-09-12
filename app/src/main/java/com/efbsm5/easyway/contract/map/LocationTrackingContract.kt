// MIT License
//
// Copyright (c) 2022 被风吹过的夏天
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package com.efbsm5.easyway.contract.map

import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MultiPointItem
import com.efbsm5.easyway.model.BaseRouteDataState
import com.efbsm5.easyway.state.IUiEffect
import com.efbsm5.easyway.state.IUiEvent
import com.efbsm5.easyway.state.IUiState
import com.melody.map.gd_compose.poperties.MapProperties
import com.melody.map.gd_compose.poperties.MapUiSettings

/**
 * LocationTrackingContract
 * @author 被风吹过的夏天
 * @email developer_melody@163.com
 * @github: https://github.com/TheMelody/OmniMap
 * created 2022/10/10 17:45
 */
class LocationTrackingContract {
    sealed class Event : IUiEvent {
        data object ShowOpenGPSDialog : Event()
        data object HideOpenGPSDialog : Event()
        data class ClickPoint(val multiPointItem: MultiPointItem) : Event()
        data object RoadTrafficClick : Event()
        data class QueryRoutePlan(val queryType: Int, val endPoint: LatLng) : Event()
    }

    data class State(
        // 是否打开了系统GPS权限
        val isOpenGps: Boolean?,
        // 是否显示打开GPS的确认弹框
        val isShowOpenGPSDialog: Boolean,
        // App是否打开了定位权限
        val grantLocationPermission: Boolean,
        // 当前位置的经纬度
        val locationLatLng: LatLng?,
        val isLoading: Boolean = true,
        val mapProperties: MapProperties,
        val mapUiSettings: MapUiSettings,
        val clickedPoint: LatLng?,
        val routDataState: BaseRouteDataState? = null,
        val points: List<MultiPointItem> = emptyList(),
        val error: String? = null
    ) : IUiState

    sealed class Effect : IUiEffect {
        internal data class Toast(val msg: String?) : Effect()
        internal data class ClickPoint(val multiPointItem: MultiPointItem) : Effect()
    }
}