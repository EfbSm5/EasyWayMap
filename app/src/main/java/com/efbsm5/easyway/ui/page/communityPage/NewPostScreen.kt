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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.efbsm5.easyway.LocationPoiActivity
import com.efbsm5.easyway.R
import com.efbsm5.easyway.SDKUtils
import com.efbsm5.easyway.contract.NewPostContract.Effect
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.getInitPost
import com.efbsm5.easyway.ui.components.TopBar
import com.efbsm5.easyway.viewmodel.pageViewmodel.NewPostViewModel


@Composable
fun NewPostPage(back: () -> Unit) {
    val viewModel: NewPostViewModel = viewModel()
    val currentState by viewModel.uiState.collectAsState()

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.setLocation(result.data?.getStringExtra("result_key") ?: "")
        }
    }
    val locationIntent = Intent(SDKUtils.getContext(), LocationPoiActivity::class.java)

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.getPicture(result.data?.data)
        }
    }
    val photoIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "image/*"
    }
    LaunchedEffect(viewModel.effect) {
        when (viewModel.effect) {
            Effect.GetPhoto -> {
                photoLauncher.launch(photoIntent)
            }

            Effect.GetLocation -> {
                locationLauncher.launch(locationIntent)
            }

            Effect.Back -> {
                back()
            }
        }
    }

    PostScreen(
        post = currentState.post,
        onSelected = viewModel::selectIndex,
        onTitleChanged = viewModel::changeTitle,
        onContentChanged = viewModel::changeContent,
        selectPhoto = viewModel::getPicture,
        publish = viewModel::push,
        back = viewModel::back,
        getLocation = viewModel::getLocation,
        selectedButton = currentState.post.type,
        getPhoto = viewModel::getPhoto,
        dialogPhoto = currentState.dialogData?.toUri()
    )
}

@Preview
@Composable
private fun PostScreen(
    post: Post = getInitPost(),
    onSelected: (Int) -> Unit = {},
    onTitleChanged: (String) -> Unit = {},
    onContentChanged: (String) -> Unit = {},
    selectPhoto: (Uri?) -> Unit = {},
    publish: () -> Unit = {},
    back: () -> Unit = {},
    getLocation: () -> Unit = { },
    selectedButton: Int = 0,
    getPhoto: () -> Unit = {},
    dialogPhoto: Uri? = null
) {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            TopBar(back = back, text = stringResource(R.string.addPost))
            PublishToSection(
                onSelected = onSelected, selectedButton = selectedButton
            )
            Spacer(modifier = Modifier.height(16.dp))
            AddTitleAndContentSection(
                post = post,
                onTitleChanged = { onTitleChanged(it) },
                onContentChanged = { onContentChanged(it) })
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(16.dp))
            AddLocation(
                location = post.position, getLocation = getLocation
            )
            ImagesSection(
                selectedPhotos = post.photo,
                getPhoto = getPhoto,
                dialogPhoto = dialogPhoto,
                setDialogPhoto = selectPhoto
            )
            Spacer(modifier = Modifier.weight(1f))
            PublishButton(publish = { publish() })
        }
    }
}

@Composable
private fun PublishToSection(onSelected: (Int) -> Unit, selectedButton: Int) {
    val typeList = listOf("", "活动", "互助", "互动")
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("发布到：", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.width(8.dp))
        for (index in typeList.indices) {
            PublishButton(
                label = typeList[index], onSelected = {
                    if (it) {
                        onSelected(index)
                    } else {
                        onSelected(0)
                    }
                }, isSelected = selectedButton == index
            )
        }
    }
}

@Composable
private fun PublishButton(label: String, isSelected: Boolean, onSelected: (Boolean) -> Unit) {
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
    post: Post, onTitleChanged: (String) -> Unit, onContentChanged: (String) -> Unit
) {
    TextField(
        value = post.title,
        onValueChange = { onTitleChanged(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("添加标题") },
    )
    Spacer(modifier = Modifier.height(8.dp))
    TextField(
        value = post.content,
        onValueChange = { onContentChanged(it) },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        placeholder = { Text("添加正文") },
    )

}

@Composable
private fun AddLocation(
    location: String,
    getLocation: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {
            getLocation()
        }) {
            Icon(
                Icons.Default.LocationOn, contentDescription = "添加地点"
            )
        }
        Text(location.ifEmpty { "添加地点" }, fontSize = 16.sp)
    }
}

@Composable
private fun ImagesSection(
    selectedPhotos: List<String>,
    getPhoto: () -> Unit,
    dialogPhoto: Uri?,
    setDialogPhoto: (Uri?) -> Unit
) {
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
                    .clickable { setDialogPhoto(photoUri.toUri()) })
        }
        item {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(MaterialTheme.colorScheme.onSecondary)
                    .border(1.dp, MaterialTheme.colorScheme.primary)
                    .clickable {
                        getPhoto
                    }, contentAlignment = Alignment.Center
            ) {
                Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }

    }
    if (dialogPhoto != null) {
        AlertDialog(onDismissRequest = { setDialogPhoto(null) }, text = {
            Image(
                painter = rememberAsyncImagePainter(dialogPhoto),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth()
            )
        }, confirmButton = {
            TextButton(onClick = { setDialogPhoto(null) }) {
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