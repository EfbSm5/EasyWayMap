package com.efbsm5.easyway.ui.page.homepage

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.efbsm5.easyway.data.models.DynamicPost
import com.efbsm5.easyway.data.models.assistModel.DynamicPostAndUser
import com.efbsm5.easyway.ui.components.DynamicPostList
import com.efbsm5.easyway.ui.page.communityPage.DetailPage
import com.efbsm5.easyway.viewmodel.pageViewmodel.DetailPageViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ShowPostPage(posts: List<DynamicPostAndUser>) {
    var state by remember { mutableStateOf<PostPageState>(PostPageState.No) }
    if (posts.isNotEmpty()) {
        state = PostPageState.All(posts)

    }
    when (state) {
        is PostPageState.All -> DynamicPostList(
            posts = posts, onClick = { state = PostPageState.Detail(it.dynamicPost) })

        is PostPageState.Detail -> {
            val detailPageViewModel: DetailPageViewModel =
                koinViewModel(parameters = { parametersOf((state as PostPageState.Detail).dynamicPost) })
            DetailPage(
                onBack = { state = PostPageState.All(posts) },
                viewModel = detailPageViewModel
            )
        }

        PostPageState.No -> Box(contentAlignment = Alignment.Center) {
            Text("没有发过动态哦")
        }
    }
}

sealed interface PostPageState {
    data object No : PostPageState
    data class Detail(val dynamicPost: DynamicPost) : PostPageState
    data class All(val posts: List<DynamicPostAndUser>) : PostPageState
}