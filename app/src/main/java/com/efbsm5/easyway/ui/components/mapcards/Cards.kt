package com.efbsm5.easyway.ui.components.mapcards

import androidx.compose.runtime.Composable
import com.amap.api.maps.model.LatLng
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.viewmodel.componentsViewmodel.NewPointCardViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MapPageCard(
    content: CardScreen,
    onChangeScreen: (CardScreen) -> Unit,
    onNavigate: (LatLng) -> Unit
) {
    when (content) {
        is CardScreen.Comment -> {
            CommentAndHistoryCard(
                navigate = onNavigate,
                changeScreen = onChangeScreen
            )
        }

        CardScreen.Function -> {
            FunctionCard(
                changeScreen = onChangeScreen,
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