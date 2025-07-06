package com.efbsm5.easyway.ui.components.mapcards

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.amap.api.maps.model.LatLng
import com.efbsm5.easyway.R
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.PointComment
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.getLatlng
import com.efbsm5.easyway.ui.components.NavigationDialog
import com.efbsm5.easyway.ui.components.TabSection
import com.efbsm5.easyway.viewmodel.componentsViewmodel.CommentAndHistoryCardViewModel
import com.efbsm5.easyway.viewmodel.componentsViewmodel.CommentCardScreen


@Composable
fun CommentAndHistoryCard(
    viewModel: CommentAndHistoryCardViewModel,
    navigate: (LatLng) -> Unit,
    changeScreen: (Screen) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val pointComment by viewModel.pointComments.collectAsState()
    val point by viewModel.point.collectAsState()
    CommentAndHistoryCardScreen(
        point = point,
        onSelect = {
            viewModel.changeState(
                if (it == 0) CommentCardScreen.Comment
                else CommentCardScreen.History
            )
        },
        state = state,
        pointComments = pointComment,
        publish = { viewModel.publish(it) },
        update = {
            changeScreen(
                Screen.NewPoint(
                    label = "更新点位"
                )
            )
        },
        navigate = navigate,
        like = { viewModel.likePost(it) },
        dislike = { viewModel.dislikePost(it) },
        likeComment = { index, boolean ->
            viewModel.likeComment(
                commentIndex = index, boolean = boolean
            )
        },
        dislikeComment = { index, boolean ->
            viewModel.dislikeComment(
                commentIndex = index, boolean = boolean
            )
        })
}

@Composable
private fun CommentAndHistoryCardScreen(
    point: EasyPoint,
    onSelect: (Int) -> Unit,
    state: CommentCardScreen,
    pointComments: List<Pair<PointComment, User>>,
    publish: (String) -> Unit,
    update: () -> Unit,
    navigate: (LatLng) -> Unit,
    like: (Boolean) -> Unit,
    dislike: (Boolean) -> Unit,
    likeComment: (Int, Boolean) -> Unit,
    dislikeComment: (Int, Boolean) -> Unit
) {
    var comment by rememberSaveable { mutableStateOf(false) }
    var destination by rememberSaveable { mutableStateOf(LatLng(0.0, 0.0)) }
    var name = ""
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        PointInfo(easyPoint = point, onNavigate = { destination1, name1 ->
            destination = destination1
            name = name1
        }, like = like, dislike = dislike)
        Spacer(modifier = Modifier.height(16.dp))
        TabSection(
            onSelect = onSelect,
            tabs = listOf("评论", "历史"),
        )
        when (state) {
            CommentCardScreen.Comment -> CommentCard(
                pointComments, like = likeComment, dislike = dislikeComment
            )

            CommentCardScreen.History -> HistoryCard()
        }
        if (comment) {
            ShowTextField(publish = {
                comment = false
                publish(it)
            }, cancel = { comment = false })
        } else BottomActionBar(comment = { comment = true }, update = update)
        if (destination != LatLng(0.0, 0.0)) NavigationDialog(destination, name) {
            navigate(
                destination
            )
            destination = LatLng(0.0, 0.0)
        }
    }
}

