package com.efbsm5.easyway.ui.page.map

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.efbsm5.easyway.ui.components.SearchBar
import io.morfly.compose.bottomsheet.material3.BottomSheetScaffold
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetScaffoldState
import io.morfly.compose.bottomsheet.material3.rememberBottomSheetState
import kotlin.math.pow

enum class SheetValue { Collapsed, PartiallyExpanded, Expanded }

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MapScreen(
    sheetContent: @Composable (ColumnScope.() -> Unit) = {},
    mapPlace: @Composable (() -> Unit) = {},
    onClickChange: () -> Unit = {},
    onClickLocation: () -> Unit = {},
    searchText: String = "",
    onSearchText: (String) -> Unit = {},
    search: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    rememberCoroutineScope()

    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    val collapsedHeightDp = 100.dp
    val collapsedHeightPx = with(density) { collapsedHeightDp.toPx() }
    val collapsedOffsetPx = screenHeightPx - collapsedHeightPx  // 顶部到 Collapsed 顶部的距离

    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded, defineValues = {
            SheetValue.Collapsed at height(collapsedHeightDp)
            SheetValue.PartiallyExpanded at offset(percent = 60)
            SheetValue.Expanded at contentHeight
        })
    val scaffoldState = rememberBottomSheetScaffoldState(sheetState)

    val partialOffsetPx = screenHeightPx * 0.60f

    var expandedOffsetPx by remember { mutableStateOf<Float?>(null) }

    LaunchedEffect(sheetState.currentValue, sheetState.targetValue) {
        if ((sheetState.currentValue == SheetValue.Expanded || sheetState.targetValue == SheetValue.Expanded) && expandedOffsetPx == null) {
            expandedOffsetPx = sheetState.offset
        }
    }

    LaunchedEffect(configuration) {
        if (sheetState.currentValue == SheetValue.Expanded) {
            expandedOffsetPx = sheetState.offset
        }
    }

    val currentOffsetPx by remember { derivedStateOf { sheetState.offset } }

    // 按钮淡出逻辑（保持原样）
    val rawButtonsAlpha by remember {
        derivedStateOf {
            val exp = expandedOffsetPx
            if (exp == null) {
                1f
            } else {
                val tolerance = 1f
                when {
                    currentOffsetPx >= partialOffsetPx - tolerance -> 1f
                    currentOffsetPx <= exp + tolerance -> 0f
                    else -> {
                        val progress = (currentOffsetPx - exp) / (partialOffsetPx - exp)
                        val eased = progress.coerceIn(0f, 1f)
                        1f - eased.pow(2)
                    }
                }
            }
        }
    }
    val buttonsAlpha by animateFloatAsState(rawButtonsAlpha, label = "buttonsAlpha")

    // 搜索栏淡出：只在 PartiallyExpanded → Collapsed 之间渐隐；在 Expanded 和 Partial 都完全显示
    val rawSearchBarAlpha by remember {
        derivedStateOf {
            val tolerance = 1f
            when {
                // 在部分展开或更上（包含 Expanded） => 完全显示
                currentOffsetPx <= partialOffsetPx + tolerance -> 1f
                // 在完全收回或更下（理论上不会更下） => 隐藏
                currentOffsetPx >= collapsedOffsetPx - tolerance -> 0f
                else -> {
                    val progress =
                        (currentOffsetPx - partialOffsetPx) / (collapsedOffsetPx - partialOffsetPx)
                    // 线性或使用 1 - progress^2
                    1f - progress.coerceIn(0f, 1f)
                }
            }
        }
    }
    val searchBarAlpha by animateFloatAsState(rawSearchBarAlpha, label = "searchBarAlpha")

