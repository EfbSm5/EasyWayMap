package com.efbsm5.easyway.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.efbsm5.easyway.R

@Composable
fun LikeAndDisLikeButton(
    like: (Boolean) -> Unit,
    dislike: (Boolean) -> Unit,
    likeNum: Int,
    dislikeNum: Int,
    modifier: Modifier
) {
    var isLiked by remember { mutableStateOf(false) }
    var isDisliked by remember { mutableStateOf(false) }
    val likeColor by animateColorAsState(targetValue = if (isLiked) Color.Red else Color.Gray)
    val dislikeColor by animateColorAsState(targetValue = if (isDisliked) Color.Red else Color.Gray)
    val likeSize by animateFloatAsState(targetValue = if (isLiked) 36f else 24f)
    val dislikeSize by animateFloatAsState(targetValue = if (isDisliked) 36f else 24f)
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
        Text(likeNum.toString(), modifier = Modifier.padding(start = 4.dp))
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
        Text(dislikeNum.toString(), modifier = Modifier.padding(start = 4.dp))
    }
}