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

package com.efbsm5.easyway.ui

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.CameraPosition
import com.amap.api.maps.model.LatLng
import com.efbsm5.easyway.contract.MultiPointOverlayContract
import com.efbsm5.easyway.dialog.ShowOpenGPSDialog
import com.efbsm5.easyway.launcher.handlerGPSLauncher
import com.efbsm5.easyway.showToast
import com.efbsm5.easyway.ui.components.RedCenterLoading
import com.efbsm5.easyway.ui.components.requestMultiplePermission
import com.efbsm5.easyway.viewmodel.MultiPointOverlayViewModel
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
 * MultiPointOverlayScreen
 * @author 被风吹过的夏天
 * @email developer_melody@163.com
 * @github: https://github.com/TheMelody/OmniMap
 * created 2022/10/21 11:26
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun MultiPointOverlayScreen() {
    val viewModel: MultiPointOverlayViewModel = viewModel()
    val currentState by viewModel.uiState.collectAsState()
    val markerState = rememberMarkerState()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(LatLng(39.91, 116.40), 3F, 0f, 0f)
    }
    LaunchedEffect(Unit) {
        snapshotFlow { currentState.clickPointLatLng }.filterNotNull()
            .collect { markerState.position = it }
    }
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.onEach {
            if (it is MultiPointOverlayContract.Effect.Toast) {
                showToast(it.msg)
            }
        }.collect()
    }
    val openGpsLauncher = handlerGPSLauncher(viewModel::checkGpsStatus)
    val reqGPSPermission = requestMultiplePermission(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
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
    LaunchedEffect(currentState.locationLatLng) {
        if (null == currentState.locationLatLng) return@LaunchedEffect
        cameraPositionState.move(CameraUpdateFactory.newLatLng(currentState.locationLatLng))
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
            onDismiss = viewModel::hideOpenGPSDialog,
            onPositiveClick = {
                viewModel.openGPSPermission(openGpsLauncher)
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GDMap(
            modifier = Modifier.matchParentSize(),
            uiSettings = currentState.uiSettings,
            cameraPositionState = cameraPositionState,
            properties = currentState.mapProperties,
            onMapLoaded = viewModel::initMultiPointDataAndCheckGps
        ) {
            MultiPointOverlay(
                enable = true,
                icon = currentState.multiPointIcon,
                multiPointItems = currentState.multiPointItems,
                onClick = viewModel::onMultiPointItemClick
            )
            Marker(
                icon = BitmapDescriptorFactory.defaultMarker(),
                state = markerState,
                visible = null != currentState.clickPointLatLng,
                isClickable = false
            )
        }
        if (currentState.isLoading) {
            RedCenterLoading()
        }
    }

}