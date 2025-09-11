package com.efbsm5.easyway.ui.page.communityPage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.efbsm5.easyway.R
import com.efbsm5.easyway.contract.community.CommunityContract
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.ui.FabConfig
import com.efbsm5.easyway.ui.LocalScaffoldController
import com.efbsm5.easyway.ui.components.AppTopBar
import com.efbsm5.easyway.ui.components.PostList
import com.efbsm5.easyway.ui.components.TabSection
import com.efbsm5.easyway.viewmodel.communityViewModel.CommunityViewModel

@Composable
fun CommunitySquareRoute(
    back: () -> Unit,
    onSelectPost: (PostAndUser) -> Unit,
    onCreateNew: () -> Unit,
    viewModel: CommunityViewModel = viewModel(),
    posts: List<PostAndUser>,
    loading: Boolean
) {
    val currentState by viewModel.uiState.collectAsState()
    val controller = LocalScaffoldController.current
    controller.setFab(
        FabConfig(
            icon = Icons.Default.Add,
            onClick = onCreateNew,
            visible = true,
        )
    )
    LaunchedEffect(posts) {
        viewModel.selectPost(posts)
    }
    // 收集一次性事件
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CommunityContract.Effect.SelectedPost -> onSelectPost(effect.postAndUser)
                CommunityContract.Effect.Back -> back()
            }
        }
    }

    CommunitySquareScreen(
        state = currentState,
        onEvent = viewModel::handleEvents,
        back = viewModel::back,
        isLoading = loading,
        filteredPosts = currentState.filterPosts,
    )
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunitySquareScreen(
    state: CommunityContract.State = CommunityContract.State(),
    isLoading: Boolean = false,
    filteredPosts: List<PostAndUser> = emptyList(),
    onEvent: (CommunityContract.Event) -> Unit = {},
    back: () -> Unit = {},
) {
    val listState = rememberLazyListState()
    if (isLoading && filteredPosts.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        CommunitySquareSkeleton()
    }

    // 全屏错误
    if (state.error != null && filteredPosts.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(state.error)
        }

    }

    LazyColumn(
        state = listState, modifier = Modifier.fillMaxSize()
    ) {
        item {
            AppTopBar(title = "test", onBack = back)
        }
        item("banner") {
            BannerCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
        item("search") {
            SearchTextField(
                value = state.searchText,
                onValueChange = { onEvent(CommunityContract.Event.EditText(it)) },
                onSubmit = { onEvent(CommunityContract.Event.Submit) })
        }
        item("tabs") {
            TabSection(
                selectedIndex = state.selectedTab,
                tabs = listOf("全部", "活动", "互助", "分享"),
                onSelect = { onEvent(CommunityContract.Event.TabSelect(it)) })
        }

        if (!isLoading && state.searchText.isNotBlank()) {
            item("activeSearchTag") {
//                    AssistChip(
//                        onClick = { /* 可加清空逻辑 */ },
//                        label = { Text("筛选：$searchText") },
//                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
//                    )
            }
        }

        if (filteredPosts.isEmpty()) {
            item("empty") {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(48.dp), contentAlignment = Alignment.Center
                ) {
                    Text("暂无内容")
                }
            }
        } else {
            item {
                PostList(
                    posts = filteredPosts,
                    onClickPost = { onEvent(CommunityContract.Event.ClickPost(it)) })
            }
        }

        if (isLoading && filteredPosts.isNotEmpty()) {
            item("loadingMore") {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp), strokeWidth = 3.dp
                    )
                }
            }
        }

        item("bottomSpace") { Spacer(Modifier.height(40.dp)) }
    }
}


@Composable
private fun SearchTextField(
    value: String, onValueChange: (String) -> Unit, onSubmit: () -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        placeholder = { Text("搜索帖子、活动...") },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        trailingIcon = {
            IconButton(onClick = onSubmit) {
                Icon(Icons.Default.Search, contentDescription = "搜索")
            }
        },
        keyboardActions = KeyboardActions(
            onSearch = { onSubmit() })
    )
}


@Composable
private fun BannerCard(modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 4.dp,
        shadowElevation = 6.dp,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = "活动横幅",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun CommunitySquareSkeleton(
    modifier: Modifier = Modifier, postItemCount: Int = 6
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Banner
        item("sk_banner") {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(20.dp))
            )
        }

        // Tabs 条
        item("sk_tabs") {
            Row(
                Modifier
                    .padding(horizontal = 16.dp)
                    .height(42.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(4) { idx ->
                    Box(
                        Modifier
                            .padding(end = if (idx != 3) 12.dp else 0.dp)
                            .size(width = 54.dp, height = 24.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            }
        }

        items(postItemCount, key = { "sk_post_$it" }) {
            PostSkeletonItem()
        }
    }
}

@Composable
private fun PostSkeletonItem() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // 标题行
        Box(
            Modifier
                .fillMaxWidth(0.65f)
                .height(18.dp)
                .clip(RoundedCornerShape(6.dp))
        )
        Spacer(Modifier.height(10.dp))
        // 内容 1
        Box(
            Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        Spacer(Modifier.height(6.dp))
        // 内容 2
        Box(
            Modifier
                .fillMaxWidth(0.85f)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        Spacer(Modifier.height(12.dp))
        // 作者行
        Box(
            Modifier
                .width(92.dp)
                .height(12.dp)
                .clip(RoundedCornerShape(4.dp))

        )
    }
    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
}



