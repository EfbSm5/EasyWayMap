package com.efbsm5.easyway.repo

import com.efbsm5.easyway.SDKUtils
import com.efbsm5.easyway.data.database.AppDataBase
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import kotlinx.coroutines.flow.Flow

object CommunityRepository {
    private val database = AppDataBase.getDatabase(SDKUtils.getContext())
    private val postDao get() = database.postDao()

    /** Room 是社区列表的唯一真源，写入后由该 Flow 自动回流到界面。 */
    fun observePosts(): Flow<List<PostAndUser>> = postDao.observeAllPosts()

    suspend fun getPost(id: Int): Result<PostAndUser> = runCatching {
        postDao.getPostById(id) ?: error("帖子不存在或已被删除")
    }
}
