package com.efbsm5.easyway.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun TabSection(
    tabs: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    scrollable: Boolean = false,
    enableDivider: Boolean = false,
    indicator: @Composable (tabPositions: List<TabPosition>) -> Unit = { tabPositions ->
        if (tabPositions.isNotEmpty()) {
            TabRowDefaults.Indicator(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedIndex])
                    .height(3.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    },
    tabTextStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    tabPadding: PaddingValues = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
    edgePadding: Dp = 0.dp, // 针对 ScrollableTabRow
    onLongPress: ((index: Int) -> Unit)? = null,
    // 可扩展：支持构建自定义内容（如果非 null 用这个）
    tabContent: (@Composable (index: Int, text: String, selected: Boolean) -> Unit)? = null,
) {
    if (tabs.isEmpty()) {
        // 可选：占位 或 直接 return
        return
    }
    // 防止外部传了越界 index（比如 tabs 列表刷新后长度变短）
    val realSelected = selectedIndex.coerceIn(0, tabs.lastIndex)

    val tabRow: @Composable (@Composable () -> Unit) -> Unit = { tabsContent ->
        if (scrollable) {
            ScrollableTabRow(
                selectedTabIndex = realSelected,
                modifier = modifier,
                edgePadding = edgePadding,
                indicator = indicator,
                divider = {
                    if (enableDivider) {
                        Divider(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }) {
                tabsContent()
            }
        } else {
            TabRow(
                selectedTabIndex = realSelected,
                modifier = modifier,
                indicator = indicator,
                divider = {
                    if (enableDivider) {
                        Divider(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }) {
                tabsContent()
            }
        }
    }

    tabRow {
        tabs.forEachIndexed { index, title ->
            val selected = index == realSelected
            // 处理长按（可选）
            val interactionSource = remember { MutableInteractionSource() }
            val pressModifier = if (onLongPress != null) {
                Modifier.pointerInput(onLongPress) {
                    detectTapGestures(
                        onLongPress = { onLongPress(index) },
                        onTap = { onSelect(index) })
                }
            } else {
                Modifier.clickable(
                    indication = null, interactionSource = interactionSource
                ) { onSelect(index) }
            }

            Tab(
                selected = selected,
                onClick = { onSelect(index) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                interactionSource = interactionSource
            ) {
                Box(
                    modifier = pressModifier
                        .padding(tabPadding)
                        .semantics {
                            contentDescription = "$title, ${if (selected) "已选中" else "未选中"}"
                        }, contentAlignment = Alignment.Center
                ) {
                    if (tabContent != null) {
                        tabContent(index, title, selected)
                    } else {
                        Text(
                            text = title,
                            style = tabTextStyle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
