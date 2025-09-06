package com.efbsm5.easyway.contract.card

import com.amap.api.services.core.PoiItemV2
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.model.ImmutableListWrapper
import com.efbsm5.easyway.state.IUiEffect
import com.efbsm5.easyway.state.IUiEvent
import com.efbsm5.easyway.state.IUiState

class FunctionCardContract {
    sealed class Event : IUiEvent

    data class State(
        val points: ImmutableListWrapper<EasyPoint>,
        val poiList: ImmutableListWrapper<PoiItemV2>
    ) : IUiState

    sealed class Effect : IUiEffect {
        class Search(val searchString: String) : Effect()
    }
}