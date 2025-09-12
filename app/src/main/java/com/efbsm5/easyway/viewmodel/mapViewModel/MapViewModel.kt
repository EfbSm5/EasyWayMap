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

package com.efbsm5.easyway.viewmodel.mapViewModel

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.LocationSource
import com.amap.api.maps.LocationSource.OnLocationChangedListener
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MultiPointItem
import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.map.MapContract
import com.efbsm5.easyway.contract.map.MapState
import com.efbsm5.easyway.data.LocationSaver
import com.efbsm5.easyway.openAppPermissionSettingPage
import com.efbsm5.easyway.repo.DataRepository
import com.efbsm5.easyway.repo.DragDropSelectPointRepository
import com.efbsm5.easyway.repo.LocationTrackingRepository
import com.efbsm5.easyway.repo.RoutePlanRepository
import com.efbsm5.easyway.safeLaunch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

/**
 * LocationTrackingViewModel
 * @author 被风吹过的夏天
 * @email developer_melody@163.com
 * @github: https://github.com/TheMelody/OmniMap
 * created 2022/10/10 17:40
 */
class MapViewModel :
    BaseViewModel<MapContract.Event, MapContract.State, MapContract.Effect>(),
    LocationSource, AMapLocationListener {

    private var mListener: OnLocationChangedListener? = null
    private var mLocationClient: AMapLocationClient? = null
    private var mLocationOption: AMapLocationClientOption? = null
    private var routeStartLocation: LatLng? = null

    override fun createInitialState(): MapContract.State {
        return MapContract.State(
            mapProperties = LocationTrackingRepository.initMapProperties(),
            mapUiSettings = LocationTrackingRepository.initMapUiSettings(),
            isShowOpenGPSDialog = false,
            grantLocationPermission = false,
            locationLatLng = null,
            isOpenGps = null,
            clickedPoint = null,
        )
    }

    init {
        asyncLaunch(Dispatchers.IO) {
            val r = DataRepository.getAllPoints()
            r.onSuccess {
                val list = it.map { it ->
                    MultiPointItem(it.getLatLng()).apply {
                        `object` = it
                        title = it.name
                    }
                }
                setState { copy(isLoading = false, points = list) }
            }.onFailure { setState { copy(isLoading = false, error = "error") } }
        }
    }

    fun moveToLatLng(latLng: LatLng) {
        setEffect { MapContract.Effect.MoveToPoint(latLng) }
    }

    fun moveToLocation() {
        currentState.locationLatLng?.let {
            moveToLatLng(it)
        }
    }

    override fun handleEvents(event: MapContract.Event) {
        when (event) {
            is MapContract.Event.ShowOpenGPSDialog -> {
                setState { copy(isShowOpenGPSDialog = true) }
            }

            is MapContract.Event.HideOpenGPSDialog -> {
                setState { copy(isShowOpenGPSDialog = false) }
            }

            is MapContract.Event.ClickPoint -> clickPoint(event.multiPointItem)
            is MapContract.Event.QueryRoutePlan -> routePlanSearch(
                event.queryType,
                startPoint = LocationSaver.location,
                endPoint = event.endPoint,
            )

            MapContract.Event.RoadTrafficClick -> setState {
                copy(
                    mapProperties = mapProperties.copy(
                        isTrafficEnabled = !mapProperties.isTrafficEnabled
                    )
                )
            }
        }
    }

    fun changeState(mapState: MapState) {
        if (mapState == MapState.LocationState) {
            setState {
                copy(
                    mapUiSettings = LocationTrackingRepository.initMapUiSettings(),
                    mapProperties = LocationTrackingRepository.initMapProperties()
                )
            }
        } else {
            setState {
                copy(
                    mapUiSettings = RoutePlanRepository.initMapUiSettings(),
                    mapProperties = RoutePlanRepository.initMapProperties()
                )
            }
        }
    }

    fun routePlanSearch(queryType: Int, startPoint: LatLng, endPoint: LatLng) {
        setState { copy(isLoading = true, routDataState = null) }
        asyncLaunch(Dispatchers.IO) {
            val result = runCatching {
                RoutePlanRepository.getRoutePlanResult(
                    queryType = queryType,
                    startPoint = startPoint,
                    endPoint = endPoint,
                )
            }
            if (result.isSuccess) {
                setState { copy(isLoading = false, routDataState = result.getOrNull()) }
            } else {
                setState { copy(isLoading = false) }
                setEffect { MapContract.Effect.Toast(result.exceptionOrNull()?.message) }
            }
        }
    }

    /**
     * 检查系统GPS开关是否打开
     */
    fun checkGpsStatus() = asyncLaunch(Dispatchers.IO) {
        val isOpenGps = DragDropSelectPointRepository.checkGPSIsOpen()
        setState { copy(isOpenGps = isOpenGps) }
        if (!isOpenGps) {
            setEvent(MapContract.Event.ShowOpenGPSDialog)
        } else {
            hideOpenGPSDialog()
        }
    }

    private fun clickPoint(multiPointItem: MultiPointItem) {
        setState { copy(clickedPoint = multiPointItem.latLng) }
        setEffect { MapContract.Effect.ClickPoint(multiPointItem) }
    }

    fun hideOpenGPSDialog() {
        setEvent(MapContract.Event.HideOpenGPSDialog)
    }

    /**
     * 手机开了GPS，app没有授予权限
     */
    fun handleNoGrantLocationPermission() {
        setState { copy(grantLocationPermission = false) }
        setEvent(MapContract.Event.ShowOpenGPSDialog)
    }

    fun handleGrantLocationPermission() {
        setState { copy(grantLocationPermission = true) }
        checkGpsStatus()
    }

    fun openGPSPermission(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
        if (DragDropSelectPointRepository.checkGPSIsOpen()) {
            // 已打开系统GPS，APP还没授权，跳权限页面
            openAppPermissionSettingPage()
        } else {
            // 打开系统GPS开关页面
            launcher.safeLaunch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    fun startMapLocation() {
        LocationTrackingRepository.initAMapLocationClient(mLocationClient, this) { client, option ->
            mLocationClient = client
            mLocationOption = option
        }
    }

    override fun onLocationChanged(amapLocation: AMapLocation?) {
        LocationTrackingRepository.handleLocationChange(amapLocation) { aMapLocation, msg ->
            if (null != aMapLocation) {
                val delayTime = if (null == currentState.locationLatLng) 100L else 0L
                setState {
                    copy(locationLatLng = LatLng(aMapLocation.latitude, aMapLocation.longitude))
                }
                asyncLaunch {
                    // 首次直接显示，高德地图【默认小蓝点】会【有点闪烁】，延迟一下再回调
                    delay(delayTime)
                    // 显示系统小蓝点
                    mListener?.onLocationChanged(aMapLocation)
                }
                LocationSaver.apply {
                    location = LatLng(aMapLocation.latitude, aMapLocation.longitude)
                    cityCode = aMapLocation.cityCode
                    locationDetail = aMapLocation.locationDetail
                }
            } else {
                setEffect { MapContract.Effect.Toast(msg) }
            }
        }
    }

    override fun activate(listener: OnLocationChangedListener?) {
        mListener = listener
        if (DragDropSelectPointRepository.checkGPSIsOpen() && currentState.grantLocationPermission) {
            startMapLocation()
        }
    }

    override fun deactivate() {
        mLocationClient?.stopLocation()
        mLocationClient?.onDestroy()
        mLocationClient = null
        mListener = null
    }

    override fun onCleared() {
        mLocationClient?.onDestroy()
        mLocationClient = null
        super.onCleared()
    }
}