@Composable
fun PointInfo(
    easyPoint: EasyPoint,
    onNavigate: (LatLng, String) -> Unit,
    like: (Boolean) -> Unit,
    dislike: (Boolean) -> Unit
) {
    var isLiked by remember { mutableStateOf(false) }
    var isDisliked by remember { mutableStateOf(false) }
    val likeColor by animateColorAsState(targetValue = if (isLiked) Color.Red else Color.Gray)
    val dislikeColor by animateColorAsState(targetValue = if (isDisliked) Color.Red else Color.Gray)
    val likeSize by animateFloatAsState(targetValue = if (isLiked) 36f else 24f)
    val dislikeSize by animateFloatAsState(targetValue = if (isDisliked) 36f else 24f)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(430.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {
        Image(
            painter = rememberAsyncImagePainter(easyPoint.photo),
            contentDescription = "Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(easyPoint.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { onNavigate(easyPoint.getLatlng(), easyPoint.name) }) {
                Text("导航")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            Icon(
                Icons.Rounded.ThumbUp,
                contentDescription = "like",
                modifier = Modifier
                    .size(likeSize.dp)
                    .clickable {
                        isLiked = !isLiked
                        if (isDisliked) isDisliked = false
                        like(isLiked)
                    },
                tint = likeColor
            )
            Text(easyPoint.likes.toString(), modifier = Modifier.padding(start = 4.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                modifier = Modifier
                    .size(dislikeSize.dp)
                    .clickable {
                        isDisliked = !isDisliked
                        if (isLiked) isLiked = false
                        dislike(isDisliked)
                    },
                painter = painterResource(id = R.drawable.thumb_down),
                contentDescription = "Dislike",
                tint = dislikeColor
            )
            Text(easyPoint.dislikes.toString(), modifier = Modifier.padding(start = 4.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("详细地址", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(easyPoint.location, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.clock), contentDescription = "Update Time"
            )
            Text("更新日期: ${easyPoint.refreshTime}", modifier = Modifier.padding(start = 4.dp))
        }
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            Icon(
                Icons.Default.Person, contentDescription = "Source"
            )
            Text("点位来源:${easyPoint.userId}", modifier = Modifier.padding(start = 4.dp))
        }
    }
}


@Composable
private fun CommentCard(
    comments: List<Pair<PointComment, User>>,
    like: (Int, Boolean) -> Unit,
    dislike: (Int, Boolean) -> Unit
) {
    if (comments.isEmpty()) {
        Text("暂无")
    } else LazyColumn {
        items(comments) { commentAndUser ->
            CommentItem(
                pointCommentAndUser = commentAndUser,
                like = { like(commentAndUser.first.index, it) },
                dislike = { dislike(commentAndUser.first.index, it) },
            )
        }
    }
}

@Composable
private fun CommentItem(
    pointCommentAndUser: Pair<PointComment, User>,
    like: (Boolean) -> Unit,
    dislike: (Boolean) -> Unit
) {
    var isLiked by remember { mutableStateOf(false) }
    var isDisliked by remember { mutableStateOf(false) }
    val likeColor by animateColorAsState(targetValue = if (isLiked) Color.Red else Color.Gray)
    val dislikeColor by animateColorAsState(targetValue = if (isDisliked) Color.Red else Color.Gray)
    val likeSize by animateFloatAsState(targetValue = if (isLiked) 36f else 24f)
    val dislikeSize by animateFloatAsState(targetValue = if (isDisliked) 36f else 24f)
    val user = pointCommentAndUser.second
    val comment = pointCommentAndUser.first
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = rememberAsyncImagePainter(user.avatar ?: R.drawable.nouser),
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            contentDescription = "avatar"
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.name, style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comment.date,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Default.ThumbUp, modifier = Modifier
                        .size(likeSize.dp)
                        .clickable {
                            like(isLiked)
                            isLiked = !isLiked
                            if (isDisliked) isDisliked = false
                        }, contentDescription = "Dislike", tint = likeColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isLiked) (comment.like + 1).toString() else comment.like.toString(),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    modifier = Modifier
                        .size(dislikeSize.dp)
                        .clickable {
                            dislike(isDisliked)
                            isDisliked = !isDisliked
                            if (isLiked) isLiked = false
                        },
                    painter = painterResource(id = R.drawable.thumb_down),
                    contentDescription = "Dislike",
                    tint = dislikeColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isDisliked) (comment.dislike + 1).toString() else comment.dislike.toString(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun BottomActionBar(comment: () -> Unit, update: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .fillMaxWidth()
            .height(39.dp)
    ) {
        OutlinedButton(
            onClick = { update() }, modifier = Modifier.weight(5f)
        ) {
            Text(text = "更新内容")
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(
            onClick = { comment() }, modifier = Modifier.weight(3f)
        ) {
            Text(text = "发布评论")
        }
    }
}

@Composable
private fun ShowTextField(
    publish: (String) -> Unit, cancel: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(), value = text, onValueChange = { text = it })
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { publish(text) }, modifier = Modifier.weight(3f)
            ) { Text("发布") }
            Spacer(modifier = Modifier.width(20.dp))
            Button(onClick = { cancel() }, modifier = Modifier.weight(2f)) { Text("取消") }
        }
    }
}