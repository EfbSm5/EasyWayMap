package com.efbsm5.easyway.ui.components.mapcards

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.amap.api.maps.model.LatLng
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.viewmodel.cardViewmodel.CommentAndHistoryCardViewModel
import com.efbsm5.easyway.viewmodel.cardViewmodel.FunctionCardViewModel

@Composable
fun MapPageCard(
    content: CardScreen,
    onChangeScreen: (CardScreen) -> Unit,
    onNavigate: (LatLng) -> Unit
) {
    when (content) {
        is CardScreen.Comment -> {
            val viewModel: CommentAndHistoryCardViewModel = viewModel()
            CommentAndHistoryCard(
                point = content.easyPoint,
                navigate = onNavigate,
                changeScreen = onChangeScreen,
                viewModel = viewModel
            )
        }

        CardScreen.Function -> {
            val viewModel: FunctionCardViewModel = viewModel()
            FunctionCard(
                changeScreen = onChangeScreen,
                navigate = onNavigate,
                viewModel = viewModel
            )
        }

        is CardScreen.NewPoint -> {
            NewPointCard(
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