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

package com.efbsm5.easyway.ui.page.map

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.CameraPosition
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MultiPointItem
import com.efbsm5.easyway.contract.map.LocationTrackingContract
import com.efbsm5.easyway.contract.map.MapState
import com.efbsm5.easyway.convertToMultiPointItem
import com.efbsm5.easyway.dialog.ShowOpenGPSDialog
import com.efbsm5.easyway.launcher.handlerGPSLauncher
import com.efbsm5.easyway.model.BusRouteDataState
import com.efbsm5.easyway.model.DrivingRouteDataState
import com.efbsm5.easyway.model.RideRouteDataState
import com.efbsm5.easyway.model.WalkRouteDataState
import com.efbsm5.easyway.repo.LocationTrackingRepository
import com.efbsm5.easyway.showMsg
import com.efbsm5.easyway.ui.components.melody.MapMenuButton
import com.efbsm5.easyway.ui.components.melody.RedCenterLoading
import com.efbsm5.easyway.ui.components.melody.RoadTrafficSwitch
import com.efbsm5.easyway.ui.components.requestMultiplePermission
import com.efbsm5.easyway.ui.route.BusRouteOverlayContent
import com.efbsm5.easyway.ui.route.DrivingRouteOverlayContent
import com.efbsm5.easyway.ui.route.RideRouteOverlayContent
import com.efbsm5.easyway.ui.route.WalkRouteOverlayContent
import com.efbsm5.easyway.viewmodel.mapViewModel.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.melody.map.gd_compose.GDMap
import com.melody.map.gd_compose.overlay.Marker
import com.melody.map.gd_compose.overlay.MultiPointOverlay
import com.melody.map.gd_compose.overlay.rememberMarkerState
import com.melody.map.gd_compose.position.rememberCameraPositionState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach

/**
 * 高德地图默认定位蓝点，这里我们代码动态替换了SDK默认的蓝点图片
 * @author 被风吹过的夏天
 * @email developer_melody@163.com
 * @github: https://github.com/TheMelody/OmniMap
 * created 2022/10/10 17:31
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun MapScreen(
    onClick: (MultiPointItem) -> Unit,
    mapState: MapState,
    onChangeState: (MapState) -> Unit = {},
    viewModel: MapViewModel = viewModel(),
    selectedPoint: MultiPointItem?
) {
    val currentState by viewModel.uiState.collectAsState()
    val cameraPosition = rememberCameraPositionState {
        position = CameraPosition(LatLng(39.91, 116.40), 11f, 0f, 0f)
    }
    var firstValue by remember { mutableStateOf(false) }
    val markerState = rememberMarkerState()
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.onEach {
            when (it) {
                is LocationTrackingContract.Effect.Toast -> showMsg(it.msg)
                is LocationTrackingContract.Effect.ClickPoint -> onClick(it.multiPointItem)
            }
        }.collect()
    }
    LaunchedEffect(selectedPoint) {
        snapshotFlow { selectedPoint }.filterNotNull().collect {
            viewModel.handleEvents(LocationTrackingContract.Event.ClickPoint(it))
        }
    }
    val openGpsLauncher = handlerGPSLauncher(viewModel::checkGpsStatus)
    val reqGPSPermission = requestMultiplePermission(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        ),
        onGrantAllPermission = viewModel::handleGrantLocationPermission,
        onNoGrantPermission = viewModel::handleNoGrantLocationPermission
    )

    LaunchedEffect(Unit) {
        snapshotFlow { reqGPSPermission.allPermissionsGranted }.collect {
            // 从app应用权限开关页面，打开权限，需要再检查一下GPS开关
            viewModel.checkGpsStatus()
        }
    }
    LaunchedEffect(mapState) {
        viewModel.changeState(mapState)
    }
    LaunchedEffect(currentState.clickedPoint) {
        snapshotFlow { currentState.clickedPoint }.filterNotNull()
            .collect { markerState.position = it }
    }
    LaunchedEffect(currentState.locationLatLng) {
        if (!firstValue && null != currentState.locationLatLng) {
            firstValue = true
            cameraPosition.animate(CameraUpdateFactory.newLatLng(currentState.locationLatLng))
        }
    }

    LaunchedEffect(currentState.isOpenGps, reqGPSPermission.allPermissionsGranted) {
        if (currentState.isOpenGps == true) {
            if (!reqGPSPermission.allPermissionsGranted) {
                reqGPSPermission.launchMultiplePermissionRequest()
            } else {
                viewModel.startMapLocation()
            }
        }
    }

    if (currentState.isShowOpenGPSDialog) {
        ShowOpenGPSDialog(
            onDismiss = viewModel::hideOpenGPSDialog, onPositiveClick = {
                viewModel.openGPSPermission(openGpsLauncher)
            })
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GDMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPosition,
            properties = currentState.mapProperties,
            uiSettings = currentState.mapUiSettings,
            locationSource = viewModel,
            onMapLoaded = viewModel::checkGpsStatus,
            onMapClick = {
                viewModel.handleEvents(
                    LocationTrackingContract.Event.ClickPoint(
                        MultiPointItem(it)
                    )
                )
            },
            onMapPOIClick = {
                it?.let {
                    viewModel.handleEvents(
                        LocationTrackingContract.Event.ClickPoint(
                            it.convertToMultiPointItem()
                        )
                    )
                }
            })
        {
            if (mapState == MapState.LocationState) {
                MultiPointOverlay(
                    enable = true,
                    icon = LocationTrackingRepository.initMultiPointIcon(),
                    multiPointItems = currentState.points,
                    onClick = { viewModel.handleEvents(LocationTrackingContract.Event.ClickPoint(it)) })
            }
            Marker(
                icon = BitmapDescriptorFactory.defaultMarker(),
                state = markerState,
                visible = currentState.clickedPoint != null,
                isClickable = false
            )
            if (!currentState.isLoading && mapState is MapState.Route) {
                currentState.routDataState?.apply {
                    when (this) {
                        is DrivingRouteDataState -> {
                            DrivingRouteOverlayContent(data = this)
                        }

                        is BusRouteDataState -> {
                            BusRouteOverlayContent(data = this)
                        }

                        is WalkRouteDataState -> {
                            WalkRouteOverlayContent(data = this)
                        }

                        is RideRouteDataState -> {
                            RideRouteOverlayContent(data = this)
                        }
                    }
                }
            }
        }
        if (mapState is MapState.Route) {
            MenuButtonList(onClick = {
                viewModel.handleEvents(
                    LocationTrackingContract.Event.QueryRoutePlan(
                        it, mapState.destination
                    )
                )
            })
            RoadTrafficSwitch(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 4.dp)
                    .clickable(onClick = { viewModel.handleEvents(LocationTrackingContract.Event.RoadTrafficClick) }),
                isEnable = currentState.mapProperties.isTrafficEnabled
            )
        }
        if (currentState.isLoading) {
            RedCenterLoading()
        }

    }
}


@Composable
private fun BoxScope.MenuButtonList(onClick: (Int) -> Unit) {
    val currentOnClick by rememberUpdatedState(newValue = onClick)
    FlowRow(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.3F))
    ) {
        MapMenuButton(text = "驾车", onClick = { currentOnClick.invoke(0) })
        MapMenuButton(text = "公交", onClick = { currentOnClick.invoke(1) })
        MapMenuButton(text = "步行", onClick = { currentOnClick.invoke(2) })
        MapMenuButton(text = "骑行", onClick = { currentOnClick.invoke(3) })
    }
}