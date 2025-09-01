package com.efbsm5.easyway.ui.page.homepage

import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.assistModel.PointCommentAndUser

//@Composable
//fun ShowPostPage(posts: List<PointCommentAndUser>) {
//    var state by remember { mutableStateOf<PostPageState>(PostPageState.No) }
//    if (posts.isNotEmpty()) {
//        state = PostPageState.All(posts)
//
//    }
//    when (state) {
//        is PostPageState.All -> PostList(
//            posts = posts, onClick = { state = PostPageState.Detail(it.dynamicPost) })
//
//        is PostPageState.Detail -> {
//            DetailPage(
//                onBack = { state = PostPageState.All(posts) },
//                postAndUser = ,
//            )
//        }
//
//        PostPageState.No -> Box(contentAlignment = Alignment.Center) {
//            Text("没有发过动态哦")
//        }
//    }
//}

sealed interface PostPageState {
    data object No : PostPageState
    data class Detail(val dynamicPost: Post) : PostPageState
    data class All(val posts: List<PointCommentAndUser>) : PostPageState
}