//     位移动画：
    val searchBarTranslationDp by animateFloatAsState(
        targetValue = -(1f - searchBarAlpha) * 20f, label = "searchBarTranslation"
    )

    val buttonGroupHeightPx = with(density) { 124.dp.toPx() }
    val buttonMarginPx = with(density) { 12.dp.toPx() }
    val minTopPx = with(density) { 108.dp.toPx() }
    val buttonsY by remember {
        derivedStateOf {
            (currentOffsetPx - buttonGroupHeightPx - buttonMarginPx).coerceAtLeast(minTopPx)
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetDragHandle = { DragHandle() },
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = Color.White,
        sheetContent = {
            sheetContent()
//            SheetContent(
//                onCollapsed = { scope.launch { sheetState.snapTo(SheetValue.Collapsed) } },
//                onPartial = { scope.launch { sheetState.animateTo(SheetValue.PartiallyExpanded) } },
//                onExpand = { scope.launch { sheetState.animateTo(SheetValue.Expanded) } })
        }) { innerPadding ->

        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
//            MapPlaceholder()
            mapPlace()
            // 搜索栏 + alpha
            SearchBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .alpha(searchBarAlpha)
                    .offset {
                        IntOffset(
                            0, with(density) { searchBarTranslationDp.dp.toPx() }.toInt()
                        )
                    },
                searchBarText = searchText,
                onChange = { onSearchText(it) },
                onSearch = { search() },
                placeHolder = "搜索地点、公交、地铁..."
            )

            FloatingFollowButtons(
                alpha = buttonsAlpha,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset {
                        IntOffset(
                            x = (-16).dp.roundToPx(), y = buttonsY.toInt()
                        )
                    },
                onClickLocation = { onClickLocation() },
                onClickChange = { onClickChange() })

            // 调试信息（需要时打开）
            // Text(
            //     "offset=${currentOffsetPx.toInt()} partial=${partialOffsetPx.toInt()} collapsed=${collapsedOffsetPx.toInt()}",
            //     modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp)
            // )
        }
    }
}

//@Composable
//private fun MapPlaceholder() {
//    Box(
//        Modifier
//            .fillMaxSize()
//            .background(Color(0xFFE0E0E0)), contentAlignment = Alignment.Center
//    ) {
//        Text("Map Placeholder", color = Color.DarkGray)
//    }
//}


@Composable
private fun FloatingFollowButtons(
    alpha: Float,
    modifier: Modifier = Modifier,
    onClickLocation: () -> Unit,
    onClickChange: () -> Unit
) {
    Column(
        modifier = modifier
            .width(56.dp)
            .alpha(alpha),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircleActionButton("定位") { onClickLocation() }
        CircleActionButton("图层") { onClickChange() }
    }
}

@Composable
private fun CircleActionButton(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(56.dp)
            .clickable { onClick() },
        shape = CircleShape,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        color = Color.White
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}
//
//@Composable
//private fun SheetContent(
//    onCollapsed: () -> Unit, onPartial: () -> Unit, onExpand: () -> Unit
//) {
//
//    Column(
//        Modifier
//            .fillMaxWidth()
//            .navigationBarsPadding()
//            .imePadding()
//            .padding(top = 8.dp)
//    ) {
//        Text(
//            "附近地点",
//            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
//            fontSize = 20.sp,
//            fontWeight = FontWeight.Bold
//        )
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(1f, fill = false),
//            contentPadding = PaddingValues(bottom = 16.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            items((0 until 12).toList()) { idx ->
//                Row(
//                    Modifier
//                        .fillMaxWidth()
//                        .height(64.dp)
//                        .padding(horizontal = 16.dp)
//                        .background(Color(0xFFF5F5F5), RoundedCornerShape(14.dp))
//                        .padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Box(
//                        Modifier
//                            .size(40.dp)
//                            .background(Color(0xFF90CAF9), CircleShape)
//                    )
//                    Spacer(Modifier.width(12.dp))
//                    Column(Modifier.weight(1f)) {
//                        Text("示例地点 #$idx", fontWeight = FontWeight.SemiBold)
//                        Text("描述信息...", fontSize = 12.sp, color = Color.Gray)
//                    }
//                    Text(">", color = Color.Gray)
//                }
//            }
//        }
//        Row(
//            Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp, vertical = 12.dp),
//            horizontalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            ActionChip("收起", onCollapsed)
//            ActionChip("中间", onPartial)
//            ActionChip("展开", onExpand)
//        }
//    }
//}

@Composable
private fun ActionChip(text: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick, shape = RoundedCornerShape(24.dp), color = Color(0xFFE0E0E0)
    ) {
        Box(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(text, fontSize = 14.sp)
        }
    }
}

@Composable
private fun DragHandle() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .size(width = 48.dp, height = 4.dp)
                .background(Color.LightGray, RoundedCornerShape(2.dp))
        )
    }
}