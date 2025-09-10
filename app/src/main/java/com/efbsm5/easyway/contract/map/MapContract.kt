package com.efbsm5.easyway.contract.map

import androidx.compose.runtime.Immutable
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MultiPointItem
import com.efbsm5.easyway.state.IUiEffect
import com.efbsm5.easyway.state.IUiEvent
import com.efbsm5.easyway.state.IUiState
import com.efbsm5.easyway.ui.components.mapcards.CardScreen

class MapContract {
    sealed class Event : IUiEvent {
        data class ChangeState(val mapState: MapState) : Event()
        data class ChangeScreen(val cardScreen: CardScreen) : Event()
        data object AddPoint : Event()
        data class ClickPoint(val multiPointItem: MultiPointItem) : Event()
        data object SwitchMap : Event()
        data class Navigate(val latLng: LatLng) : Event()
        data class EditText(val string: String) : Event()
        data object Search : Event()
    }

    @Immutable
    data class State(
        val mapState: MapState = MapState.LocationState,
        val cardScreen: CardScreen = CardScreen.Function,
        val searchBarText: String = ""
    ) : IUiState

    sealed class Effect : IUiEffect {
        internal data class Toast(val msg: String) : Effect()
    }
}

sealed interface MapState {
    data object LocationState : MapState
    data class Route(val destination: LatLng) : MapState
}