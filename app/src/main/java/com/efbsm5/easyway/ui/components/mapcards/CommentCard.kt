package com.efbsm5.easyway.ui.components.mapcards

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.amap.api.maps.model.LatLng
import com.efbsm5.easyway.R
import com.efbsm5.easyway.contract.card.CommentAndHistoryCardContract
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.assistModel.PointCommentAndUser
import com.efbsm5.easyway.getLatlng
import com.efbsm5.easyway.ui.components.LikeAndDisLikeButton
import com.efbsm5.easyway.ui.components.TabSection
import com.efbsm5.easyway.ui.components.mapcards.CardScreen.NewPoint
import com.efbsm5.easyway.viewmodel.cardViewmodel.CommentAndHistoryCardViewModel
import com.efbsm5.easyway.viewmodel.cardViewmodel.CommentCardScreen
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach


@Composable
fun CommentAndHistoryCard(
    navigate: (LatLng) -> Unit,
    changeScreen: (CardScreen) -> Unit,
    viewModel: CommentAndHistoryCardViewModel = viewModel()
) {
    val currentState by viewModel.uiState.collectAsState()
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.onEach {
            when (it) {
                CommentAndHistoryCardContract.Effect.Back -> changeScreen(CardScreen.Function)
                CommentAndHistoryCardContract.Effect.Update -> changeScreen(NewPoint("New point"))
                CommentAndHistoryCardContract.Effect.Comment -> viewModel::publish
            }
        }.collect()
    }
    CommentAndHistoryInnerScreen(
        state = currentState,
        onEvent = viewModel::onEvent
    )
}


@Composable
private fun CommentAndHistoryInnerScreen(
    state: CommentAndHistoryCardContract.State,
    onEvent: (CommentAndHistoryCardContract.Event) -> Unit
) {
    // 局部 UI 控制状态
    var showCommentSheet by rememberSaveable { mutableStateOf(false) }
    var navTarget by rememberSaveable { mutableStateOf<LatLng?>(null) }
    var navName by rememberSaveable { mutableStateOf<String?>(null) }
    var previewImage by remember { mutableStateOf<String?>(null) }

    // 容器：使用 Box 叠层（底 → 内容 → 底部操作 → Sheet/Dialog）
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // 主内容可滚动
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp), // 为底部按钮留空间
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // 头部信息卡片
            item(key = "header") {
                PointHeaderCard(
                    easyPoint = state.point,
                    onNavigateClick = {
                        navTarget = state.point.getLatlng()
                        navName = state.point.name
                    },
                    onLike = { onEvent(CommentAndHistoryCardContract.Event.LikePoint(it)) },
                    onDislike = { onEvent(CommentAndHistoryCardContract.Event.DislikePoint(it)) },
                    onImageClick = { previewImage = state.point.photo },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            // Tab
            item(key = "tabs") {
                TabSection(
                    selectedIndex = if (state.state == CommentCardScreen.Comment) 0 else 1,
                    tabs = listOf("评论", "历史"),
                    onSelect = { onEvent(CommentAndHistoryCardContract.Event.ChangeState(if (it == 0) CommentCardScreen.Comment else CommentCardScreen.History)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            // 内容区
            when (state.state) {
                CommentCardScreen.Loading -> {
                    items(5, key = { "skeleton_$it" }) { CommentSkeleton() }
                }

                CommentCardScreen.Comment -> {
                    if (state.loading) {
                        items(3, key = { "loading_fake_$it" }) { CommentSkeleton() }
                    } else if (state.pointComments.items.isEmpty()) {
                        item("empty_comment") {
                            EmptyState(
                                text = "暂无评论，点击底部按钮发布第一条",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp)
                            )
                        }
                    } else {
                        itemsIndexed(
                            state.pointComments.items,
                            key = { _, c -> c.pointComment.index }) { index, item ->
                            CommentItemCard(
                                data = item, onLike = { liked ->
                                    {
                                        onEvent(
                                            CommentAndHistoryCardContract.Event.LikeComment(
                                                item.pointComment.index, liked
                                            )
                                        )
                                    }
                                }, onDislike = { disliked ->
                                    {
                                        onEvent(
                                            CommentAndHistoryCardContract.Event.DislikeComment(
                                                item.pointComment.index, disliked
                                            )
                                        )
                                    }
                                }, modifier = Modifier.fillMaxWidth()
                            )
                            if (index != state.pointComments.items.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier
                                        .padding(horizontal = 24.dp)
                                        .alpha(0.25f),
                                    thickness = DividerDefaults.Thickness,
                                )
                            }
                        }
                    }
                }

                CommentCardScreen.History -> {
                    item("history_block") {
//                        HistorySection(
//                            historyItems = uiState.history.map { it.toString() },
//                            modifier = Modifier.fillMaxWidth()
//                        )
                    }
                }
            }
        }

        // 底部操作栏（不使用 Scaffold）
        BottomActionBarModern(
            onComment = { showCommentSheet = true },
            onUpdate = { onEvent(CommentAndHistoryCardContract.Event.Update) },
            visible = state.state is CommentCardScreen.Comment,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )

        CommentInputBottomSheet(
            visible = showCommentSheet,
            onDismiss = { showCommentSheet = false },
            onSend = {
                onEvent(CommentAndHistoryCardContract.Event.PublishComment)
                showCommentSheet = false
            })

        // 导航对话框
        if (navTarget != null && navName != null) {
            AlertDialog(
                onDismissRequest = { navTarget = null; navName = null },
                title = { Text("导航到：${navName}") },
                text = { Text("是否使用地图进行导航？") },
                confirmButton = {
                    TextButton(onClick = {
                        onEvent(CommentAndHistoryCardContract.Event.Navigate(navTarget!!))
                        navTarget = null
                        navName = null
                    }) { Text("确定") }
                },
                dismissButton = {
                    TextButton(onClick = { navTarget = null; navName = null }) { Text("取消") }
                })
        }

        // 图片预览
        if (previewImage != null) {
            Dialog(onDismissRequest = { previewImage = null }) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(10.dp)
                ) {
                    AsyncImage(
                        model = previewImage,
                        contentDescription = "预览图",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp, max = 420.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { previewImage = null },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "关闭预览")
                    }
                }
            }
        }
    }
}


