package com.efbsm5.easyway.ui.page.map

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.efbsm5.easyway.contract.map.MapContract
import com.efbsm5.easyway.contract.map.MapState
import com.efbsm5.easyway.showMsg
import com.efbsm5.easyway.ui.components.mapcards.CardScreen
import com.efbsm5.easyway.ui.components.mapcards.MapPageCard
import com.efbsm5.easyway.viewmodel.mapViewModel.MapPageViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.onEach


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class,
    ExperimentalFoundationApi::class
)
@Preview
@Composable
fun MapPage() {
    val viewmodel: MapPageViewModel = viewModel()
    val currentState by viewmodel.uiState.collectAsState()

    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Collapsed, defineValues = {
            SheetValue.Collapsed at height(0.dp)
            SheetValue.PartiallyExpanded at offset(percent = 60)
            SheetValue.Expanded at contentHeight
        })
    LaunchedEffect(Unit) {
        snapshotFlow { currentState.cardScreen }.drop(1).collect {
            sheetState.animateTo(SheetValue.PartiallyExpanded)
        }
    }
    LaunchedEffect(viewmodel.effect) {
        viewmodel.effect.onEach { effect ->
            when (effect) {
                is MapContract.Effect.Toast -> showMsg(effect.msg)
            }
        }.collect()
    }
    BackHandler(
        enabled = currentState.cardScreen != CardScreen.Function,
        onBack = { viewmodel.onEvent(MapContract.Event.ChangeScreen(CardScreen.Function)) })
//    rememberBottomSheetScaffoldState(sheetState)

    MapScreen(
        sheetState = sheetState,
        sheetContent = {
            MapPageCard(
                onNavigate = { viewmodel.onEvent(MapContract.Event.ChangeState(MapState.Route(it))) },
                content = currentState.cardScreen,
                onChangeScreen = { { viewmodel.onEvent(MapContract.Event.ChangeScreen(it)) } })
        },
        mapPlace = {
            when (currentState.mapState) {
                MapState.LocationState -> {
                    LocationTrackingScreen()
                }

                MapState.PointState -> {
                    MultiPointOverlayScreen(onclick = {
                        viewmodel.onEvent(
                            MapContract.Event.ClickPoint(
                                it
                            )
                        )
                    })
                }

                is MapState.Route -> {
                    RoutePlanScreen()
                }
            }
        },
        onClickChange = { viewmodel.onEvent(MapContract.Event.SwitchMap) },
        onClickLocation = { },
        searchText = currentState.searchBarText,
        editText = {
            viewmodel.onEvent(MapContract.Event.EditText(it))
        },
        search = { viewmodel.onEvent(MapContract.Event.Search) })

}

