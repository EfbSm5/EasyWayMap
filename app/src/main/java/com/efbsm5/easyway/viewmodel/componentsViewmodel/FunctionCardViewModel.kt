package com.efbsm5.easyway.viewmodel.componentsViewmodel

import com.efbsm5.easyway.base.BaseViewModel
import com.efbsm5.easyway.contract.FunctionCardContract
import com.efbsm5.easyway.model.ImmutableListWrapper
import kotlinx.coroutines.Dispatchers

class FunctionCardViewModel :
    BaseViewModel<FunctionCardContract.Event, FunctionCardContract.State, FunctionCardContract.Effect>() {

    fun search(string: String) {
        asyncLaunch(Dispatchers.IO) {
            searchForPoi(
                string, context = context, onPoiSearched = { _poiList.value = it })
            repository.getPointByName(string).collect {
                _points.value = it
            }
        }
    }

    override fun createInitialState(): FunctionCardContract.State {
        return FunctionCardContract.State(
            points = ImmutableListWrapper(emptyList()),
            poiList = ImmutableListWrapper(emptyList())
        )
    }

    override fun handleEvents(event: FunctionCardContract.Event) {

    }


}

