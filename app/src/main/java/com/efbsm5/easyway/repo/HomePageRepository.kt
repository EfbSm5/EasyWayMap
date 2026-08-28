package com.efbsm5.easyway.repo

import com.efbsm5.easyway.data.database.dao.PointsDao
import com.efbsm5.easyway.data.database.dao.PostDao
import com.efbsm5.easyway.data.database.dao.UserDao
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

interface HomePageRepository {
    val userIds: Flow<Int>

    fun observeHomePage(userId: Int): Flow<HomePageSnapshot>
}

class RoomHomePageRepository(
    private val userDao: UserDao,
    private val pointsDao: PointsDao,
    private val postDao: PostDao,
    override val userIds: Flow<Int>,
    private val fallbackUser: User,
) : HomePageRepository {

    override fun observeHomePage(userId: Int): Flow<HomePageSnapshot> = combineHomePageData(
        userFlow = userDao.observeUserById(userId),
        pointsFlow = pointsDao.observePointWithCommentsByUserId(userId),
        postsFlow = postDao.observePostAndCommentsByUserId(userId),
        fallbackUser = fallbackUser,
    )
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
