package com.efbsm5.easyway.ui.components.mapcards

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.amap.api.maps.model.LatLng
import com.amap.api.services.core.PoiItemV2
import com.efbsm5.easyway.R
import com.efbsm5.easyway.addPoiItem
import com.efbsm5.easyway.calculateDistance
import com.efbsm5.easyway.convertToLatLng
import com.efbsm5.easyway.data.LocationSaver
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.formatDistance
import com.efbsm5.easyway.getLatlng
import com.efbsm5.easyway.model.ImmutableListWrapper
import com.efbsm5.easyway.ui.components.NavigationDialog
import com.efbsm5.easyway.ui.components.TabSection
import com.efbsm5.easyway.viewmodel.componentsViewmodel.FunctionCardViewModel

@Composable
fun FunctionCard(
    changeScreen: (CardScreen) -> Unit, navigate: (LatLng) -> Unit
) {
    val viewModel: FunctionCardViewModel = viewModel()
    val currentState by viewModel.uiState.collectAsState()

    FunctionCardScreen(
        onclick = {
            viewModel.search(
                string = it, page = 1
            )
        },
        poiItemV2s = currentState.poiList,
        changeScreen = changeScreen,
        navigate = navigate,
        easyPoints = currentState.points
    )
}

@Composable
private fun FunctionCardScreen(
    onclick: (String) -> Unit = { },
    poiItemV2s: ImmutableListWrapper<PoiItemV2> = ImmutableListWrapper(emptyList()),
    easyPoints: ImmutableListWrapper<EasyPoint> = ImmutableListWrapper(emptyList()),
    changeScreen: (CardScreen) -> Unit,
    navigate: (LatLng) -> Unit
) {
    var isSearching by rememberSaveable { mutableStateOf(false) }
    var destination by rememberSaveable { mutableStateOf(LatLng(0.0, 0.0)) }
    var name = ""
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        SearchBar(onClick = {
            onclick(it)
            isSearching = true
        })
        Spacer(Modifier.height(10.dp))
        if (isSearching) {
            SearchPart(
                poiItemV2s = poiItemV2s.items,
                easyPoints = easyPoints.items,
                onPoiItemV2Selected = { changeScreen(CardScreen.Comment(addPoiItem(it))) },
                onPointSelected = { changeScreen(CardScreen.Comment(it)) },
                navigate = { _destination, _name ->
                    destination = _destination
                    name = _name
                })
        } else IconGrid {
            onclick(it)
            isSearching = true
        }
        if (LatLng(0.0, 0.0) != destination) NavigationDialog(
            location = destination, name = name, navigate = {
                navigate
                destination = LatLng(0.0, 0.0)
            })
    }
}

@Composable
private fun SearchBar(onClick: (String) -> Unit) {
    var text by rememberSaveable { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("搜索") },
            modifier = Modifier.clip(RoundedCornerShape(9))
        )
        IconButton(onClick = { onClick(text) }) {
            Icon(
                Icons.Default.Search, contentDescription = "search"
            )
        }
    }
}

@Composable
private fun IconGrid(onclick: (String) -> Unit) {
    val items = listOf(
        Pair(R.drawable.car, "汽车"),
        Pair(R.drawable.heart, "爱心站点"),
        Pair(R.drawable.rest, "娱乐设施"),
        Pair(R.drawable.park, "停车位"),
        Pair(R.drawable.lift, "电梯"),
        Pair(R.drawable.toliet, "厕所"),
        Pair(R.drawable.podao, "坡道"),
        Pair(R.drawable.lunyi, "轮椅租赁"),
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(4), modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), content = {
            items(items.size) { index ->
                val item = items[index]
                IconAndName(iconRes = item.first, text = item.second) { onclick(item.second) }
            }
        })
}

@Composable
private fun IconAndName(iconRes: Int, text: String, onclick: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(19))
            .clickable { onclick() }) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = text,
            modifier = Modifier
                .size(50.dp)
                .padding(bottom = 8.dp),
//            tint = MaterialTheme.colorScheme.surfaceTint
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun SearchPart(
    poiItemV2s: List<PoiItemV2>,
    onPoiItemV2Selected: (poiItem: PoiItemV2) -> Unit,
    onPointSelected: (point: EasyPoint) -> Unit,
    easyPoints: List<EasyPoint> = emptyList(),
    navigate: (LatLng, String) -> Unit
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            TabSection(
                tabs = listOf("无障碍地点", "全部地点"), onSelect = {
                    selectedTabIndex = it
                })
        }
        item { Spacer(modifier = Modifier.height(50.dp)) }
        if (selectedTabIndex == 1) {
            items(poiItemV2s) { poiItem ->
                AccessiblePlaceItem(
                    imageRes = poiItem.photos.firstOrNull()?.url,
                    title = poiItem.title,
                    distance = calculateDistance(
                        LocationSaver.location, convertToLatLng(poiItem.latLonPoint)
                    ),
                    navigate = { navigate(convertToLatLng(poiItem.latLonPoint), poiItem.title) },
                    select = { onPoiItemV2Selected(poiItem) })
            }
        } else {
            items(easyPoints) { easyPoint ->
                AccessiblePlaceItem(
                    imageRes = easyPoint.photo,
                    title = easyPoint.name,
                    distance = calculateDistance(
                        LocationSaver.location, easyPoint.getLatlng()
                    ),
                    navigate = { navigate(easyPoint.getLatlng(), easyPoint.name) },
                    select = { onPointSelected(easyPoint) })
            }
        }

    }
}

@Composable
private fun AccessiblePlaceItem(
    imageRes: String?, title: String, distance: Float, navigate: () -> Unit, select: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                select()
            }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                rememberAsyncImagePainter(imageRes?.toUri()),
                contentDescription = "地点图片",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = distance.formatDistance(), color = Color.Gray, fontSize = 14.sp
                )
            }
            Button(
                onClick = { navigate() },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                modifier = Modifier.size(40.dp)
            ) {
                Text("路线", fontSize = 12.sp, color = Color.Blue)
            }
        }
    }
}

