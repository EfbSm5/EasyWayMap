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

import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MultiPointItem
import com.efbsm5.easyway.BuildConfig
import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.map.MultiPointOverlayContract
import com.efbsm5.easyway.data.WuhanBoxGenerator
import com.efbsm5.easyway.repo.DataRepository
import com.efbsm5.easyway.repo.MultiPointOverlayRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map

/**
 * MultiPointOverlayViewModel
 * @author 被风吹过的夏天
 * @email developer_melody@163.com
 * @github: https://github.com/TheMelody/OmniMap
 * created 2022/10/21 11:21
 */
class MultiPointOverlayViewModel :
    BaseViewModel<MultiPointOverlayContract.Event, MultiPointOverlayContract.State, MultiPointOverlayContract.Effect>() {
    override fun createInitialState(): MultiPointOverlayContract.State {
        return MultiPointOverlayContract.State(
            isLoading = true,
            clickPointLatLng = null,
            uiSettings = MultiPointOverlayRepository.initMapUiSettings(),
            multiPointIcon = MultiPointOverlayRepository.initMultiPointIcon(),
            multiPointItems = emptyList()
        )
    }

    override fun handleEvents(event: MultiPointOverlayContract.Event) {
        when (event) {
            is MultiPointOverlayContract.Event.MultiPointClick -> {
                setState { copy(clickPointLatLng = event.pointItem.latLng) }
            }
        }
    }

    fun initMultiPointData() = asyncLaunch(Dispatchers.IO) {
        if (BuildConfig.DEBUG) {
            val list: MutableList<MultiPointItem> = mutableListOf()
            try {
                repeat(10) {
                    list.add(MultiPointItem(WuhanBoxGenerator.random()))
                }
            } catch (e: Exception) {

            }
            setState {
                copy(
                    isLoading = false, multiPointItems = list
                )
            }
        } else {
            DataRepository.getAllPoints().map { pointSimplifies ->
                pointSimplifies.map { pointSimplify ->
                    MultiPointItem(LatLng(pointSimplify.lat, pointSimplify.lng))
                }
            }.collect {
                setState {
                    copy(
                        isLoading = false, multiPointItems = it
                    )
                }
            }
        }


    }

    fun onMultiPointItemClick(pointItem: MultiPointItem) {
        setEvent(MultiPointOverlayContract.Event.MultiPointClick(pointItem))
    }
}