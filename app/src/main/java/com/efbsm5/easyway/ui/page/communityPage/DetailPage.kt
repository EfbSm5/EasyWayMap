package com.efbsm5.easyway.ui.page.communityPage

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.efbsm5.easyway.R
import com.efbsm5.easyway.contract.community.DetailContract
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.data.models.assistModel.PostCommentAndUser
import com.efbsm5.easyway.showMsg
import com.efbsm5.easyway.ui.components.MediaGrid
import com.efbsm5.easyway.ui.components.ReactionButton
import com.efbsm5.easyway.ui.components.UserAvatar
import com.efbsm5.easyway.viewmodel.communityViewModel.DetailViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun DetailRoute(
    postAndUser: PostAndUser,
    onBack: () -> Unit,
    viewModel: DetailViewModel,
    onLikeUpdated: (PostAndUser) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        postAndUser.let { viewModel.setPostAndUser(postAndUser) }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.onEach { effect ->
            when (effect) {
                DetailContract.Effect.Back -> onBack()
                is DetailContract.Effect.Toast -> showMsg(effect.string)
                is DetailContract.Effect.Liked -> {
                    val snapshot = postAndUser.post
                    val delta = if (effect.boolean) 1 else -1
                    onLikeUpdated(
                        postAndUser.copy(
                            post = snapshot.copy(
                                likedByMe = effect.boolean,
                                like = (snapshot.like + delta).coerceAtLeast(0)
                            )
                        )
                    )
                }
            }
        }.collect()
    }

    DetailScreen(
        state = uiState, onBack = viewModel::back, onEvent = viewModel::onEvent
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    state: DetailContract.State, onBack: () -> Unit, onEvent: (DetailContract.Event) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val showInput = state.showTextField
    val density = LocalDensity.current

    LargeTopAppBar(
        title = { Text("详情") }, navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "返回")
            }
        }, scrollBehavior = scrollBehavior
    )

    if (!showInput) {
        ExtendedFloatingActionButton(
            text = { Text("回复") },
            icon = { Icon(Icons.Default.Edit, contentDescription = null) },
            onClick = { onEvent(DetailContract.Event.ShowInput(true)) })
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        // 主体列表
        LazyColumn(
            modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(
                bottom = if (showInput) 160.dp else 96.dp, top = 8.dp, start = 16.dp, end = 16.dp
            ), verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "post") {
                PostCard(
                    post = state.post,
                    user = state.user,
                    liked = state.post?.likedByMe == true,
                    onToggleLike = { onEvent(DetailContract.Event.ToggleLikePost) })
            }

            if (state.comments.isNotEmpty()) {
                item(key = "comment_title") {
                    Text(
                        "评论 (${state.comments.size})",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                itemsIndexed(
                    state.comments,
                    key = { index, item -> item.postComment.index }) { index, commentAndUser ->
                    CommentCard(data = commentAndUser, onToggleLike = {
                        onEvent(DetailContract.Event.ToggleLikeComment(index))
                    }, onToggleDislike = {
                        onEvent(DetailContract.Event.ToggleDisLikeComment(index))
                    })
                }
            } else {
                item("empty") {
                    EmptyCommentsPlaceholder(onClick = { onEvent(DetailContract.Event.ShowInput(true)) })
                }
            }
        }

        // 半透明遮罩（输入框显示时）
        AnimatedVisibility(
            visible = showInput, enter = fadeIn(), exit = fadeOut()
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .noRippleClickable { onEvent(DetailContract.Event.ShowInput(false)) })
        }

        // 底部输入栏
        AnimatedVisibility(visible = showInput, enter = slideInVertically {
            with(density) { 80.dp.roundToPx() }
        } + fadeIn(), exit = fadeOut() + slideOutVertically()) {
            CommentInputBar(
                text = state.input,
                onTextChange = { onEvent(DetailContract.Event.ChangeInput(it)) },
                onSend = {
                    onEvent(DetailContract.Event.SendComment)
                    onEvent(DetailContract.Event.ShowInput(false))
                },
                onDismiss = { onEvent(DetailContract.Event.ShowInput(false)) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                        RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .padding(WindowInsets.navigationBars.asPaddingValues())
                    .padding(16.dp)
            )
        }

        // Back 处理
        BackHandler(enabled = showInput) {
            onEvent(DetailContract.Event.ShowInput(false))
        }
    }

}


fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) { onClick() }
}

@Composable
private fun PostCard(
    post: Post?, user: User?, liked: Boolean, onToggleLike: () -> Unit
) {
    if (post == null || user == null) {
        LoadingPostSkeleton()
        return
    }

    var localLiked by remember(liked) { mutableStateOf(liked) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                UserAvatar(url = user.avatar)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(user.name, fontWeight = FontWeight.SemiBold)
                    Text(
                        post.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                AssistChip(onClick = {}, label = { Text("关注") }, leadingIcon = {
                    Icon(Icons.Default.Add, contentDescription = null, Modifier.size(16.dp))
                })
            }
            Spacer(Modifier.height(12.dp))
            Text(post.content, style = MaterialTheme.typography.bodyMedium, lineHeight = 20.sp)
            if (post.photo.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                MediaGrid(urls = post.photo)
            }
            Spacer(Modifier.height(12.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ReactionButton(
                        active = localLiked,
                        activeColor = MaterialTheme.colorScheme.primary,
                        icon = Icons.Default.ThumbUp,
                        count = post.like,
                        onClick = {
                            localLiked = !localLiked
                            onToggleLike()
                        })
                }
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    IconButton(onClick = { /* 收藏 */ }) {
                        Icon(Icons.Default.Star, contentDescription = "收藏")
                    }
                    IconButton(onClick = { /* 分享 */ }) {
                        Icon(Icons.Default.Share, contentDescription = "分享")
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentCard(
    data: PostCommentAndUser, onToggleLike: () -> Unit, onToggleDislike: () -> Unit
) {
    var like by remember { mutableStateOf(data.postComment.likedByMe) }
    var dislike by remember { mutableStateOf(data.postComment.dislikedByMe) }

    Surface(
        shape = RoundedCornerShape(16.dp), tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(12.dp)) {
            UserAvatar(url = data.user.avatar, size = 40.dp)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        data.user.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        data.postComment.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    data.postComment.content,
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReactionButton(
                        active = like,
                        icon = Icons.Default.ThumbUp,
                        count = data.postComment.like,
                        activeColor = MaterialTheme.colorScheme.primary
                    ) {
                        like = !like
                        if (dislike && like) {
                            dislike = false
                            onToggleDislike()
                        }
                        onToggleLike()
                    }
                    ReactionButton(
                        active = dislike,
                        painter = painterResource(id = R.drawable.thumb_down),
                        count = data.postComment.dislike,
                        activeColor = MaterialTheme.colorScheme.error
                    ) {
                        dislike = !dislike
                        if (like && dislike) {
                            like = false
                            onToggleLike()
                        }
                        onToggleDislike()
                    }
                }
            }
        }
    }
}


@Composable
private fun EmptyCommentsPlaceholder(onClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Edit,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(42.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "还没有评论",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onClick) {
            Text("写第一条评论")
        }
    }
}

@Composable
private fun CommentInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("写评论", style = MaterialTheme.typography.titleSmall)
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "关闭")
            }
        }
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp, max = 180.dp),
            placeholder = { Text("友善发言...") },
            maxLines = 6
        )
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(
                enabled = text.isNotBlank(), onClick = onSend
            ) {
                Text("发送")
            }
        }
    }
}

@Composable
private fun LoadingPostSkeleton() {
    Surface(
        shape = RoundedCornerShape(20.dp), tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row {
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Box(
                        Modifier
                            .height(14.dp)
                            .width(100.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                    Spacer(Modifier.height(8.dp))
                    Box(
                        Modifier
                            .height(12.dp)
                            .width(140.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            repeat(3) {
                Box(
                    Modifier
                        .height(12.dp)
                        .fillMaxWidth(if (it == 2) 0.5f else 1f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

