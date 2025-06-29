package com.efbsm5.easyway.ui.page.homepage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CommonSettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        SectionTitle("界面布局")
        SettingItem("字体大小", showArrow = true)
        SettingItem("主页底部导航栏设置", showArrow = true)

        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle("记录与存储")
        SettingItem("聊天记录管理", "备份、迁移", true)
        SettingItem("存储空间", "聊天记录、文件清理", true)

        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle("互动功能")
        SettingItem("头像双击动作设置", showArrow = true)
        SettingItem("自定义撤回消息", showArrow = true)
        SettingItem("聊天", showArrow = true)
        SettingItem("图片、视频、文件和通话", showArrow = true)
        SettingItem("互动标识", showArrow = false)

        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle("其他")
        ToggleSettingItem("最近浏览内容自动添加彩签", true)
        ToggleSettingItem("摇动手机截屏", false)
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SettingItem(
    title: String,
    subtitle: String? = null,
    showArrow: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        if (showArrow) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun ToggleSettingItem(title: String, isEnabled: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Switch(checked = isEnabled, onCheckedChange = {})
    }
}