package com.efbsm5.easyway.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.efbsm5.easyway.ui.FabConfig

@Composable
fun FloatingButton(fabConfig: FabConfig?) {
    AnimatedContent(
        targetState = fabConfig, label = "fabChange"
    ) { cfg ->
        if (cfg != null && cfg.visible) {
            if (cfg.text != null) {
                ExtendedFloatingActionButton(
                    onClick = cfg.onClick,
                    expanded = cfg.extended,
                    icon = { Icon(cfg.icon, contentDescription = cfg.text) },
                    text = { Text(cfg.text) },
                    containerColor = cfg.containerColor
                        ?: FloatingActionButtonDefaults.containerColor,
                    contentColor = cfg.contentColor
                        ?: FloatingActionButtonDefaults.containerColor,
                )
            } else {
                FloatingActionButton(
                    onClick = cfg.onClick,
                    containerColor = cfg.containerColor
                        ?: FloatingActionButtonDefaults.containerColor,
                    contentColor = cfg.contentColor
                        ?: FloatingActionButtonDefaults.containerColor,
                    modifier = Modifier.offset(y = -cfg.offset)
                ) {
                    Icon(cfg.icon, contentDescription = null)
                }
            }
        }
    }
}