package com.efbsm5.easyway.ui.page.map

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.efbsm5.easyway.contract.map.MapRouteContract
import com.efbsm5.easyway.contract.map.MapState
import com.efbsm5.easyway.showMsg
import com.efbsm5.easyway.ui.components.MapBottomSheet
import com.efbsm5.easyway.ui.components.SheetValue
import com.efbsm5.easyway.ui.components.mapcards.CardScreen
import com.efbsm5.easyway.ui.components.mapcards.MapPageCard
import com.efbsm5.easyway.viewmodel.mapViewModel.MapRouteViewModel
import com.efbsm5.easyway.viewmodel.mapViewModel.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class,
    ExperimentalFoundationApi::class
)
@Preview
@Composable
fun MapRoutePage(
    viewmodel: MapRouteViewModel = viewModel()
) {
    val currentState by viewmodel.uiState.collectAsState()

    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Collapsed, defineValues = {
            SheetValue.Collapsed at height(0.dp)
            SheetValue.PartiallyExpanded at contentHeight
            SheetValue.Expanded at height(percent = 100)
        })
    LaunchedEffect(Unit) {
        snapshotFlow { currentState.cardScreen }.drop(1).collect {
            sheetState.animateTo(SheetValue.PartiallyExpanded)
        }
    }
    LaunchedEffect(viewmodel.effect) {
        viewmodel.effect.onEach { effect ->
            when (effect) {
                is MapRouteContract.Effect.Toast -> showMsg(effect.msg)
            }
        }.collect()
    }
    val mapViewModel: MapViewModel = viewModel()

    var collapsing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val sheetNotCollapsed = sheetState.currentValue != SheetValue.Collapsed
    val showOtherScreen = currentState.cardScreen != CardScreen.Function
    BackHandler(enabled = (sheetNotCollapsed || showOtherScreen) && !collapsing) {
        when {
            sheetNotCollapsed -> {
                scope.launch {
                    collapsing = true
                    runCatching {
                        sheetState.animateTo(SheetValue.Collapsed)
                    }
                    collapsing = false
                }
            }

            showOtherScreen -> {
                viewmodel.handleEvents(MapRouteContract.Event.ChangeScreen(CardScreen.Function))
            }
        }
    }

    MapBottomSheet(
        sheetState = sheetState,
        sheetContent = {
            MapPageCard(
                onNavigate = {
                    viewmodel.handleEvents(
                        MapRouteContract.Event.ChangeState(
                            MapState.Route(
                                it
                            )
                        )
                    )
                },
                content = currentState.cardScreen,
                onChangeScreen = { viewmodel.handleEvents(MapRouteContract.Event.ChangeScreen(it)) })
        },
        mapPlace = {
            MapScreen(
                onClick = { viewmodel.handleEvents(MapRouteContract.Event.ClickPoint(it)) },
                mapState = currentState.mapState,
                onChangeState = { viewmodel.handleEvents(MapRouteContract.Event.ChangeState(it)) },
                viewModel = mapViewModel,
                selectedPoint = currentState.selectedPoint
            )
        },
        onClickChange = { viewmodel.handleEvents(MapRouteContract.Event.SwitchMap) },
        onClickLocation = { mapViewModel.moveToLocation() },
        searchText = currentState.searchBarText,
        editText = { viewmodel.handleEvents(MapRouteContract.Event.EditText(it)) },
        search = { viewmodel.handleEvents(MapRouteContract.Event.Search) })

}
