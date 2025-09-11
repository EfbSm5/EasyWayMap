package com.efbsm5.easyway.data.network

import com.efbsm5.easyway.SDKUtils
import com.efbsm5.easyway.data.database.AppDataBase
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.ModelNames
import com.efbsm5.easyway.data.models.PointComment
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.PostComment
import com.efbsm5.easyway.data.models.User

object IntentRepository {
    private val db = AppDataBase.getDatabase(SDKUtils.getContext())
    private val postCommentDao = db.postCommentDao()
    private val pointCommentDao = db.pointCommentDao()
    private val userDao = db.userDao()
    private val postDao = db.postDao()
    private val pointsDao = db.pointsDao()

    suspend fun syncData() {
        // 顺序很重要：先用户 -> 帖子/地点 -> 评论（避免外键约束失败）
        syncUsers()
        syncPosts()
        syncEasyPoints()
        syncComments()
    }

    private suspend fun syncComments() {
        // 同步点评论
        val networkPointPointComment =
            EasyPointNetWork.sendRequest<PointComment>(ModelNames.PointComment)
        // 同步帖评论
        val networkPostComments = EasyPointNetWork.sendRequest<PostComment>(ModelNames.PostComments)

        val localPointComments = pointCommentDao.getAll()
        val localPostComments = postCommentDao.getAll()

        val localPointMap = localPointComments.associateBy { it.index }
        val localPostMap = localPostComments.associateBy { it.index }

        val pcNetworkIds = networkPointPointComment.map { it.index }.toSet()
        val pcLocalIds = localPointComments.map { it.index }.toSet()
        val pcToDelete = (pcLocalIds - pcNetworkIds).toList()
        val pcChanged = networkPointPointComment.filter { localPointMap[it.index] != it }

        val postcNetworkIds = networkPostComments.map { it.index }.toSet()
        val postcLocalIds = localPostComments.map { it.index }.toSet()
        val postcToDelete = (postcLocalIds - postcNetworkIds).toList()
        val postcChanged = networkPostComments.filter { localPostMap[it.index] != it }

        db.runInTransaction {
            if (pcToDelete.isNotEmpty()) pointCommentDao.deleteAll(pcToDelete)
            if (pcChanged.isNotEmpty()) pointCommentDao.insertAll(pcChanged)

            if (postcToDelete.isNotEmpty()) postCommentDao.deleteAll(postcToDelete)
            if (postcChanged.isNotEmpty()) postCommentDao.insertAll(postcChanged)
        }
    }

    private suspend fun syncUsers() {
        val localUsers = userDao.getAllUsers()
        val networkUsers = EasyPointNetWork.sendRequest<User>(ModelNames.Users)
        val localMap = localUsers.associateBy { it.id }
        val localIds = localMap.keys
        val networkIds = networkUsers.map { it.id }.toSet()
        val toDelete = (localIds - networkIds).toList()
        // 仅保留新增或内容发生变化的用户
        val changed = networkUsers.filter { localMap[it.id] != it }
        db.runInTransaction {
            if (toDelete.isNotEmpty()) userDao.deleteAll(toDelete)
            if (changed.isNotEmpty()) userDao.insertAll(changed)
        }
    }

    private suspend fun syncPosts() {
        val networkPosts = EasyPointNetWork.sendRequest<Post>(ModelNames.DynamicPosts)
        val localPosts = postDao.getAllPostEntities()
        val localMap = localPosts.associateBy { it.id }
        val localIds = localMap.keys
        val networkIds = networkPosts.map { it.id }.toSet()
        val toDelete = (localIds - networkIds).toList()
        // 尽量保留本地 likedByMe 状态
        val changed = networkPosts.map { net ->
            val local = localMap[net.id]
            if (local != null) net.copy(likedByMe = local.likedByMe) else net
        }.filter { post -> localMap[post.id] != post }
        db.runInTransaction {
            if (toDelete.isNotEmpty()) postDao.deleteAll(toDelete)
            if (changed.isNotEmpty()) postDao.insertAll(changed)
        }
    }

    private suspend fun syncEasyPoints() {
        val networkPoints = EasyPointNetWork.sendRequest<EasyPoint>(ModelNames.EasyPoints)
        val localPoints = pointsDao.getAllPointEntities()
        val localMap = localPoints.associateBy { it.pointId }
        val localIds = localMap.keys
        val networkIds = networkPoints.map { it.pointId }.toSet()
        val toInsertOrUpdate = networkPoints.filter { localMap[it.pointId] != it }
        val toDelete = (localIds - networkIds).toList()
        db.runInTransaction {
            if (toDelete.isNotEmpty()) pointsDao.deleteAll(toDelete)
            if (toInsertOrUpdate.isNotEmpty()) pointsDao.insertAll(toInsertOrUpdate)
        }
    }

}