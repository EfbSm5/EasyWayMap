//package com.efbsm5.easyway.ui.page
//
//import androidx.activity.compose.BackHandler
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material3.BottomSheetScaffold
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.SheetState
//import androidx.compose.material3.SheetValue
//import androidx.compose.material3.SmallFloatingActionButton
//import androidx.compose.material3.rememberBottomSheetScaffoldState
//import androidx.compose.material3.rememberStandardBottomSheetState
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.amap.api.maps.AMap
//import com.amap.api.maps.LocationSource
//import com.efbsm5.easyway.R
//import com.efbsm5.easyway.ui.components.mapcards.MapPageCard
//import com.efbsm5.easyway.ui.components.mapcards.Screen
//import com.efbsm5.easyway.viewmodel.pageViewmodel.MapPageViewModel
//import com.google.accompanist.permissions.ExperimentalPermissionsApi
//import kotlinx.coroutines.launch
//
//
//@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
//@Composable
//fun MapPage(viewModel: MapPageViewModel) {
//    val state by viewModel.state.collectAsState()
//    val mapState by viewModel.mapState.collectAsState()
//    val sheetState = rememberStandardBottomSheetState(initialValue = SheetValue.PartiallyExpanded)
//    val locationController = LocationController()
//    LaunchedEffect(state) {
//        if (state != Screen.Function) launch { sheetState.expand() }
//    }
//    LaunchedEffect(mapState) {
//        if (mapState !is MapState.Point) launch { sheetState.partialExpand() }
//    }
//    BackHandler(
//        enabled = sheetState.currentValue != SheetValue.PartiallyExpanded, onBack = {
//
//        })
//    BackHandler(
//        enabled = state != Screen.Function, onBack = { viewModel.changeScreen(Screen.Function) })
//    RequestPermission()
//    MapScreen(
//        onChangeScreen = viewModel::changeScreen,
//        sheetState = sheetState,
//        onMarkerClick = viewModel.onMarkerClick,
//        onMapClick = viewModel.onMapClick,
//        onPoiClick = viewModel.onPoiClick,
//        mapState = mapState,
//        onChangeMapState = {
//            viewModel::changeState
//        },
//        locationSource = locationController.getLocationSource(),
//        onAdd = {
//            viewModel.changeScreen(
//                Screen.NewPoint(
//                    label = AppUtils.getContext().getString(R.string.newPoint)
//                )
//            )
//        })
//
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun MapScreen(
//    mapState: MapState,
//    onChangeMapState: (MapState) -> Unit,
//    onChangeScreen: (Screen) -> Unit,
//    sheetState: SheetState,
//    onMarkerClick: AMap.OnMarkerClickListener,
//    onMapClick: AMap.OnMapClickListener,
//    onPoiClick: AMap.OnPOIClickListener,
//    locationSource: LocationSource,
//    onAdd: () -> Unit
//) {
//    BottomSheetScaffold(
//        sheetContent = {
//            MapPageCard(
//                onChangeScreen = onChangeScreen,
//                onNavigate = { onChangeMapState(MapState.Route(it)) })
//        },
//        scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState),
//        sheetPeekHeight = 128.dp
//    ) {
//        SmallFloatingActionButton(onAdd) { Icon(Icons.Default.Add, contentDescription = "add") }
//        GDMap(
//            onMapClick = onMapClick,
//            onPoiClick = onPoiClick,
//            onMarkerClick = onMarkerClick,
//            mapState = mapState,
//            locationSource = locationSource,
//            modifier = Modifier.fillMaxSize()
//        )
//    }
//}
