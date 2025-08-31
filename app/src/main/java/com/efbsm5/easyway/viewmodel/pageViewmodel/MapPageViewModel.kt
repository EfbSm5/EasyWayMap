package com.efbsm5.easyway.viewmodel.pageViewmodel

import com.amap.api.maps.model.LatLng
import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.MapContract
import com.efbsm5.easyway.ui.components.mapcards.CardScreen


class MapPageViewModel : BaseViewModel<MapContract.Event, MapContract.State, MapContract.Effect>() {

    fun setState(mapState: MapState) {
        setEvent(MapContract.Event.ChangeState(mapState))
    }

    fun setScreen(cardScreen: CardScreen) {
        setEvent(MapContract.Event.ChangeScreen(cardScreen))
    }

    fun onAdd() {
        setEvent(MapContract.Event.ChangeScreen(CardScreen.NewPoint("")))
    }

    override fun createInitialState(): MapContract.State {
        return MapContract.State(
            mapState = MapState.PointState,
            cardScreen = CardScreen.Function
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
    data object Loading : MapState
    data class Route(val destination: LatLng) : MapState
}

enum class SheetValue { Collapsed, PartiallyExpanded, Expanded }
