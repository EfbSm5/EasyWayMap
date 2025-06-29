package com.efbsm5.easyway.ui.components.mapcards

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.amap.api.maps.model.LatLng
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.viewmodel.componentsViewmodel.CommentAndHistoryCardViewModel
import com.efbsm5.easyway.viewmodel.componentsViewmodel.FunctionCardViewModel
import com.efbsm5.easyway.viewmodel.componentsViewmodel.NewPointCardViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MapPageCard(
    onNavigate: (LatLng) -> Unit, onChangeScreen: (Screen) -> Unit
) {
    var content by remember { mutableStateOf<Screen>(Screen.Function) }
    when (content) {
        is Screen.Comment -> {
            val commentAndHistoryCardViewModel: CommentAndHistoryCardViewModel =
                koinViewModel(parameters = { parametersOf((content as Screen.Comment).easyPoint) })
            CommentAndHistoryCard(
                viewModel = commentAndHistoryCardViewModel,
                navigate = onNavigate,
                changeScreen = onChangeScreen
            )
        }

        Screen.Function -> {
            val functionCardViewModel: FunctionCardViewModel = koinViewModel()
            FunctionCard(
                viewModel = functionCardViewModel, changeScreen = onChangeScreen,
                navigate = onNavigate,
            )
        }

        is Screen.NewPoint -> {
            val newPointCardViewModel: NewPointCardViewModel = koinViewModel()
            NewPointCard(
                viewModel = newPointCardViewModel,
                changeScreen = onChangeScreen,
                label = (content as Screen.NewPoint).label
            )
        }
    }
}

sealed interface Screen {
    data object Function : Screen
    data class NewPoint(val label: String) : Screen
    data class Comment(val easyPoint: EasyPoint) : Screen
}