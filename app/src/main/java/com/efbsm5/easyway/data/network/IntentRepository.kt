package com.efbsm5.easyway.data.network

import com.efbsm5.easyway.SDKUtils
import com.efbsm5.easyway.data.database.AppDataBase

object IntentRepository {
    private val db = AppDataBase.getDatabase(SDKUtils.getContext())
    private val postCommentDao = db.postCommentDao()
    private val pointCommentDao = db.pointCommentDao()
    private val userDao = db.userDao()
    private val postDao = db.postDao()
    private val pointsDao = db.pointsDao()

    suspend fun syncData() {
        syncUsers()
        syncComments()
//        syncDynamicPosts()
        syncEasyPoints()
    }

    private suspend fun syncComments() {
//        val localComments = postCommentDao.()
//        val networkPointComments = EasyPointNetWork.sendRequest<PointComment>(ModelNames.Comments)
//        val toInsert = networkPointComments.filter { it !in localComments }
//        localComments.filter { it !in networkPointComments }.map { it.commentId }
//        db.runInTransaction {
////                    commentDao.deleteAll(toDelete)
//            commentDao.insertAll(toInsert)
//        }

    }

    private suspend fun syncUsers() {
//        val localUsers = userDao.getAllUsers()
//        val networkUsers = EasyPointNetWork.sendRequest<User>(ModelNames.Users)
//        val toInsert = networkUsers.filter { it !in localUsers }
//        localUsers.filter { it !in networkUsers }.map { it.id }
//        db.runInTransaction {
////                    userDao.deleteAll(toDelete)
//            userDao.insertAll(toInsert)
//        }
    }

//    private suspend fun syncDynamicPosts() {
//        val networkPosts = EasyPointNetWork.sendRequest<Post>(ModelNames.DynamicPosts)
//        val localPosts = ostDao.getAllDynamicPostsByOnce()
//        val toInsert = networkPosts.filter { it !in localPosts }
//        localPosts.filter { it !in networkPosts }.map { it.id }
//        db.runInTransaction {
////                    dynamicPostDao.deleteAll(toDelete)
//            ostDao.insertAll(toInsert)
//        }
//    }

    private suspend fun syncEasyPoints() {
//        val networkPoints = EasyPointNetWork.sendRequest<EasyPoint>(ModelNames.EasyPoints)
//        val localPoints = pointsDao.loadAllPointsByOnce()
//        val toInsert =
//            networkPoints.filter { networkPoint -> localPoints.none { it.pointId == networkPoint.pointId } }
//        localPoints.filter { localPoint -> networkPoints.none { it.pointId == localPoint.pointId } }
//            .map { it.pointId }
//        db.runInTransaction {
//                    pointsDao.deleteAll(toDelete)
//            pointsDao.insertAll(toInsert)
//        }
    }


}