package com.efbsm5.easyway.ui.components.mapcards

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.amap.api.maps.model.LatLng
import com.amap.api.services.core.PoiItemV2
import com.efbsm5.easyway.R
import com.efbsm5.easyway.addPoiItem
import com.efbsm5.easyway.calculateDistance
import com.efbsm5.easyway.contract.card.FunctionCardContract
import com.efbsm5.easyway.convertToLatLng
import com.efbsm5.easyway.data.LocationSaver
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.formatDistance
import com.efbsm5.easyway.getLatlng
import com.efbsm5.easyway.viewmodel.cardViewmodel.FunctionCardViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun FunctionCard(
    changeScreen: (CardScreen) -> Unit, navigate: (LatLng) -> Unit,
    viewModel: FunctionCardViewModel = viewModel()

) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.effect.onEach {
            when (it) {
                is FunctionCardContract.Effect.Search -> viewModel.search(it.searchString, 1)
            }
        }.collect()
    }
    FunctionCardScreen(
        state = state,
        onSearch = { key -> viewModel.search(key, page = 1) },
        changeScreen = changeScreen,
        navigate = navigate
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FunctionCardScreen(
    state: FunctionCardContract.State = FunctionCardContract.State(),
    onSearch: (String) -> Unit,
    changeScreen: (CardScreen) -> Unit,
    navigate: (LatLng) -> Unit
) {
    var isSearching by rememberSaveable { mutableStateOf(false) }
    var destination by rememberSaveable { mutableStateOf<LatLng?>(null) }
    var destinationName by rememberSaveable { mutableStateOf("") }

    // 顶部渐变背景
    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
            MaterialTheme.colorScheme.background
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Column(Modifier.fillMaxSize()) {
            Crossfade(
                targetState = isSearching,
                animationSpec = spring(dampingRatio = 0.9f, stiffness = 300f),
                label = "modeCrossfade"
            ) { searching ->
                if (searching) {
                    SearchResultArea(
                        poiItemV2s = state.poiList.items,
                        easyPoints = state.points.items,
                        onPoiSelect = { changeScreen(CardScreen.Comment(addPoiItem(it))) },
                        onPointSelect = { changeScreen(CardScreen.Comment(it)) },
                        onNavigateClick = { latLng, name ->
                            destination = latLng
                            destinationName = name
                        })
                } else {
                    IconCategoryGrid(
                        onCategoryClick = {
                            onSearch(it)
                            isSearching = true
                        })
                }
            }
        }

        // 导航弹窗
        if (destination != null) {
            NavigationConfirmDialog(
                name = destinationName,
                onDismiss = { destination = null },
                onConfirm = {
                    destination?.let(navigate)
                    destination = null
                })
        }
    }
}

@Composable
private fun IconCategoryGrid(
    onCategoryClick: (String) -> Unit
) {
    val items = listOf(
        R.drawable.car to "汽车",
        R.drawable.heart to "爱心站点",
        R.drawable.rest to "娱乐设施",
        R.drawable.park to "停车位",
        R.drawable.lift to "电梯",
        R.drawable.toliet to "厕所",
        R.drawable.podao to "坡道",
        R.drawable.lunyi to "轮椅租赁"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
    ) {
        items(items.size) { idx ->
            val (res, label) = items[idx]
            CategoryChip(
                iconRes = res, text = label, onClick = { onCategoryClick(label) })
        }
    }
}

@Composable
private fun CategoryChip(
    iconRes: Int, text: String, onClick: () -> Unit
) {
    val bg by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.surface, label = "chipBg"
    )
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) { onClick() }
            .padding(vertical = 12.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = CircleShape, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = text,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(42.dp)
                    .padding(10.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = text, style = MaterialTheme.typography.labelMedium, maxLines = 1
        )
    }
}

@Composable
private fun SearchResultArea(
    poiItemV2s: List<PoiItemV2>,
    easyPoints: List<EasyPoint>,
    onPoiSelect: (PoiItemV2) -> Unit,
    onPointSelect: (EasyPoint) -> Unit,
    onNavigateClick: (LatLng, String) -> Unit
) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("无障碍地点", "全部地点")

    Column(Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = tab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = tab == index,
                    onClick = { tab = index },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    text = { Text(title) })
            }
        }

        Spacer(Modifier.height(8.dp))

        val list = if (tab == 0) easyPoints else poiItemV2s

        LazyColumn(
            modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            items(list.size) { idx ->
                if (tab == 0) {
                    val p = easyPoints[idx]
                    val distance = remember(p.pointId) {
                        calculateDistance(LocationSaver.location, p.getLatlng())
                    }
                    PlaceListItem(
                        imageUrl = p.photo,
                        title = p.name,
                        distance = distance.formatDistance(),
                        onClick = { onPointSelect(p) },
                        onNavigate = { onNavigateClick(p.getLatlng(), p.name) })
                } else {
                    val poi = poiItemV2s[idx]
                    val distance = remember(poi.poiId) {
                        calculateDistance(LocationSaver.location, convertToLatLng(poi.latLonPoint))
                    }
                    PlaceListItem(
                        imageUrl = poi.photos.firstOrNull()?.url,
                        title = poi.title,
                        distance = distance.formatDistance(),
                        onClick = { onPoiSelect(poi) },
                        onNavigate = {
                            onNavigateClick(
                                convertToLatLng(poi.latLonPoint), poi.title
                            )
                        })
                }
            }
        }
    }
}

@Composable
private fun PlaceListItem(
    imageUrl: String?, title: String, distance: String, onClick: () -> Unit, onNavigate: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true)
                    .diskCachePolicy(CachePolicy.ENABLED).memoryCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = title,
                modifier = Modifier
                    .size(66.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                AssistChip(
                    onClick = {},
                    label = { Text(distance) },
                    shape = RoundedCornerShape(50),
                    border = null
                )
            }

            FilledTonalButton(
                onClick = { onNavigate() },
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("路线", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun NavigationConfirmDialog(
    name: String, onDismiss: () -> Unit, onConfirm: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(onClick = {
            onConfirm()
        }) { Text("开始导航") }
    }, dismissButton = {
        TextButton(onClick = onDismiss) { Text("取消") }
    }, title = { Text("前往") }, text = { Text("是否导航到：$name") })
}
