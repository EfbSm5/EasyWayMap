package com.efbsm5.easyway.repo

import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.data.models.assistModel.PointWithComments
import com.efbsm5.easyway.data.models.assistModel.PostWithComments
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class HomePageSnapshot(
    val user: User,
    val points: List<PointWithComments>,
    val posts: List<PostWithComments>,
)

fun interface HomePageDataSource {
    fun observeHomePage(userId: Int): Flow<HomePageSnapshot>
}

internal fun combineHomePageData(
    userFlow: Flow<User?>,
    pointsFlow: Flow<List<PointWithComments>>,
    postsFlow: Flow<List<PostWithComments>>,
    fallbackUser: User,
): Flow<HomePageSnapshot> = combine(userFlow, pointsFlow, postsFlow) { user, points, posts ->
    HomePageSnapshot(
        user = user ?: fallbackUser,
        points = points,
        posts = posts,
    )
}
