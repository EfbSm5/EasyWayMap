package com.efbsm5.easyway.ui.page.map

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.efbsm5.easyway.contract.map.MapContract
import com.efbsm5.easyway.showMsg
import com.efbsm5.easyway.ui.components.mapcards.CardScreen
import com.efbsm5.easyway.ui.components.mapcards.MapPageCard
import com.efbsm5.easyway.viewmodel.mapViewModel.MapPageViewModel
import com.efbsm5.easyway.viewmodel.mapViewModel.MapState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import io.morfly.compose.bottomsheet.material3.requireSheetVisibleHeightDp
import kotlinx.coroutines.flow.collect
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
        initialValue = SheetValue.PartiallyExpanded, defineValues = {
            SheetValue.Hidden at height(100.dp)
            SheetValue.PartiallyExpanded at offset(percent = 50)
            SheetValue.Expanded at contentHeight
        })
    val bottomPadding by remember { derivedStateOf { sheetState.requireSheetVisibleHeightDp() } }
    LaunchedEffect(currentState.cardScreen) {
        sheetState.animateTo(SheetValue.PartiallyExpanded)
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
        onBack = { viewmodel.setScreen(CardScreen.Function) })

    BottomSheetScaffold(
        scaffoldState = rememberBottomSheetScaffoldState(sheetState),
        sheetContent = {
            MapPageCard(
                onNavigate = viewmodel::navigate,
                content = currentState.cardScreen,
                onChangeScreen = viewmodel::setScreen
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(y = -bottomPadding - 20.dp),
                    onClick = viewmodel::changeScreen,
                    content = { Icon(Icons.Default.LocationOn, "change map") })
                when (currentState.mapState) {
                    MapState.LocationState -> {
//                        LocationTrackingScreen()
                    }

                    MapState.PointState -> {
                        MultiPointOverlayScreen(onclick = viewmodel::clickPoint)
                    }

                    is MapState.Route -> {
//                        RoutePlanScreen()
                    }
                }
            }
        })
}

