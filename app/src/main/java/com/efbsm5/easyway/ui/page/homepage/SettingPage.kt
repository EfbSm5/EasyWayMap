package com.efbsm5.easyway.ui.page.homepage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.efbsm5.easyway.viewmodel.HomePageState

@Preview
@Composable
fun SettingsScreen(changeState: (HomePageState) -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopBar(title = "设置", back = { changeState(HomePageState.Main) })
        Spacer(modifier = Modifier.height(8.dp))
        SettingGroup(
            items = listOf(
                SettingItem(
                    Icons.Default.Settings,
                    "通用设置",
                    onClick = { changeState(HomePageState.CommonSetting) }),
                SettingItem(Icons.Default.Notifications, "通知设置", onClick = {
                    changeState(HomePageState.InformSetting)
                })
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        SettingGroup(
            items = listOf(
                SettingItem(
                    Icons.Default.Lock,
                    "账号安全",
                    onClick = { changeState(HomePageState.Safety) }),
                SettingItem(
                    Icons.Default.Info,
                    "免责声明",
                    onClick = { changeState(HomePageState.Declare) }),
                SettingItem(Icons.Default.Face, "关于我们", onClick = {
                    changeState(HomePageState.ShowVersionAndHelp)
                })
            ),
        )
        Spacer(modifier = Modifier.height(16.dp))
        SettingGroup(
            items = listOf(
                SettingItem(
                    Icons.AutoMirrored.Filled.ExitToApp,
                    "注销账号",
                    Color(0xFF1E88E5),
                    onClick = { changeState(HomePageState.RegForActivity) })
            )
        )
    }
}

@Composable
fun TopBar(title: String, back: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(back) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
fun SettingGroup(items: List<SettingItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clip(RoundedCornerShape(6.dp))
    ) {
        items.forEachIndexed { index, item ->
            SettingRow(
                item
            )
            if (index != items.lastIndex) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant) // 分割线颜色
                )
            }
        }
    }
}

@Composable
fun SettingRow(item: SettingItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable {
                item.onClick
            }, verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = item.iconTint,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = item.title,
            fontSize = 16.sp,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Arrow",
            tint = Color(0xFFBDBDBD),
            modifier = Modifier.size(24.dp)
        )
    }
}

data class SettingItem(
    val icon: ImageVector,
    val title: String,
    val iconTint: Color = Color(0xFF1E88E5),
    val onClick: () -> Unit
)