package com.efbsm5.easyway.ui.page.communityPage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.efbsm5.easyway.R
import com.efbsm5.easyway.data.models.DynamicPost
import com.efbsm5.easyway.map.MapUtil
import com.efbsm5.easyway.ui.components.TopBar
import com.efbsm5.easyway.viewmodel.pageViewmodel.NewPostPageViewModel


@Composable
fun NewDynamicPostPage(back: () -> Unit, viewModel: NewPostPageViewModel) {
    val newPost by viewModel.newPost.collectAsState()
    val photos by viewModel.chosenPhotos.collectAsState()
    DynamicPostScreen(
        dynamicPost = newPost,
        onSelected = { viewModel.editPost(newPost.copy(type = it)) },
        onTitleChanged = { viewModel.editPost(newPost.copy(title = it)) },
        onContentChanged = { viewModel.editPost(newPost.copy(content = it)) },
        photos = photos,
        onSelectedPhoto = {
            it?.let {
                viewModel.getPicture(it)
            }
        },
        publish = {
            viewModel.push()
            back()
        },
        back = { back() },
        getLocation = { viewModel.getLocation() })
}

@Preview
@Composable
private fun DynamicPostScreen(
    dynamicPost: DynamicPost = MapUtil.getInitPost(),
    photos: List<Uri> = emptyList(),
    onSelected: (Int) -> Unit = {},
    onTitleChanged: (String) -> Unit = {},
    onContentChanged: (String) -> Unit = {},
    onSelectedPhoto: (Uri?) -> Unit = {},
    publish: () -> Unit = {},
    back: () -> Unit = {},
    getLocation: () -> String = { "" }
) {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            TopBar(back = back, text = stringResource(R.string.addPost))
            PublishToSection(onSelected = { onSelected(it) })
            Spacer(modifier = Modifier.height(16.dp))
            AddTitleAndContentSection(
                dynamicPost = dynamicPost,
                onTitleChanged = { onTitleChanged(it) },
                onContentChanged = { onContentChanged(it) })
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(16.dp))
            AddLocationAndImagesSection(
                selectedPhotos = photos,
                onSelectedPhoto = { it?.let { onSelectedPhoto(it) } },
                getLocation = getLocation
            )
            Spacer(modifier = Modifier.weight(1f))
            PublishButton(publish = { publish() })
        }
    }
}

@Composable
private fun PublishToSection(onSelected: (Int) -> Unit) {
    var selectedButton by remember { mutableStateOf("") }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("发布到：", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.width(8.dp))
        Row {
            PublishButton("活动", selectedButton) {
                if (it) {
                    onSelected(1)
                    selectedButton = "活动"
                } else {
                    onSelected(0)
                    selectedButton = ""
                }
            }
            PublishButton("互助", selectedButton) {
                if (it) {
                    onSelected(2)
                    selectedButton = "互助"
                } else {
                    onSelected(0)
                    selectedButton = ""
                }
            }
            PublishButton("互动", selectedButton) {
                if (it) {
                    onSelected(3)
                    selectedButton = "互动"
                } else {
                    onSelected(0)
                    selectedButton = ""
                }
            }
        }
    }
}

@Composable
private fun PublishButton(label: String, selectedButton: String, onSelected: (Boolean) -> Unit) {
    val isSelected = selectedButton == label
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    )
    Button(
        onClick = {
            if (isSelected) {
                onSelected(false)
            } else {
                onSelected(true)
            }
        },
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Text(label, color = if (isSelected) Color.White else Color.Black)
    }
}


@Composable
private fun AddTitleAndContentSection(
    dynamicPost: DynamicPost, onTitleChanged: (String) -> Unit, onContentChanged: (String) -> Unit
) {
    Column {
        TextField(
            value = dynamicPost.title,
            onValueChange = { onTitleChanged(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("添加标题") },
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = dynamicPost.content,
            onValueChange = { onContentChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = { Text("添加正文") },
        )
    }
}

@Composable
private fun AddLocationAndImagesSection(
    selectedPhotos: List<Uri>, onSelectedPhoto: (Uri?) -> Unit, getLocation: () -> String
) {
    var location by remember { mutableStateOf("") }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onSelectedPhoto(result.data?.data)
        }
    }
    var showDialog by remember { mutableStateOf<Uri?>(null) }
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                location = getLocation()
            }) {
                Icon(
                    Icons.Default.LocationOn, contentDescription = "添加地点"
                )
            }
            Text(if (location.isNotEmpty()) location else "添加地点", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(4), modifier = Modifier.height(200.dp)
        ) {
            items(selectedPhotos.size) { index ->
                val photoUri = selectedPhotos[index]
                Image(
                    painter = rememberAsyncImagePainter(photoUri),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(4.dp)
                        .clickable { showDialog = photoUri })
            }
            item {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(MaterialTheme.colorScheme.onSecondary)
                        .border(1.dp, MaterialTheme.colorScheme.primary)
                        .clickable {
                            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                addCategory(Intent.CATEGORY_OPENABLE)
                                type = "image/*"
                            }
                            launcher.launch(intent)
                        }, contentAlignment = Alignment.Center
                ) {
                    Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
    if (showDialog != null) {
        AlertDialog(onDismissRequest = { showDialog = null }, text = {
            Image(
                painter = rememberAsyncImagePainter(showDialog),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth()
            )
        }, confirmButton = {
            TextButton(onClick = { showDialog = null }) {
                Text("关闭")
            }
        })
    }
}

@Composable
private fun PublishButton(publish: () -> Unit) {
    Button(
        onClick = { publish() },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(24.dp),
    ) {
        Text("发布帖子", color = Color.White, fontSize = 16.sp)
    }
}