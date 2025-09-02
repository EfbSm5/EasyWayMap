package com.efbsm5.easyway.contract

import com.efbsm5.easyway.state.IUiEffect
import com.efbsm5.easyway.state.IUiEvent
import com.efbsm5.easyway.state.IUiState
import com.efbsm5.easyway.ui.components.mapcards.CardScreen
import com.efbsm5.easyway.viewmodel.pageViewmodel.MapState

class MapContract {
    sealed class Event : IUiEvent {
        data class ChangeState(val mapState: MapState) : Event()
        data class ChangeScreen(val cardScreen: CardScreen) : Event()
    }

    data class State(
        val mapState: MapState,
        val cardScreen: CardScreen
    ) : IUiState

    sealed class Effect : IUiEffect {
        internal data class Toast(val msg: String) : Effect()
    }
}