package com.efbsm5.easyway.viewmodel.mapViewModel

import com.amap.api.maps.model.MultiPointItem
import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.map.MapRouteContract
import com.efbsm5.easyway.contract.map.MapRouteContract.Event.ChangeScreen
import com.efbsm5.easyway.contract.map.MapState.Route
import com.efbsm5.easyway.data.models.assistModel.EasyPointSimplify
import com.efbsm5.easyway.repo.DataRepository
import com.efbsm5.easyway.ui.components.mapcards.CardScreen
import com.efbsm5.easyway.ui.components.mapcards.CardScreen.NewPoint
import kotlinx.coroutines.Dispatchers


class MapRouteViewModel :
    BaseViewModel<MapRouteContract.Event, MapRouteContract.State, MapRouteContract.Effect>() {

    fun setEffect(effect: MapRouteContract.Effect) {
        setEffect(effect)
    }

    private fun clickPoint(multiPointItem: MultiPointItem) {
        if (multiPointItem.`object` is EasyPointSimplify) {
            asyncLaunch(Dispatchers.IO) {
                val r =
                    DataRepository.getPointFromLatLng(multiPointItem.`object` as EasyPointSimplify)
                r.onSuccess {
                    val next = currentState.clickNonce + 1
                    setState {
                        copy(
                            clickNonce = next,
                            cardScreen = CardScreen.Comment(it, nonce = next),
                            selectedPoint = multiPointItem
                        )
                    }
                    setEffect { MapRouteContract.Effect.Toast("click ${it.name}") }
                }.onFailure {
                    setEffect { MapRouteContract.Effect.Toast("error") }
                }
            }
        } else {
            setState { copy(selectedPoint = multiPointItem) }
        }
    }

    private fun search() {

    }

    override fun createInitialState(): MapRouteContract.State {
        return MapRouteContract.State()
    }

    override fun handleEvents(event: MapRouteContract.Event) {
        when (event) {
            is MapRouteContract.Event.ChangeState -> setState { copy(mapState = event.mapState) }
            is ChangeScreen -> setState { copy(cardScreen = event.cardScreen) }
            MapRouteContract.Event.AddPoint -> setEvent(
                ChangeScreen(NewPoint(""))
            )

            is MapRouteContract.Event.ClickPoint -> clickPoint(event.multiPointItem)
            MapRouteContract.Event.SwitchMap -> {}
            is MapRouteContract.Event.Navigate -> setState { copy(mapState = Route(event.latLng)) }
            is MapRouteContract.Event.EditText -> setState { copy(searchBarText = event.string) }
            MapRouteContract.Event.Search -> search()
        }
    }


}


