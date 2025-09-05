package com.efbsm5.easyway.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


@Composable
fun ReactionButton(
    active: Boolean,
    icon: ImageVector? = null,
    painter: Painter? = null,
    count: Int,
    activeColor: Color,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(if (active) 1.25f else 1f, label = "scale")
    val tint by animateColorAsState(
        (if (active) activeColor else MaterialTheme.colorScheme.onSurfaceVariant),
        label = "tint"
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Box(
            Modifier
                .size(28.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            contentAlignment = Alignment.Center
        ) {
            if (icon != null) Icon(icon, contentDescription = null, tint = tint)
            else if (painter != null) Icon(painter, contentDescription = null, tint = tint)
        }
        Spacer(Modifier.width(2.dp))
        AnimatedContent(targetState = count, label = "countAnim") { c ->
            Text(
                c.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = tint
            )
        }
    }
}
