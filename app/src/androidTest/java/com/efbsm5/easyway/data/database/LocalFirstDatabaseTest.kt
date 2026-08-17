package com.efbsm5.easyway.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** 验证本地写入会通过 Room Flow 刷新 UI 数据，并持久化互动状态。 */
@RunWith(AndroidJUnit4::class)
class LocalFirstDatabaseTest {

    private lateinit var database: AppDataBase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDataBase::class.java)
            .allowMainThreadQueries()
            .build()
        database.userDao().insert(User(id = 1, name = "本地用户", avatar = null))
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun postInsertAndReactionAreObservable() = runBlocking {
        val postId = database.postDao().insert(
            Post(
                title = "测试帖子",
                type = 0,
                date = "2026-08-12",
                content = "本地内容",
                lat = 30.0,
                lng = 120.0,
                position = "测试位置",
                userId = 1,
                photo = emptyList(),
            )
        ).toInt()

        val inserted = database.postDao().observeAllPosts().first().single()
        assertEquals(postId, inserted.post.id)
        assertEquals("本地用户", inserted.user.name)

        assertEquals(1, database.postDao().updateLikeState(postId, 1, true))
        val updated = requireNotNull(database.postDao().getPostById(postId)).post
        assertEquals(1, updated.like)
        assertTrue(updated.likedByMe)
    }

    @Test
    fun pointInsertIsObservable() = runBlocking {
        val pointId = database.pointsDao().insertAndReturnId(
            EasyPoint(
                name = "测试点位",
                type = "其他",
                info = "本地备注",
                location = "测试位置",
                photo = null,
                refreshTime = "2026-08-12",
                lat = 30.0,
                lng = 120.0,
                userId = 1,
            )
        ).toInt()

        val points = database.pointsDao().observeAllPoints().first()
        assertFalse(points.isEmpty())
        assertEquals(pointId, points.single().pointId)
        assertEquals("测试点位", points.single().name)
    }
}
