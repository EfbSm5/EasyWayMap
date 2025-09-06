package com.efbsm5.easyway.ui.page.communityPage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.efbsm5.easyway.LocationPoiActivity
import com.efbsm5.easyway.R
import com.efbsm5.easyway.SDKUtils
import com.efbsm5.easyway.contract.community.NewPostContract
import com.efbsm5.easyway.contract.community.NewPostContract.Effect
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.ui.components.AppTopBar
import com.efbsm5.easyway.viewmodel.communityViewModel.NewPostViewModel


@Composable
fun NewPostPage(
    back: () -> Unit, onPostSuccess: (PostAndUser) -> Unit, viewModel: NewPostViewModel
) {
    val currentState by viewModel.uiState.collectAsState()

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.setLocation(result.data?.getStringExtra("result_key") ?: "")
        }
    }
    val locationIntent = remember { Intent(SDKUtils.getContext(), LocationPoiActivity::class.java) }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.getPicture(result.data?.data)
        }
    }
    val photoIntent = remember {
        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
    }
    LaunchedEffect(viewModel.effect) {
        when (viewModel.effect) {
            Effect.GetPhoto -> {
                locationLauncher.launch(
                    locationIntent
                )
            }

            Effect.GetLocation -> {
                photoLauncher.launch(photoIntent)
            }

            Effect.Back -> back()
        }
    }

    PostScreen(
        state = currentState,
        onEvent = viewModel::onEvent,
        onEffect = viewModel::onEffect,
        back = back
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostScreen(
    state: NewPostContract.State,
    onEvent: (NewPostContract.Event) -> Unit,
    onEffect: (Effect) -> Unit,
    back: () -> Unit
) {
    // 统一滚动
    val scrollState = rememberScrollState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .imePadding()
                .navigationBarsPadding()
        ) {
            AppTopBar(onBack = back, title = stringResource(R.string.addPost))

            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                CategorySection(
                    selectedIndex = state.onSelectedCategory,
                    onSelected = { onEvent(NewPostContract.Event.SelectedCategory(it)) }
                )

                Spacer(Modifier.height(20.dp))

                PostContentCard(
                    title = state.post.title,
                    content = state.post.content,
                    onTitleChanged = { onEvent(NewPostContract.Event.TitleChanged(it)) },
                    onContentChanged = { onEvent(NewPostContract.Event.EditContent(it)) }
                )

                Spacer(Modifier.height(20.dp))

                LocationSection(
                    location = state.post.position,
                    onGetLocation = { onEffect(Effect.GetLocation) }
                )

                Spacer(Modifier.height(20.dp))

                ImagesSection(
                    selectedPhotos = state.post.photo,
                    onAddPhoto = { onEffect(Effect.GetPhoto) },
                    previewDialogPhoto = state.previewPhoto,
                    setPreviewDialogPhoto = { onEvent(NewPostContract.Event.PickPhotoDialogResult(it)) }
                )

                Spacer(Modifier.height(28.dp))
            }

            // 底部发布按钮悬浮/固定在底部
            Surface(
                tonalElevation = 6.dp, shadowElevation = 8.dp
            ) {
                PostActionButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    onPublish = { onEvent(NewPostContract.Event.Publish) },
                    enabled = state.post.title.isNotBlank() && state.post.content.isNotBlank()
                )
            }
        }
    }
}

@Composable
private fun CategorySection(
    selectedIndex: Int, onSelected: (Int) -> Unit
) {
    val typeList = listOf("全部", "活动", "互助", "互动")
    Text(
        text = "发布到",
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
    ) {
        typeList.forEachIndexed { index, label ->
            CategoryChip(
                label = label, selected = selectedIndex == index, onClick = { onSelected(index) })
        }
    }
}

@Composable
private fun CategoryChip(
    label: String, selected: Boolean, onClick: () -> Unit
) {
    val bg by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        label = ""
    )
    val contentColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = ""
    )
    val scale by animateFloatAsState(if (selected) 1.02f else 1f, label = "")

    Surface(
        color = bg,
        shape = RoundedCornerShape(24.dp),
        tonalElevation = if (selected) 4.dp else 0.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))

            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            color = contentColor,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostContentCard(
    title: String,
    content: String,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit
) {
    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChanged,
                placeholder = { Text("添加标题（必填）") },
                maxLines = 1,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 18.sp, fontWeight = FontWeight.Medium
                )
            )
            Spacer(Modifier.height(14.dp))
            OutlinedTextField(
                value = content,
                onValueChange = onContentChanged,
                placeholder = { Text("添加正文内容…") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 140.dp),
                textStyle = LocalTextStyle.current.copy(lineHeight = 20.sp),
                supportingText = {
                    val count = content.length
                    Text("$count / 1000", fontSize = 12.sp)
                },
                maxLines = 10
            )
        }
    }
}

@Composable
private fun LocationSection(
    location: String, onGetLocation: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onGetLocation() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "添加地点",
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = location.ifBlank { "添加地点" },
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = if (location.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ImagesSection(
    selectedPhotos: List<String>,
    onAddPhoto: () -> Unit,
    previewDialogPhoto: Uri?,
    setPreviewDialogPhoto: (Uri?) -> Unit
) {
    val max = 9
    Text(
        text = "图片 (${selectedPhotos.size}/$max)",
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        state = gridState,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp, max = 260.dp)
    ) {
        items(selectedPhotos.size) { index ->
            val uri = selectedPhotos[index]
            PhotoThumbnail(
                uriString = uri, onClick = { setPreviewDialogPhoto(uri.toUri()) })
        }
        if (selectedPhotos.size < max) {
            item {
                AddPhotoCard(onAddPhoto = onAddPhoto)
            }
        }
    }

    if (previewDialogPhoto != null) {
        PhotoPreviewDialog(
            photo = previewDialogPhoto, onDismiss = { setPreviewDialogPhoto(null) })
    }
}

@Composable
private fun PhotoThumbnail(
    uriString: String, onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp,
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }) {
        Image(
            painter = rememberAsyncImagePainter(uriString),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun AddPhotoCard(onAddPhoto: () -> Unit) {
    val borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.2.dp, color = borderColor, shape = RoundedCornerShape(12.dp)
            )
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            .clickable { onAddPhoto() }, contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("+", fontSize = 28.sp, fontWeight = FontWeight.Black, color = borderColor)
            Text("添加", fontSize = 12.sp, color = borderColor)
        }
    }
}

@Composable
private fun PhotoPreviewDialog(
    photo: Uri, onDismiss: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(onClick = onDismiss) { Text("关闭") }
    }, text = {
        Image(
            painter = rememberAsyncImagePainter(photo),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        )
    })
}

@Composable
private fun PostActionButton(
    modifier: Modifier = Modifier, onPublish: () -> Unit, enabled: Boolean
) {
    val container =
        if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor =
        if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Button(
        onClick = onPublish,
        enabled = enabled,
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = contentColor,
            disabledContainerColor = container,
            disabledContentColor = contentColor
        ),
        modifier = modifier.height(52.dp)
    ) {
        Text("发布帖子", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}
