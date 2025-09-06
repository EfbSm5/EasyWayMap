package com.efbsm5.easyway.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppTopBar(
    title: String? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    onBack: (() -> Unit)? = null,
    titleContent: (@Composable () -> Unit)? = null,
    centerTitle: Boolean = false,
    showDivider: Boolean = false,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    testTag: String = "TopBar",
    elevationWhenScrolled: Boolean = true
) {
    val usedNavigationIcon: (@Composable () -> Unit)? = when {
        navigationIcon != null -> navigationIcon
        onBack != null -> {
            {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "返回",
                        tint = titleColor
                    )
                }
            }
        }

        else -> null
    }

    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = containerColor,
        titleContentColor = titleColor,
        navigationIconContentColor = titleColor,
        actionIconContentColor = titleColor
    )

    val bar = @Composable {
        TopAppBar(
            modifier = modifier
                .semantics { this.contentDescription = "AppTopBar" }
                .testTag(testTag),
            title = {
                if (titleContent != null) {
                    ProvideTextStyle(MaterialTheme.typography.titleLarge) {
                        titleContent()
                    }
                } else if (title != null) {
                    val textComposable = @Composable {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (centerTitle) {
                        Box(
                            modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                        ) { textComposable() }
                    } else {
                        textComposable()
                    }
                }
            },
            navigationIcon = { usedNavigationIcon?.invoke() },
            actions = actions,
            colors = colors,
            scrollBehavior = scrollBehavior,
            windowInsets = windowInsets
        )
    }

    if (elevationWhenScrolled && scrollBehavior != null) {
        val elevation by remember {
            derivedStateOf {
                if (scrollBehavior.state.overlappedFraction > 0f) 4.dp else 0.dp
            }
        }
        Surface(
            tonalElevation = 0.dp, shadowElevation = elevation
        ) {
            Column {
                bar()
                if (showDivider) {
                    Divider(
                        thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    } else {
        Column {
            bar()
            if (showDivider) {
                Divider(
                    thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}
