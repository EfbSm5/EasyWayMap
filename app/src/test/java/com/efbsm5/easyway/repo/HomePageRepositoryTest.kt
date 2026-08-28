package com.efbsm5.easyway.repo

import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.data.models.assistModel.PointWithComments
import com.efbsm5.easyway.data.models.assistModel.PostWithComments
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomePageRepositoryTest {

    @Test
    fun combinedRoomData_waitsForAllSourcesAndRefreshesOnChange() = runTest {
        val users = MutableSharedFlow<User?>(replay = 1)
        val points = MutableSharedFlow<List<PointWithComments>>(replay = 1)
        val posts = MutableSharedFlow<List<PostWithComments>>(replay = 1)
        val fallbackUser = User(id = 0, name = "默认用户", avatar = null)
        val snapshots = mutableListOf<HomePageSnapshot>()

        backgroundScope.launch {
            combineHomePageData(users, points, posts, fallbackUser).collect(snapshots::add)
        }
        runCurrent()

        users.emit(null)
        points.emit(listOf(pointWithComments(id = 1)))
        runCurrent()
        assertTrue(snapshots.isEmpty())

        posts.emit(listOf(postWithComments(id = 2)))
        runCurrent()
        assertEquals(1, snapshots.size)
        assertEquals(fallbackUser, snapshots.single().user)
        assertEquals(listOf(1), snapshots.single().points.map { it.point.pointId })
        assertEquals(listOf(2), snapshots.single().posts.map { it.post.id })

        points.emit(listOf(pointWithComments(id = 3)))
        runCurrent()
        assertEquals(2, snapshots.size)
        assertEquals(listOf(3), snapshots.last().points.map { it.point.pointId })
    }

    private fun pointWithComments(id: Int) = PointWithComments(
        point = EasyPoint(
            pointId = id,
            name = "点位$id",
            type = "其他",
            info = "说明",
            location = "位置",
            photo = null,
            refreshTime = "2026-08-27",
            lat = 30.0,
            lng = 120.0,
            userId = 1,
        ),
        comments = emptyList(),
    )

    private fun postWithComments(id: Int) = PostWithComments(
        post = Post(
            id = id,
            title = "帖子$id",
            type = 0,
            date = "2026-08-27",
            content = "内容",
            lat = 30.0,
            lng = 120.0,
            position = "位置",
            userId = 1,
            photo = emptyList(),
        ),
        comments = emptyList(),
    )
}
