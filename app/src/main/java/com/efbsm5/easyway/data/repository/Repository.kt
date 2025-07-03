package com.efbsm5.easyway.data.repository


import android.net.Uri
import com.amap.api.maps.model.LatLng
import com.efbsm5.easyway.SDKUtils
import com.efbsm5.easyway.data.UserManager
import com.efbsm5.easyway.data.database.AppDataBase
import com.efbsm5.easyway.data.models.PointComment
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.data.models.assistModel.EasyPointSimplify
import com.efbsm5.easyway.getCurrentFormattedTime
import com.efbsm5.easyway.getInitPoint
import com.efbsm5.easyway.getInitUser
import kotlinx.coroutines.flow.Flow

object DataRepository {
    val context = SDKUtils.getContext()
    private val database = AppDataBase.getDatabase(context)

    fun getAllPoints(): Flow<List<EasyPointSimplify>> {
        return database.pointsDao().loadAllPoints()
    }

    fun getAllDynamicPosts(): Flow<List<Post>> {
        return database.dynamicPostDao().getAllDynamicPosts()
    }

    fun getAllCommentsById(commentId: Int): Flow<List<PointComment>> {
        return database.commentDao().getCommentByCommentId(commentId)
    }

    fun getUserById(userId: Int): User {
        return database.userDao().getUserById(userId) ?: getInitUser()
    }

    fun addLikeForComment(commentIndex: Int) {
        database.commentDao().increaseLikes(commentIndex)
    }

    fun decreaseLikeForComment(commentIndex: Int) {
        database.commentDao().decreaseLikes(commentIndex)
    }

    fun addDisLikeForComment(commentIndex: Int) {
        database.commentDao().increaseDislikes(commentIndex)
    }

    fun decreaseDisLikeForComment(commentIndex: Int) {
        database.commentDao().decreaseDislikes(commentIndex)
    }

    fun addLikeForPoint(pointId: Int) {
        database.pointsDao().increaseLikes(pointId)
    }

    fun decreaseLikeForPoint(pointId: Int) {
        database.pointsDao().decreaseLikes(pointId)
    }

    fun addDisLikeForPoint(pointId: Int) {
        database.pointsDao().increaseDislikes(pointId)
    }

    fun decreaseDisLikeForPoint(pointId: Int) {
        database.pointsDao().decreaseDislikes(pointId)
    }

    fun uploadPost(dynamicPost: Post, photos: List<Uri>) {
        val id = database.dynamicPostDao().getCount() + 1
        val date = getCurrentFormattedTime()
        val commentId = database.commentDao().getMaxCommentId() + 1
        val photo = emptyList<Uri>().toMutableList()
        photos.forEach { uri ->
//            httpClient.uploadImage(
//                context, uri,
//                callback = {
//                    if (it != null) {
//                        photo.add(it)
//                    }
//                },
//            )
        }
        val post = Post(
            id = id,
            title = dynamicPost.title,
            date = date,
            like = 0,
            content = dynamicPost.content,
            lng = dynamicPost.lng,
            lat = dynamicPost.lat,
            position = dynamicPost.position,
            userId = UserManager.userId,
            commentId = commentId,
            type = dynamicPost.type,
            photo = photo,
        )
        database.dynamicPostDao().insert(post)
    }

    fun getPointFromLatlng(latLng: LatLng): EasyPoint {
        return database.pointsDao().getPointByLatLng(latLng.latitude, latLng.longitude)
            ?: getInitPoint(latLng)
    }

    fun uploadComment(pointComment: PointComment) {
        database.commentDao().insert(pointComment)
    }

    fun uploadPoint(easyPoint: EasyPoint) {
        var photoUri: Uri? = null
//        easyPoint.photo?.let { uri ->
//            httpClient.uploadImage(
//                context, uri,
//                callback = {
//                    photoUri = it
//                },
//            )
//        }
        EasyPoint(
            pointId = database.pointsDao().getCount() + 1,
            name = easyPoint.name,
            type = easyPoint.type,
            info = easyPoint.info,
            location = easyPoint.location,
            photo = photoUri,
            refreshTime = getCurrentFormattedTime(),
            likes = 0,
            dislikes = 0,
            lat = easyPoint.lat,
            lng = easyPoint.lng,
            userId = UserManager.userId,
            commentId = database.commentDao().getMaxCommentId() + 1
        ).let {
            database.pointsDao().insert(it)
        }
    }

    fun getCommentCount(commentId: Int): Flow<Int> {
        return database.commentDao().getCountById(commentId)
    }


    fun getPostByUserId(userId: Int): Flow<List<Post>> {
        return database.dynamicPostDao().getAllDynamicPostsByUserId(userId)
    }

    fun getPointByUserId(userId: Int): Flow<List<EasyPoint>> {
        return database.pointsDao().getPointByUserId(userId)
    }

    fun getCommentByUserId(userId: Int): Flow<List<PointComment>> {
        return database.commentDao().getCommentByUserId(userId)
    }

    fun getCommentCount(): Int {
        return database.commentDao().getCount()
    }

    fun getPointByName(string: String): Flow<List<EasyPoint>> {
        return database.pointsDao().searchEasyPointsByName(string)
    }

    fun addLikeForPost(postId: Int) {
        database.dynamicPostDao().increaseLikes(postId)
    }

    fun decreaseLikeForPost(postId: Int) {
        database.dynamicPostDao().decreaseLikes(postId)
    }
}