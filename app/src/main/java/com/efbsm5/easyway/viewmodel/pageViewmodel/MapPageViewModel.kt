package com.efbsm5.easyway.viewmodel.pageViewmodel

import com.amap.api.maps.model.LatLng
import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.MapContract
import com.efbsm5.easyway.repo.DataRepository
import com.efbsm5.easyway.ui.components.mapcards.CardScreen


class MapPageViewModel : BaseViewModel<MapContract.Event, MapContract.State, MapContract.Effect>() {

    fun setState(mapState: MapState) {
        setEvent(MapContract.Event.ChangeState(mapState))
    }

    fun setScreen(cardScreen: CardScreen) {
        setEvent(MapContract.Event.ChangeScreen(cardScreen))
    }

    fun changeScreen() {
        if (currentState.mapState == MapState.LocationState) setEvent(
            MapContract.Event.ChangeState(
                MapState.PointState
            )
        ) else setEvent(
            MapContract.Event.ChangeState(
                MapState.LocationState
            )
        )
    }

    fun clickPoint(latLng: LatLng) {
        asyncLaunch {
            val point = DataRepository.getPointFromLatLng(latLng)
            setState { copy(cardScreen = CardScreen.Comment(point)) }
            setEffect { MapContract.Effect.Toast("click ${point.name}") }
        }
    }

    fun navigate(latLng: LatLng) {
        setState(MapState.Route(latLng))
    }

    fun onAdd() {
        setEvent(MapContract.Event.ChangeScreen(CardScreen.NewPoint("")))
    }

    override fun createInitialState(): MapContract.State {
        return MapContract.State(
            mapState = MapState.PointState, cardScreen = CardScreen.Function
        )
    }

    override fun handleEvents(event: MapContract.Event) {
        when (event) {
            is MapContract.Event.ChangeState -> setState { copy(mapState = event.mapState) }
            is MapContract.Event.ChangeScreen -> setState { copy(cardScreen = event.cardScreen) }
        }
    }

}

sealed interface MapState {
    data object PointState : MapState
    data object LocationState : MapState
    data class Route(val destination: LatLng) : MapState
}
