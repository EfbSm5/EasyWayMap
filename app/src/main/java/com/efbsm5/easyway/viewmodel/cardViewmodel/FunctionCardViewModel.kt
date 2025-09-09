package com.efbsm5.easyway.viewmodel.cardViewmodel

import com.amap.api.services.core.PoiItemV2
import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.card.FunctionCardContract
import com.efbsm5.easyway.model.ImmutableListWrapper
import com.efbsm5.easyway.repo.OnPoiSearched
import com.efbsm5.easyway.repo.searchForPoi
import kotlinx.coroutines.Dispatchers

class FunctionCardViewModel :
    BaseViewModel<FunctionCardContract.Event, FunctionCardContract.State, FunctionCardContract.Effect>() {

    fun search(string: String, page: Int) {
        asyncLaunch(Dispatchers.IO) {
            searchForPoi(
                keyword = string, page = page, onPoiSearched = object : OnPoiSearched {
                    override fun onPoiSearched(result: List<PoiItemV2>) {
                        setState { copy(poiList = ImmutableListWrapper(result)) }
                    }
                })
        }

    }

    fun handlePhotoUri() {

    }

    override fun createInitialState(): FunctionCardContract.State {
        return FunctionCardContract.State(
            points = ImmutableListWrapper(emptyList()), poiList = ImmutableListWrapper(emptyList())
        )
    }

    override fun handleEvents(event: FunctionCardContract.Event) {

    }


}

