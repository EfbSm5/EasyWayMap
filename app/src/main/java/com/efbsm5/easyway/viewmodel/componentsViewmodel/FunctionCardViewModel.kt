package com.efbsm5.easyway.viewmodel.componentsViewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amap.api.services.core.PoiItemV2
import com.efbsm5.easyway.LocationSaver
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.repo.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FunctionCardViewModel(val repository: DataRepository, val locationSaver: LocationSaver) :
    ViewModel() {
    private val _poiList = MutableStateFlow(emptyList<PoiItemV2>())
    private val _points = MutableStateFlow(emptyList<EasyPoint>())
    val points: StateFlow<List<EasyPoint>> = _points
    val poiList: StateFlow<List<PoiItemV2>> = _poiList

    fun search(string: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
//            searchForPoi(
//                string, context = context, onPoiSearched = { _poiList.value = it })
//            repository.getPointByName(string).collect {
//                _points.value = it
//            }
        }
    }




}

