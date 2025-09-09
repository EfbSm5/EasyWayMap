package com.efbsm5.easyway.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class ScaffoldController {
    var fabConfig by mutableStateOf<FabConfig?>(null)
        private set
    var topBarConfig by mutableStateOf(TopBarConfig())
        private set
    var bottomBarConfig by mutableStateOf(BottomBarConfig())
        private set

    fun setFab(config: FabConfig?) {
        fabConfig = config
    }

    fun refresh() {
        fabConfig = null
        topBarConfig = TopBarConfig()
//        bottomBarConfig = BottomBarConfig()
    }

    fun setTopBar(config: TopBarConfig) {
        topBarConfig = config
    }

    fun setBottomBar(config: BottomBarConfig) {
        bottomBarConfig = config
    }

    fun clearFab() = setFab(null)
    fun hideAllChrome() {
        topBarConfig = topBarConfig.copy(show = false)
        bottomBarConfig = bottomBarConfig.copy(show = false)
        fabConfig = fabConfig?.copy(visible = false)
    }
}

val LocalScaffoldController = staticCompositionLocalOf<ScaffoldController> {
    error("ScaffoldController not provided")
}

data class FabConfig(
    val icon: ImageVector,
    val onClick: () -> Unit,
    val text: String? = null,
    val extended: Boolean = false,
    val visible: Boolean = false,
    val containerColor: Color? = null,
    val contentColor: Color? = null,
    val offset: Dp = 0.dp
)

data class TopBarConfig(
    val title: String? = null,
    val back: (() -> Unit)? = null,
    val show: Boolean = false
)

data class BottomBarConfig(
    val content: (@Composable () -> Unit)? = null,
    val show: Boolean = true
)
