package com.efbsm5.easyway.ui.components.mapcards

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.efbsm5.easyway.contract.card.NewPointCardContract
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.getInitPoint
import com.efbsm5.easyway.viewmodel.cardViewmodel.NewPointCardViewModel

// 设施类别常量（可后续本地化）
private val CATEGORY_OPTIONS = listOf(
    "无障碍电梯",
    "无障碍厕所",
    "停车位",
    "公共交通",
    "轮椅租赁",
    "爱心站点",
    "AED",
    "坡道",
    "无障碍汽车",
    "其他"
)

@Composable
fun NewPointCard(
    changeScreen: (CardScreen) -> Unit,
    label: String,
    viewModel: NewPointCardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Effect 处理
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                NewPointCardContract.Effect.Upload -> {
                    // 可添加上传中提示
                }

                is NewPointCardContract.Effect.UploadPhoto -> {
                    // 已在内部处理（如果需要可在此做 UI 提示）
                }

                NewPointCardContract.Effect.Back -> changeScreen(CardScreen.Function)
            }
        }
    }

    NewPointForm(
        title = label,
        point = uiState.tempPoint,
        onNameChange = viewModel::changeNameValue,
        onInfoChange = viewModel::changeInfoValue,
        onLocationChange = viewModel::changeLocation,
        onTypeChange = viewModel::changeType,
        onUploadImage = { uri ->
            uri?.let {
//                viewModel.handlePhotoUri(it) // 你可以在 VM 中封装读取文件逻辑
            }
        },
        onSubmit = { viewModel.callback(true) },
        onCancel = { viewModel.callback(false) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewPointForm(
    title: String,
    point: EasyPoint = getInitPoint(),
    onNameChange: (String) -> Unit,
    onInfoChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onUploadImage: (Uri?) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    rememberCoroutineScope()

    // 本地 UI 状态（表单）
    var facilityName by rememberSaveable { mutableStateOf(point.name) }
    var facilityInfo by rememberSaveable { mutableStateOf(point.info) }
    var facilityLocation by rememberSaveable { mutableStateOf(point.location) }
    var facilityType by rememberSaveable { mutableStateOf(point.type) }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    // 校验逻辑
    val isNameValid = facilityName.trim().length in 2..30
    val isTypeValid = facilityType.isNotBlank()
    val isLocationValid = facilityLocation.trim().isNotEmpty()
    val isFormValid = isNameValid && isTypeValid && isLocationValid

    // 回写到外层（只要变更就同步）
    LaunchedEffect(facilityName) { onNameChange(facilityName) }
    LaunchedEffect(facilityInfo) { onInfoChange(facilityInfo) }
    LaunchedEffect(facilityLocation) { onLocationChange(facilityLocation) }
    LaunchedEffect(facilityType) { onTypeChange(facilityType) }

    // 选择图片 Launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            val uri = res.data?.data
            imageUri = uri
            onUploadImage(uri)
        }
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.Start
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            Spacer(Modifier.height(16.dp))

            // 类别选择
            TypeSelector(
                selected = facilityType, onSelected = { facilityType = it })

            Spacer(Modifier.height(16.dp))

            // 设施名
            OutlinedTextField(
                value = facilityName,
                onValueChange = { facilityName = it },
                label = { Text("设施名 *") },
                singleLine = true,
                supportingText = {
                    if (!isNameValid && facilityName.isNotBlank()) Text("长度需在 2-30 字之间")
                },
                isError = !isNameValid && facilityName.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            // 说明
            OutlinedTextField(
                value = facilityInfo,
                onValueChange = { facilityInfo = it },
                label = { Text("设施说明") },
                placeholder = { Text("可描述可用情况、开放时间、注意事项等") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            // 所在位置
            OutlinedTextField(
                value = facilityLocation,
                onValueChange = { facilityLocation = it },
                label = { Text("所在位置 *") },
                singleLine = false,
                supportingText = {
                    if (!isLocationValid && facilityLocation.isNotBlank()) Text("请输入位置描述")
                },
                isError = !isLocationValid && facilityLocation.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(18.dp))

            // 图片上传
            UploadImageBlock(imageUri = imageUri, onPick = {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "image/*"
                }
                launcher.launch(intent)
            }, onClear = {
                imageUri = null
                onUploadImage(null)
            })

            Spacer(Modifier.height(8.dp))
            AssistChip(
                onClick = {},
                label = { Text("提示：仅上传与该设施相关的清晰照片") },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                ),
                leadingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = false)
                })

            Spacer(Modifier.height(26.dp))

            // 操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    onClick = { onCancel() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text("取消")
                }
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp), enabled = isFormValid, onClick = {
                        if (isFormValid) onSubmit()
                    }) {
                    Text("确认上传")
                }
            }
            Spacer(Modifier.height(8.dp))
            if (!isFormValid) {
                Text(
                    text = "请完善带 * 的必填项后再提交",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TypeSelector(
    selected: String, onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(
            text = "设施类别 *",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = selected,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("请选择类别") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                CATEGORY_OPTIONS.forEach { option ->
                    DropdownMenuItem(text = {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                option, maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                            if (option == selected) {
                                Text("已选", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }, onClick = {
                        onSelected(option)
                        expanded = false
                    })
                }
            }
        }
    }
}

@Composable
private fun UploadImageBlock(
    imageUri: Uri?, onPick: () -> Unit, onClear: () -> Unit
) {
    Column {
        Text(
            text = "图片（可选）",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable { onPick() },
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ) {
            Box(
                modifier = Modifier
                    .height(160.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Crossfade(targetState = imageUri, label = "imageCrossfade") { uri ->
                    if (uri == null) {
                        Text(
                            "点击选择图片", style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current).data(uri)
                                .crossfade(true).build(),
                            contentDescription = "设施照片",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                if (imageUri != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                            .clickable { onClear() }
                            .padding(horizontal = 10.dp, vertical = 4.dp)) {
                        Text(
                            "移除", style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.error
                            )
                        )
                    }
                }
            }
        }
    }
}
