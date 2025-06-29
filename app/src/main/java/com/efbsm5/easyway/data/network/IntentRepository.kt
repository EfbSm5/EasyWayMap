package com.efbsm5.easyway.data.network

import com.efbsm5.easyway.SDKUtils
import com.efbsm5.easyway.data.database.AppDataBase
import com.efbsm5.easyway.data.models.Comment
import com.efbsm5.easyway.data.models.DynamicPost
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.ModelNames
import com.efbsm5.easyway.data.models.User

object IntentRepository {
    private val db = AppDataBase.getDatabase(SDKUtils.getContext())
    private val commentDao = db.commentDao()
    private val userDao = db.userDao()
    private val dynamicPostDao = db.dynamicPostDao()
    private val pointsDao = db.pointsDao()

    suspend fun syncData() {
        syncUsers()
        syncComments()
        syncDynamicPosts()
        syncEasyPoints()
    }

    private suspend fun syncComments() {
        val localComments = commentDao.getAllComments()
        val networkComments = EasyPointNetWork.sendRequest<Comment>(ModelNames.Comments)
        val toInsert = networkComments.filter { it !in localComments }
        localComments.filter { it !in networkComments }.map { it.commentId }
        db.runInTransaction {
//                    commentDao.deleteAll(toDelete)
            commentDao.insertAll(toInsert)
        }

    }

    private suspend fun syncUsers() {
        val localUsers = userDao.getAllUsers()
        val networkUsers = EasyPointNetWork.sendRequest<User>(ModelNames.Users)
        val toInsert = networkUsers.filter { it !in localUsers }
        localUsers.filter { it !in networkUsers }.map { it.id }
        db.runInTransaction {
//                    userDao.deleteAll(toDelete)
            userDao.insertAll(toInsert)
        }
    }

    private suspend fun syncDynamicPosts() {
        val networkPosts = EasyPointNetWork.sendRequest<DynamicPost>(ModelNames.DynamicPosts)
        val localPosts = dynamicPostDao.getAllDynamicPostsByOnce()
        val toInsert = networkPosts.filter { it !in localPosts }
        localPosts.filter { it !in networkPosts }.map { it.id }
        db.runInTransaction {
//                    dynamicPostDao.deleteAll(toDelete)
            dynamicPostDao.insertAll(toInsert)
        }
    }

    private suspend fun syncEasyPoints() {
        val networkPoints = EasyPointNetWork.sendRequest<EasyPoint>(ModelNames.EasyPoints)
        val localPoints = pointsDao.loadAllPointsByOnce()
        val toInsert =
            networkPoints.filter { networkPoint -> localPoints.none { it.pointId == networkPoint.pointId } }
        localPoints.filter { localPoint -> networkPoints.none { it.pointId == localPoint.pointId } }
            .map { it.pointId }
        db.runInTransaction {
//                    pointsDao.deleteAll(toDelete)
            pointsDao.insertAll(toInsert)
        }
    }


}