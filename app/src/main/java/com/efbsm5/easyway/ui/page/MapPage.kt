package com.efbsm5.easyway.ui.page

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.efbsm5.easyway.ui.components.mapcards.CardScreen
import com.efbsm5.easyway.ui.components.mapcards.MapPageCard
import com.efbsm5.easyway.viewmodel.pageViewmodel.MapPageViewModel
import com.efbsm5.easyway.viewmodel.pageViewmodel.MapState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class,
    ExperimentalFoundationApi::class
)
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

//    val scope = rememberCoroutineScope()
//    LaunchedEffect(Unit) {
//        scope.launch { sheetState.bottomSheetState.animateTo(BottomSheetValue.HalfExpanded) }
//    }

//backHandler
    BackHandler(
        enabled = currentState.cardScreen != CardScreen.Function,
        onBack = { viewmodel.setScreen(CardScreen.Function) })

    BottomSheetScaffold(
        scaffoldState = rememberBottomSheetScaffoldState(sheetState), sheetContent = {
            MapPageCard(
                onNavigate = { viewmodel.setState(MapState.Route(it)) },
                content = currentState.cardScreen,
                onChangeScreen = viewmodel::setScreen
            )
        },

        //contentChanging

        content = {
            Box(modifier = Modifier.padding(it))
            {

                when (currentState.mapState) {
                    MapState.LocationState -> {
                        LocationTrackingScreen()
                    }

                    MapState.PointState -> {
                        MultiPointOverlayScreen(onclick = {})
                    }

                    is MapState.Route -> {
                        RoutePlanScreen()
                    }
                }
            }
        })
}
