package com.efbsm5.easyway.ui.components.mapcards

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.map.MapUtil
import com.efbsm5.easyway.viewmodel.componentsViewmodel.NewPointCardViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun NewPointCard(
    changeScreen: (Screen) -> Unit,
    viewModel: NewPointCardViewModel,
    label: String
) {
    val context = LocalContext.current
    val newPoint by viewModel.tempPoint.collectAsState()
    NewPointCardSurface(
        label = label,
        point = newPoint,
        onInfoValueChange = { viewModel.changeTempPoint(newPoint.copy(info = it)) },
        onLocationValueChange = { viewModel.changeTempPoint(newPoint.copy(location = it)) },
        onUploadImage = { uri ->
            uri?.let { uri1 ->
                val inputStream = context.contentResolver.openInputStream(uri1)
                inputStream?.let {
                    val file = File(context.cacheDir, "temp_image")
                    val outputStream = FileOutputStream(file)
                    it.copyTo(outputStream)
                    outputStream.close()
                    it.close()
                    viewModel.changeTempPoint(newPoint.copy(photo = file.toUri()))
                }
            }
        },
        onSelectType = {
            viewModel.changeTempPoint(newPoint.copy(type = it))
        },
        callback = {
            if (it) {
                viewModel.getLocation().apply {
                    viewModel.changeTempPoint(
                        newPoint.copy(
                            lat = latitude, lng = longitude
                        )
                    )
                }

                viewModel.publishPoint()
                changeScreen(Screen.Function)
            } else {
                changeScreen(Screen.Function)
            }
        },
        onNameValueChange = { viewModel.changeTempPoint(newPoint.copy(name = it)) },
    )
}

@Composable
private fun NewPointCardSurface(
    label: String,
    point: EasyPoint = MapUtil.getInitPoint(),
    onInfoValueChange: (String) -> Unit = {},
    onLocationValueChange: (String) -> Unit = {},
    onNameValueChange: (String) -> Unit = {},
    onUploadImage: (Uri?) -> Unit = {},
    onSelectType: (String) -> Unit = {},
    callback: (Boolean) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            DropdownField(onSelectType = { onSelectType(it) })
            Spacer(modifier = Modifier.height(16.dp))
            TextFieldWithText(
                label = "设施名", text = point.name
            ) { onNameValueChange(it) }
            Spacer(modifier = Modifier.height(16.dp))
            TextFieldWithText(label = "设施说明", text = point.info) { onInfoValueChange(it) }
            Spacer(modifier = Modifier.height(16.dp))
            TextFieldWithText(
                label = "所在位置", text = point.location
            ) { onLocationValueChange(it) }
            Spacer(modifier = Modifier.height(16.dp))
            UploadImageSection { onUploadImage(it) }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    callback(true)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "确认上传", color = MaterialTheme.colorScheme.onPrimary)
            }
            Button(
                onClick = { callback(false) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(text = "取消", color = MaterialTheme.colorScheme.onSecondary)
            }
        }
    }
}

@Composable
private fun DropdownField(
    onSelectType: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }
    Column {
        Text(
            text = "设施类别",
            style = TextStyle(fontSize = 16.sp),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Box(
            modifier = Modifier
                .border(1.dp, Color.Gray)
                .padding(8.dp)
                .fillMaxWidth()
                .clickable { expanded = true }) {
            Text(text = selectedOption.ifEmpty { "请选择" })
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf(
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
            ).forEach { option ->
                DropdownMenuItem(onClick = {
                    onSelectType(option)
                    expanded = false
                    selectedOption = option
                }, text = { Text(option) })
            }
        }
    }
}

@Composable
private fun TextFieldWithText(label: String, text: String, onValueChange: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = TextStyle(fontSize = 16.sp),
            modifier = Modifier
                .width(70.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
        TextField(
            value = text, onValueChange = { onValueChange(it) }, modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun UploadImageSection(onChoosePicture: (Uri?) -> Unit) {
    var selectedImageUri: Uri? by remember { mutableStateOf(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onChoosePicture(result.data?.data)
            selectedImageUri = result.data?.data
        }
    }
    Column {
        Text(text = "上传图片", style = TextStyle(fontSize = 16.sp))
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(MaterialTheme.colorScheme.onBackground)
                .border(1.dp, MaterialTheme.colorScheme.primary)
                .clickable {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "image/*"
                    }
                    launcher.launch(intent)
                }, contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(model = selectedImageUri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(text = "严禁上传无关图片", color = Color.Red, fontSize = 12.sp)
            }
        }
    }
}

