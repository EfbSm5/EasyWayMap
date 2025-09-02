package com.efbsm5.easyway.viewmodel.componentsViewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amap.api.maps.model.LatLng
import com.efbsm5.easyway.data.LocationSaver
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.repo.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewPointCardViewModel(
    private val repository: DataRepository,
    private val locationSaver: LocationSaver
) :
    ViewModel() {
    private var _tempPoint = MutableStateFlow(
        EasyPoint(
            userId = 0,
            name = "",
            type = "",
            info = "",
            location = "",
            photo = ("https://27142293.s21i.faiusr.com/2/ABUIABACGAAg_I_bmQYokt25kQUwwAc4gAU.jpg"),
            refreshTime = "2024-12-29",
            likes = 100,
            dislikes = 10,
            lat = 37.7749,
            lng = -122.4194,
            pointId = 0
        )
    )
    val tempPoint: StateFlow<EasyPoint> = _tempPoint


    fun changeTempPoint(easyPoint: EasyPoint) {
        _tempPoint.value = easyPoint
    }

    fun getLocation(): LatLng {
        return locationSaver.location
    }

    fun publishPoint() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.uploadPoint(_tempPoint.value)
        }
    }
}