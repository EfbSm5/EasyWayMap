package com.efbsm5.easyway.ui.page.communityPage

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.efbsm5.easyway.R
import com.efbsm5.easyway.data.models.Comment
import com.efbsm5.easyway.data.models.DynamicPost
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.map.MapUtil
import com.efbsm5.easyway.ui.components.TopBar
import com.efbsm5.easyway.viewmodel.pageViewmodel.DetailPageViewModel

@Composable
fun DetailPage(onBack: () -> Unit, viewModel: DetailPageViewModel) {
    val postUser by viewModel.postUser.collectAsState()
    val commentAndUser by viewModel.commentAndUser.collectAsState()
    val post by viewModel.post.collectAsState()
    DetailPageScreen(
        onBack = onBack,
        post = post!!,
        postUser = postUser,
        commentAndUser = commentAndUser,
        comment = { viewModel.comment(it) },
        like = { boolean, index -> viewModel.likeComment(boolean, index) },
        dislike = { boolean, index -> viewModel.dislikeComment(boolean, index) },
        likePost = { viewModel.likePost(it) })
}

@Preview
@Composable
private fun DetailPageScreen(
    onBack: () -> Unit = {},
    post: DynamicPost = MapUtil.getInitPost(),
    postUser: User = MapUtil.getInitUser(),
    commentAndUser: List<Pair<Comment, User>> = emptyList(),
    comment: (String) -> Unit = {},
    like: (Boolean, Int) -> Unit = { _, _ -> },
    dislike: (Boolean, Int) -> Unit = { _, _ -> },
    likePost: (Boolean) -> Unit = {}
) {
    var showTextField by remember { mutableStateOf(false) }
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TopBar(text = "详情页", back = onBack)
            Spacer(modifier = Modifier.height(16.dp))
            DetailsContent(
                post = post, user = postUser, like = likePost
            )
            HorizontalDivider(
                thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
            Comments(
                list = commentAndUser, like = like, dislike = dislike
            )
            CommentSection(comment = { showTextField = true })
            if (showTextField) {
                AddCommentField(
                    onClickButton = {
                        showTextField = false
                        comment(it)
                    })
            }
        }
    }
    BackHandler(enabled = showTextField) {
        showTextField = false
    }
}


@Composable
private fun DetailsContent(
    post: DynamicPost,
    user: User,
    like: (Boolean) -> Unit,
) {
    var isLiked by remember { mutableStateOf(false) }
    val likeColor by animateColorAsState(
        targetValue = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
            alpha = 0.6f
        )
    )
    val likeSize by animateFloatAsState(targetValue = if (isLiked) 36f else 24f)
    Row(
        modifier = Modifier.padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(user.avatar),
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                user.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium
            )
            Text(post.date, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
    Text(post.content, style = MaterialTheme.typography.bodyLarge)
    Spacer(modifier = Modifier.height(8.dp))
    if (post.photo.isNotEmpty()) {
        Image(
            painter = rememberAsyncImagePainter(post.photo.first()),
            contentDescription = "Post Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(8.dp))
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Icon(
            Icons.Default.ThumbUp, modifier = Modifier
                .size(likeSize.dp)
                .clickable {
                    like(isLiked)
                    isLiked = !isLiked
                }, contentDescription = "Like", tint = likeColor
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(post.like.toString(), style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.width(16.dp))
    }
}

@Composable
private fun Comments(
    list: List<Pair<Comment, User>>, like: (Boolean, Int) -> Unit, dislike: (Boolean, Int) -> Unit
) {
    LazyColumn(modifier = Modifier.padding(vertical = 16.dp)) {
        items(list) { commentAndUser ->
            CommentItems(
                commentAndUser, like = like, dislike = dislike
            )
        }
    }
}

@Composable
private fun CommentItems(
    commentAndUser: Pair<Comment, User>,
    like: (Boolean, Int) -> Unit,
    dislike: (Boolean, Int) -> Unit
) {
    var isLiked by remember { mutableStateOf(false) }
    var isDisliked by remember { mutableStateOf(false) }
    val likeColor by animateColorAsState(
        targetValue = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
            alpha = 0.6f
        )
    )
    val dislikeColor by animateColorAsState(
        targetValue = if (isDisliked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface.copy(
            alpha = 0.6f
        )
    )
    val likeSize by animateFloatAsState(targetValue = if (isLiked) 36f else 24f)
    val dislikeSize by animateFloatAsState(targetValue = if (isDisliked) 36f else 24f)
    val user = commentAndUser.second
    val comment = commentAndUser.first
    Row(
        modifier = Modifier.padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(user.avatar),
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Row {
                Text(
                    user.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(comment.date, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Text(comment.content, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            Icons.Default.ThumbUp, modifier = Modifier
                .size(likeSize.dp)
                .clickable {
                    isLiked = !isLiked
                    if (isDisliked) isDisliked = false
                    like(isLiked, comment.index)
                }, contentDescription = "Like", tint = likeColor
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(comment.like.toString(), style = MaterialTheme.typography.bodySmall)
        Icon(
            modifier = Modifier
                .size(dislikeSize.dp)
                .clickable {
                    isDisliked = !isDisliked
                    if (isLiked) isLiked = false
                    dislike(isDisliked, comment.index)
                },
            painter = painterResource(id = R.drawable.thumb_down),
            contentDescription = "Dislike",
            tint = dislikeColor
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(comment.dislike.toString(), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun CommentSection(comment: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Row(
            modifier = Modifier
                .height(30.dp)
                .width(80.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable { comment() }, verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Edit,
                contentDescription = "Write Comment",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "写回复",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCommentField(
    onClickButton: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    Row(verticalAlignment = Alignment.CenterVertically) {
        TextField(
            value = commentText,
            onValueChange = { commentText = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("添加评论", style = MaterialTheme.typography.bodySmall) },
        )
        Spacer(modifier = Modifier.width(8.dp))
        TextButton(onClick = {
            onClickButton(commentText)
        }) {
            Text(
                "发送",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