@Composable
private fun PointHeaderCard(
    easyPoint: EasyPoint,
    onNavigateClick: () -> Unit,
    onLike: (Boolean) -> Unit,
    onDislike: (Boolean) -> Unit,
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier, shape = RoundedCornerShape(20.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp, max = 240.dp)
                    .clickable { onImageClick() }) {
                AsyncImage(
                    model = easyPoint.photo,
                    contentDescription = "点位图片",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                0f to Color(0x66000000),
                                0.4f to Color.Transparent,
                                1f to Color(0xAA000000)
                            )
                        )
                )
                Text(
                    text = easyPoint.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(onClick = onNavigateClick, label = { Text("导航") }, leadingIcon = {
                    Icon(Icons.Outlined.Place, contentDescription = null)
                })
                Spacer(Modifier.weight(1f))
                LikeAndDisLikeButton(
                    like = onLike,
                    dislike = onDislike,
                    likeNum = easyPoint.likes,
                    dislikeNum = easyPoint.dislikes,
                    modifier = Modifier
                )
            }
            Divider(Modifier.padding(top = 8.dp))
            InfoRow(Icons.Outlined.LocationOn, "详细地址", easyPoint.location)
            InfoRow(Icons.Outlined.Info, "更新日期", easyPoint.refreshTime)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text("$label：", fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Text(
            value,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}


/* ---------------- 评论项 ---------------- */
@Composable
private fun CommentItemCard(
    data: PointCommentAndUser,
    onLike: (Boolean) -> Unit,
    onDislike: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        AsyncImage(
            model = data.user.avatar ?: R.drawable.nouser,
            contentDescription = "用户头像",
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
                .padding(12.dp)
                .weight(1f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    data.user.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    data.pointComment.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Text(
                data.pointComment.content,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 6.dp)
            )
            LikeAndDisLikeButton(
                like = onLike,
                dislike = onDislike,
                likeNum = data.pointComment.like,
                dislikeNum = data.pointComment.dislike,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            )
        }
    }
}

/* ---------------- 骨架 ---------------- */
@Composable
private fun CommentSkeleton() {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .alpha(0.55f)
    ) {
        Box(
            Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.fillMaxWidth()) {
            Box(
                Modifier
                    .height(14.dp)
                    .fillMaxWidth(0.3f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .height(12.dp)
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Spacer(Modifier.height(6.dp))
            Box(
                Modifier
                    .height(12.dp)
                    .fillMaxWidth(0.55f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    }
}

/* ---------------- 空状态 ---------------- */
@Composable
private fun EmptyState(
    text: String, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Outlined.Build, null, modifier = Modifier.size(54.dp))
        Spacer(Modifier.height(12.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

/* ---------------- 底部操作栏（不依赖 Scaffold） ---------------- */
@Composable
private fun BottomActionBarModern(
    onComment: () -> Unit, onUpdate: () -> Unit, visible: Boolean, modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically { it / 3 },
        exit = fadeOut() + slideOutVertically { it / 3 },
        modifier = modifier
    ) {
        Surface(
            tonalElevation = 4.dp, shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onUpdate, modifier = Modifier.weight(4f)
                ) { Text("更新内容") }
                Spacer(Modifier.width(16.dp))
                Button(
                    onClick = onComment, modifier = Modifier.weight(3f)
                ) { Text("发布评论") }
            }
        }
    }
}

/* ---------------- 评论输入 BottomSheet（自实现） ---------------- */
@SuppressLint("UnrememberedMutableState")
@Composable
private fun CommentInputBottomSheet(
    visible: Boolean, onDismiss: () -> Unit, onSend: (String) -> Unit
) {
    if (!visible) return

    // 动画高度
    val transition = updateTransition(targetState = visible, label = "sheet")
    val alpha by transition.animateFloat(label = "alpha") { if (it) 1f else 0f }
    val offsetY by transition.animateDp(label = "offset") {
        if (it) 0.dp else 400.dp
    }

    // 背景遮罩
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f * alpha))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) { onDismiss() }) {
        var text by remember { mutableStateOf("") }
        val canSend by derivedStateOf { text.isNotBlank() }

        Surface(
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            tonalElevation = 8.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = offsetY)
                .fillMaxWidth()
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(16.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "发布评论",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "关闭")
                    }
                }
                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        if (it.length <= 500) text = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    placeholder = { Text("写点什么…") },
                    maxLines = 6
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${text.length}/500", style = MaterialTheme.typography.labelSmall)
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("取消") }
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = { onSend(text.trim()) }, enabled = canSend
                    ) {
                        Text("发送")
                    }
                }
            }
        }
    }
}

/* ---------------- 历史区块示例 ---------------- */
@Composable
private fun HistorySection(
    historyItems: List<String>, modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.padding(16.dp), shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("历史记录", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            if (historyItems.isEmpty()) {
                Text("暂无历史记录", color = MaterialTheme.colorScheme.outline)
            } else {
                historyItems.forEachIndexed { i, s ->
                    Text("${i + 1}. $s", modifier = Modifier.padding(vertical = 4.dp))
                    if (i != historyItems.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .alpha(0.4f),
                            thickness = DividerDefaults.Thickness,
                            color = DividerDefaults.color
                        )
                    }
                }
            }
        }
    }
}