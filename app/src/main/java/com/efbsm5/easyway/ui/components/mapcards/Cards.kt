package com.efbsm5.easyway.ui.components.mapcards

import androidx.compose.runtime.Composable
import com.amap.api.maps.model.LatLng
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.viewmodel.componentsViewmodel.CommentAndHistoryCardViewModel
import com.efbsm5.easyway.viewmodel.componentsViewmodel.FunctionCardViewModel
import com.efbsm5.easyway.viewmodel.componentsViewmodel.NewPointCardViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MapPageCard(
    content: CardScreen,
    onChangeScreen: (CardScreen) -> Unit,
    onNavigate: (LatLng) -> Unit
) {
    when (content) {
        is CardScreen.Comment -> {
            val commentAndHistoryCardViewModel: CommentAndHistoryCardViewModel =
                koinViewModel(parameters = { parametersOf(content.easyPoint) })
            CommentAndHistoryCard(
                viewModel = commentAndHistoryCardViewModel,
                navigate = onNavigate,
                changeScreen = onChangeScreen
            )
        }

        CardScreen.Function -> {
            val functionCardViewModel: FunctionCardViewModel = koinViewModel()
            FunctionCard(
                viewModel = functionCardViewModel, changeScreen = onChangeScreen,
                navigate = onNavigate,
            )
        }

        is CardScreen.NewPoint -> {
            val newPointCardViewModel: NewPointCardViewModel = koinViewModel()
            NewPointCard(
                viewModel = newPointCardViewModel,
                changeScreen = onChangeScreen,
                label = content.label
            )
        }
    }
}

sealed interface CardScreen {
    data object Function : CardScreen
    data class NewPoint(val label: String) : CardScreen
    data class Comment(val easyPoint: EasyPoint) : CardScreen
}