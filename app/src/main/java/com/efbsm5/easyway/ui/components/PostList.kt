package com.efbsm5.easyway.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.efbsm5.easyway.data.models.assistModel.PostAndUser

@Composable
fun PostList(
    posts: List<PostAndUser>, onClick: (PostAndUser) -> Unit
) {
    if (posts.isEmpty()) {
        Text("没有数据")
    } else {
        LazyColumn {
            items(posts) {
                PostsItem(
                    it
                ) { onClick(it) }
            }
        }
    }
}


@Composable
private fun PostsItem(postAndUser: PostAndUser, onClick: () -> Unit) {
    val user = postAndUser.user
    val dynamicPost = postAndUser.post
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() }) {
        Image(
            rememberAsyncImagePainter(user),
            contentDescription = "头像",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.name, style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "#分享", color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = dynamicPost.date, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = if (dynamicPost.content.length > 15) dynamicPost.content.take(15) + "..." else dynamicPost.content,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                dynamicPost.photo.forEach {
                    Image(
                        rememberAsyncImagePainter(
                            it
                        ),
                        contentDescription = "评论图片",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "点赞",
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = dynamicPost.like.toString(),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}
