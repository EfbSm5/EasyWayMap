package com.efbsm5.easyway.viewmodel.mapViewModel

import com.amap.api.maps.model.LatLng
import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.map.RoutePlanContract
import com.efbsm5.easyway.repo.RoutePlanRepository
import kotlinx.coroutines.Dispatchers

/**
 * RoutePlanViewModel
 * @author 被风吹过的夏天
 * @email developer_melody@163.com
 * @github: https://github.com/TheMelody/OmniMap
 * created 2022/10/14 15:02
 */
class RoutePlanViewModel :
    BaseViewModel<RoutePlanContract.Event, RoutePlanContract.State, RoutePlanContract.Effect>() {

    override fun createInitialState(): RoutePlanContract.State {
        return RoutePlanContract.State(
            isLoading = false,
            queryStartPoint = LatLng(39.942295, 116.335891),
            queryEndPoint = LatLng(39.995576, 116.481288),
            uiSettings = RoutePlanRepository.initMapUiSettings(),
            mapProperties = RoutePlanRepository.initMapProperties(),
            dataState = null
        )
    }

    override fun handleEvents(event: RoutePlanContract.Event) {
        when (event) {
            is RoutePlanContract.Event.RoadTrafficClick -> {
                setState { copy(mapProperties = mapProperties.copy(isTrafficEnabled = !mapProperties.isTrafficEnabled)) }
            }

            is RoutePlanContract.Event.QueryRoutePlan -> {
                setState { copy(isLoading = true, dataState = null) }
                asyncLaunch(Dispatchers.IO) {
                    val result = runCatching {
                        RoutePlanRepository.getRoutePlanResult(
                            queryType = event.queryType,
                            startPoint = currentState.queryStartPoint,
                            endPoint = currentState.queryEndPoint,
                            // 北京的城市区号：10
                            cityCode = "10"
                        )
                    }
                    if (result.isSuccess) {
                        setState { copy(isLoading = false, dataState = result.getOrNull()) }
                    } else {
                        setState { copy(isLoading = false) }
                        setEffect { RoutePlanContract.Effect.Toast(result.exceptionOrNull()?.message) }
                    }
                }
            }
        }
    }

    fun queryRoutePlan(queryType: Int = 0) {
        setEvent(RoutePlanContract.Event.QueryRoutePlan(queryType))
    }

    fun switchRoadTraffic() {
        setEvent(RoutePlanContract.Event.RoadTrafficClick)
    }
}