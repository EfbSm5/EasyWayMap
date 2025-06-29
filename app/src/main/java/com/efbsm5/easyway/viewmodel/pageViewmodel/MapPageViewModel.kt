//package com.efbsm5.easyway.viewmodel.pageViewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.amap.api.maps.AMap
//import com.efbsm5.easyway.data.repository.DataRepository
//import com.efbsm5.easyway.map.MapState
//import com.efbsm5.easyway.map.MapUtil
//import com.efbsm5.easyway.ui.components.mapcards.Screen
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//
//
//class MapPageViewModel(
//    val repository: DataRepository
//) : ViewModel() {
//    private val _state = MutableStateFlow<Screen>(Screen.Function)
//    private val _mapState = MutableStateFlow<MapState>(MapState.Point(emptyList()))
//    val state: StateFlow<Screen> = _state
//    val onMarkerClick = AMap.OnMarkerClickListener {
//        viewModelScope.launch(Dispatchers.IO) {
//            changeScreen(
//                Screen.Comment(
//                    easyPoint = repository.getPointFromLatlng(it.position)
//                )
//            )
//        }
//        true
//    }
//    val onPoiClick = AMap.OnPOIClickListener {
//        changeScreen(
//            Screen.Comment(
//                easyPoint = MapUtil.poiToEasyPoint(it)
//            )
//        )
//    }
//    val onMapClick = AMap.OnMapClickListener {}
//    val mapState: StateFlow<MapState> = _mapState
//
//    init {
//        getPoints()
//    }
//
//    fun getPoints() {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.getAllPoints().collect {
//                _mapState.value = MapState.Point(it)
//            }
//        }
//    }
//
//    fun changeScreen(screen: Screen) {
//        _state.value = screen
//    }
//
//    fun changeState(mapState: MapState) {
//        _mapState.value = mapState
//    }
//
//}
