package com.efbsm5.easyway.viewmodel

import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.data.models.assistModel.PointWithComments
import com.efbsm5.easyway.data.models.assistModel.PostWithComments
import com.efbsm5.easyway.repo.HomePageRepository
import com.efbsm5.easyway.repo.HomePageSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomePageViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun snapshot_stopsLoadingAndSubsequentChangesRefreshState() = runTest {
        val updates = MutableSharedFlow<HomePageSnapshot>(replay = 1)
        val repository = repository(flowOf(7)) { requestedUserId ->
            assertEquals(7, requestedUserId)
            updates
        }
        val viewModel = HomePageViewModel(repository)
        runCurrent()

        assertTrue(viewModel.uiState.value.isLoading)

        updates.emit(snapshot(userId = 7, pointId = 1, postId = 2))
        runCurrent()
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(7, viewModel.uiState.value.user.id)
        assertEquals(listOf(1), viewModel.uiState.value.points.map { it.point.pointId })
        assertEquals(listOf(2), viewModel.uiState.value.post.map { it.post.id })
        assertEquals(null, viewModel.uiState.value.error)

        updates.emit(snapshot(userId = 7, pointId = 3, postId = 4))
        runCurrent()
        assertEquals(listOf(3), viewModel.uiState.value.points.map { it.point.pointId })
        assertEquals(listOf(4), viewModel.uiState.value.post.map { it.post.id })
    }

    @Test
    fun upstreamError_preservesLastSnapshotAndExposesStableError() = runTest {
        val initialSnapshot = snapshot(userId = 8, pointId = 5, postId = 6)
        val repository = repository(flowOf(8)) {
            flow {
                emit(initialSnapshot)
                throw IllegalStateException("数据库读取失败")
            }
        }
        val viewModel = HomePageViewModel(repository)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(initialSnapshot.user, state.user)
        assertEquals(initialSnapshot.points, state.points)
        assertEquals(initialSnapshot.posts, state.post)
        assertEquals("数据库读取失败", state.error)
    }

    @Test
    fun userIdChange_switchesSourceAndKeepsOldSnapshotUntilReplacement() = runTest {
        val userIds = MutableSharedFlow<Int>(replay = 1)
        val userZeroUpdates = MutableSharedFlow<HomePageSnapshot>(replay = 1)
        val realUserUpdates = MutableSharedFlow<HomePageSnapshot>(replay = 1)
        val observedUserIds = mutableListOf<Int>()
        val repository = repository(userIds) { userId ->
            observedUserIds += userId
            when (userId) {
                0 -> userZeroUpdates
                9 -> realUserUpdates
                else -> error("未配置用户 $userId")
            }
        }
        val viewModel = HomePageViewModel(repository)
        runCurrent()

        userIds.emit(0)
        userZeroUpdates.emit(snapshot(userId = 0, pointId = 1, postId = 2))
        runCurrent()
        assertEquals(0, viewModel.uiState.value.user.id)
        assertFalse(viewModel.uiState.value.isLoading)

        userIds.emit(0)
        runCurrent()
        assertEquals(listOf(0), observedUserIds)

        userIds.emit(9)
        runCurrent()
        assertTrue(viewModel.uiState.value.isLoading)
        assertEquals(0, viewModel.uiState.value.user.id)
        assertEquals(null, viewModel.uiState.value.error)

        realUserUpdates.emit(snapshot(userId = 9, pointId = 3, postId = 4))
        runCurrent()
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(9, viewModel.uiState.value.user.id)
        assertEquals(listOf(3), viewModel.uiState.value.points.map { it.point.pointId })
        assertEquals(listOf(4), viewModel.uiState.value.post.map { it.post.id })
        assertEquals(listOf(0, 9), observedUserIds)
    }

    private fun repository(
        userIds: Flow<Int>,
        observe: (Int) -> Flow<HomePageSnapshot>,
    ): HomePageRepository = object : HomePageRepository {
        override val userIds: Flow<Int> = userIds

        override fun observeHomePage(userId: Int): Flow<HomePageSnapshot> = observe(userId)
    }

    private fun snapshot(userId: Int, pointId: Int, postId: Int) = HomePageSnapshot(
        user = User(id = userId, name = "用户$userId", avatar = null),
        points = listOf(pointWithComments(pointId, userId)),
        posts = listOf(postWithComments(postId, userId)),
    )

    private fun pointWithComments(id: Int, userId: Int) = PointWithComments(
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
            userId = userId,
        ),
        comments = emptyList(),
    )

    private fun postWithComments(id: Int, userId: Int) = PostWithComments(
        post = Post(
            id = id,
            title = "帖子$id",
            type = 0,
            date = "2026-08-27",
            content = "内容",
            lat = 30.0,
            lng = 120.0,
            position = "位置",
            userId = userId,
            photo = emptyList(),
        ),
        comments = emptyList(),
    )
}
