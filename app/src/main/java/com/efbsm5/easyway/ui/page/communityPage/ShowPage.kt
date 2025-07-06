package com.efbsm5.easyway.ui.page.communityPage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.efbsm5.easyway.R
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.model.ImmutableListWrapper
import com.efbsm5.easyway.ui.components.PostList
import com.efbsm5.easyway.ui.components.TabSection
import com.efbsm5.easyway.ui.components.TopBar

@Preview
@Composable
fun ShowPage(
    back: () -> Unit = {},
    posts: ImmutableListWrapper<PostAndUser> = ImmutableListWrapper(emptyList()),
    titleText: String = "心无距离，共享每一刻",
    onSelect: (Int) -> Unit = {},
    onClick: (PostAndUser) -> Unit = {},
    search: (String) -> Unit = {}
) {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(back = back, text = titleText)
            BannerSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            SearchBar(
                search = search,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp)
                    ),
            )
            TabSection(
                onSelect = { onSelect(it) }, tabs = listOf("全部", "活动", "互助", "分享")
            )
            PostList(posts = posts.items, onClick = { onClick(it) })
        }
    }
}


@Composable
private fun BannerSection(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.img),
        contentDescription = "活动横幅",
        modifier = modifier
    )
}

@Composable
private fun SearchBar(search: (String) -> Unit, modifier: Modifier) {
    var text by rememberSaveable { mutableStateOf("") }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        TextField(
            value = text, onValueChange = { text = it }, modifier = Modifier.background(
                MaterialTheme.colorScheme.background, shape = RoundedCornerShape(8.dp)
            ), placeholder = { Text("搜索") })
        Spacer(modifier = Modifier.width(20.dp))
        IconButton(
            onClick = { search(text) },
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "搜索",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

