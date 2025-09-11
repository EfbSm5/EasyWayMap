package com.efbsm5.easyway.repo

import com.efbsm5.easyway.SDKUtils
import com.efbsm5.easyway.data.database.AppDataBase
import com.efbsm5.easyway.data.models.assistModel.PostAndUser

object CommunityRepository {
    private val database = AppDataBase.getDatabase(SDKUtils.getContext())
    private val postDao get() = database.postDao()
    fun getAllPosts(): Result<List<PostAndUser>> =
        runCatching { postDao.getAllPosts() }

    fun getPost(id: Int): Result<PostAndUser> =
        runCatching { postDao.getPostById(id) }

}