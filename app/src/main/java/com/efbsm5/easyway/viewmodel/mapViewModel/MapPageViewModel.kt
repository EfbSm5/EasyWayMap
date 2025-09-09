package com.efbsm5.easyway.viewmodel.mapViewModel

import com.amap.api.maps.model.LatLng
import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.map.MapContract
import com.efbsm5.easyway.contract.map.MapContract.Event.ChangeScreen
import com.efbsm5.easyway.contract.map.MapState
import com.efbsm5.easyway.contract.map.MapState.Route
import com.efbsm5.easyway.repo.DataRepository
import com.efbsm5.easyway.ui.components.mapcards.CardScreen
import com.efbsm5.easyway.ui.components.mapcards.CardScreen.NewPoint


class MapPageViewModel : BaseViewModel<MapContract.Event, MapContract.State, MapContract.Effect>() {
    fun setEffect(effect: MapContract.Effect) {
        setEffect(effect)
    }

    fun onEvent(event: MapContract.Event) {
        setEvent(event)
    }

    private fun changeScreen() {
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

    private fun clickPoint(latLng: LatLng) {
        asyncLaunch {
            val r = DataRepository.getPointFromLatLng(latLng)
            r.onSuccess {
                setState { copy(cardScreen = CardScreen.Comment(it)) }
                setEffect { MapContract.Effect.Toast("click ${it.name}") }
            }.onFailure {
                setEffect { MapContract.Effect.Toast("error") }
            }

        }
    }

    private fun search() {

    }

    override fun createInitialState(): MapContract.State {
        return MapContract.State()
    }

    override fun handleEvents(event: MapContract.Event) {
        when (event) {
            is MapContract.Event.ChangeState -> setState { copy(mapState = event.mapState) }
            is ChangeScreen -> setState { copy(cardScreen = event.cardScreen) }
            MapContract.Event.AddPoint -> setEvent(
                ChangeScreen(NewPoint(""))
            )

            is MapContract.Event.ClickPoint -> clickPoint(event.latLng)
            MapContract.Event.SwitchMap -> changeScreen()
            is MapContract.Event.Navigate -> setState { copy(mapState = Route(event.latLng)) }
            is MapContract.Event.EditText -> setState { copy(searchBarText = event.string) }
            MapContract.Event.Search -> search()
        }
    }


}


