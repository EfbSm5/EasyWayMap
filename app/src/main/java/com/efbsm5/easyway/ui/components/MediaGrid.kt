package com.efbsm5.easyway.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage

@Composable
fun MediaGrid(urls: List<String>) {
    val display = urls.take(3)
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        display.forEachIndexed { index, url ->
            val shape = RoundedCornerShape(12.dp)
            SubcomposeAsyncImage(
                model = url,
                contentDescription = "图片$index",
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp)
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                loading = {
                    Box(Modifier.fillMaxSize())
                },
                contentScale = ContentScale.Crop
            )
        }
    }
}