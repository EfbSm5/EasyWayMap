package com.efbsm5.easyway.ui.components


import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.data.models.assistModel.PostType


@Composable
fun PostList(
    posts: List<PostAndUser>,
    modifier: Modifier = Modifier,
    onClickPost: (PostAndUser) -> Unit,
    onClickLike: ((PostAndUser) -> Unit)? = null,
    listState: LazyListState = rememberLazyListState(),
    showSkeleton: Boolean = false,
    skeletonCount: Int = 6,
    emptyText: String = "没有数据",
    onReachedEnd: (() -> Unit)? = null,
    isAppending: Boolean = false,
    appendIndicator: @Composable (() -> Unit)? = null,
    enableExpandContent: Boolean = true
) {
    // 触底检测（分页）
    LaunchedEffect(listState, posts.size) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }.collect { lastIndex ->
            if (onReachedEnd != null && lastIndex != null && lastIndex >= posts.size - 3 && posts.isNotEmpty() && !showSkeleton) {
                onReachedEnd()
            }
        }
    }

    when {
        showSkeleton -> {
            LazyColumn(
                state = listState,
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(skeletonCount) { PostItemSkeleton() }
            }
        }

        posts.isEmpty() -> {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(emptyText, style = MaterialTheme.typography.bodyMedium)
            }
        }

        else -> {
            LazyColumn(
                state = listState,
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(items = posts, key = { it.post.id } // 确保 Post.id 唯一
                ) { pu ->
                    PostItem(
                        postAndUser = pu,
                        onClick = { onClickPost(pu) },
                        onClickLike = onClickLike,
                        enableExpand = enableExpandContent
                    )
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                }

                if (isAppending) {
                    item("appending_indicator") {
                        appendIndicator?.invoke() ?: DefaultAppendIndicator()
                    }
                }

                item("bottom_spacer") { Spacer(Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
private fun DefaultAppendIndicator() {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(26.dp), strokeWidth = 3.dp)
    }
}

@Composable
private fun PostItem(
    postAndUser: PostAndUser,
    onClick: () -> Unit,
    onClickLike: ((PostAndUser) -> Unit)?,
    enableExpand: Boolean
) {
    val user = postAndUser.user
    val post = postAndUser.post

    // 展开状态（按帖子 id 记忆）
    var expanded by rememberSaveable(post.id) { mutableStateOf(false) }
    val contentMaxLines = if (expanded || !enableExpand) Int.MAX_VALUE else 4

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = LocalIndication.current,
                interactionSource = remember { MutableInteractionSource() }) { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)) {
        // 顶部：头像 + 名称 + 标签 + 时间
        Row(verticalAlignment = Alignment.CenterVertically) {
            UserAvatar(url = user.avatar)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = post.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            PostCategoryTag(post.type) // 假设 category 字段
        }

        // 内容文本
        if (post.content.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = contentMaxLines,
                overflow = TextOverflow.Ellipsis
            )
            if (enableExpand && isExpandable(post.content) && !expanded) {
                Text(
                    "展开",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable { expanded = true })
            } else if (enableExpand && expanded) {
                Text(
                    "收起",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable { expanded = false })
            }
        }

        // 图片
        if (post.photo.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            PostImagesGrid(urls = post.photo)
        }

        // 操作栏
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.ThumbUp,
                contentDescription = "点赞",
                modifier = Modifier
                    .size(18.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onClickLike?.invoke(postAndUser) })
            Spacer(Modifier.width(4.dp))
            Text(
                text = post.like.toString(), style = MaterialTheme.typography.labelMedium
            )
            // 这里可加 评论 / 分享 / 收藏 等
        }
    }
}

@Composable
private fun PostCategoryTag(category: Int?) {
    val label = when (category) {
        PostType.SHARE -> "分享"
        PostType.HELP -> "互助"
        PostType.ACTIVITY -> "活动"
        PostType.ALL, null -> "all"
        else -> {}
    }
    AssistChip(
        onClick = {}, label = { Text("#$label") }, shape = RoundedCornerShape(8.dp)
    )
}

private fun isExpandable(content: String) = content.length > 140

// ----------------------- 图片布局 ------------------------

@Composable
private fun PostImagesGrid(urls: List<String>) {
    val maxShow = urls.take(9) // 最多显示 9 张
    val columns = when (maxShow.size) {
        1 -> 1
        2, 4 -> 2
        else -> 3
    }
    val spacing = 6.dp

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        maxItemsInEachRow = columns,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        maxShow.forEachIndexed { index, url ->
            val isSingle = maxShow.size == 1
            val cellModifier = if (isSingle) {
                Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            } else {
                Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            }
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(url).crossfade(true)
                    .build(),
                contentDescription = "图片 ${index + 1}",
                contentScale = if (isSingle) ContentScale.Crop else ContentScale.Crop,
                modifier = cellModifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    }
}

// ----------------------- 骨架屏 ------------------------

@Composable
private fun PostItemSkeleton() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(42.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Box(
                    Modifier
                        .fillMaxWidth(0.4f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    Modifier
                        .fillMaxWidth(0.3f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
            Box(
                Modifier
                    .width(54.dp)
                    .height(22.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        Spacer(Modifier.height(12.dp))
        repeat(2) {
            Box(
                Modifier
                    .fillMaxWidth(if (it == 0) 0.95f else 0.75f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            Spacer(Modifier.height(8.dp))
        }

        // 图片占位（模拟 3 张）
        Row(
            Modifier
                .fillMaxWidth()
                .height(100.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(3) {
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        Box(
            Modifier
                .width(80.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
        )
    }
    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
}
