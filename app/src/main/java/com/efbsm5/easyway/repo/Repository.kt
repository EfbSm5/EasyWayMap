package com.efbsm5.easyway.repo


import android.net.Uri
import com.amap.api.maps.model.LatLng
import com.efbsm5.easyway.SDKUtils
import com.efbsm5.easyway.data.UserManager
import com.efbsm5.easyway.data.database.AppDataBase
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.PostComment
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.data.models.assistModel.EasyPointSimplify
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.data.models.assistModel.PostCommentAndUser
import com.efbsm5.easyway.getCurrentFormattedTime
import com.efbsm5.easyway.getInitPoint
import com.efbsm5.easyway.getInitUser
import kotlinx.coroutines.flow.Flow

object DataRepository {
    private val database = AppDataBase.getDatabase(SDKUtils.getContext())

    fun getAllPoints(): Flow<List<EasyPointSimplify>> {
        return database.pointsDao().loadAllPoints()
    }

    suspend fun getAllPosts(): List<Post> {
        return database.postDao().getAllPosts()
    }

    suspend fun getPostAndUser(): List<PostAndUser> {
        return database.postDao().getPostWithUser()
    }

    suspend fun getPostAndComments(id: Int): List<PostCommentAndUser> {
        return database.postDao().getPostWithComment(id).comments
    }

    suspend fun getUserById(userId: Int): User {
        return database.userDao().getUserById(userId) ?: getInitUser()
    }

    suspend fun addLikeForPointComment(commentIndex: Int) {
        database.pointCommentDao().increaseLikes(commentIndex)
    }

    suspend fun decreaseLikeForPointComment(commentIndex: Int) {
        database.pointCommentDao().decreaseLikes(commentIndex)
    }

    suspend fun addDisLikeForPointComment(commentIndex: Int) {
        database.pointCommentDao().increaseDislikes(commentIndex)
    }

    suspend fun decreaseDisLikeForPointComment(commentIndex: Int) {
        database.pointCommentDao().decreaseDislikes(commentIndex)
    }

    suspend fun addLikeForPostComment(commentIndex: Int) {
        database.postCommentDao().increaseLikes(commentIndex)
    }

    suspend fun decreaseLikeForPostComment(commentIndex: Int) {
        database.postCommentDao().decreaseLikes(commentIndex)
    }

    suspend fun addDisLikeForPostComment(commentIndex: Int) {
        database.postCommentDao().increaseDislikes(commentIndex)
    }

    suspend fun decreaseDisLikeForPostComment(commentIndex: Int) {
        database.postCommentDao().decreaseDislikes(commentIndex)
    }

    suspend fun addLikeForPoint(pointId: Int) {
        database.pointsDao().increaseLikes(pointId)
    }

    suspend fun decreaseLikeForPoint(pointId: Int) {
        database.pointsDao().decreaseLikes(pointId)
    }

    suspend fun addDisLikeForPoint(pointId: Int) {
        database.pointsDao().increaseDislikes(pointId)
    }

    suspend fun decreaseDisLikeForPoint(pointId: Int) {
        database.pointsDao().decreaseDislikes(pointId)
    }

    suspend fun uploadPost(post: Post, photos: List<Uri>) {
        val id = database.postDao().getCount() + 1
        val date = getCurrentFormattedTime()
        val photo = emptyList<String>().toMutableList()
//        photos.forEach { uri ->
//            httpClient.uploadImage(
//                context, uri,
//                callback = {
//                    if (it != null) {
//                        photo.add(it)
//                    }
//                },
//            )
//        }
        val post = Post(
            id = id,
            title = post.title,
            date = date,
            like = 0,
            content = post.content,
            lng = post.lng,
            lat = post.lat,
            position = post.position,
            userId = UserManager.userId,
            type = post.type,
            photo = photo,
        )
        database.postDao().insert(post)
    }


    suspend fun uploadPoint(easyPoint: EasyPoint) {
        var photoUri: String = ""
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
        ).let {
            database.pointsDao().insert(it)
        }
    }

    suspend fun uploadPostComment(comment: PostComment) {
        database.postCommentDao().insert(comment)
    }

    suspend fun getPointFromLatLng(latLng: LatLng): EasyPoint {
        return database.pointsDao().getPointByLatLng(latLng.latitude, latLng.longitude)
            ?: getInitPoint(latLng)
    }

    suspend fun getPointByUserId(userId: Int): Flow<List<EasyPoint>> {
        return database.pointsDao().getPointByUserId(userId)
    }

    suspend fun getPointByName(string: String): Flow<List<EasyPoint>> {
        return database.pointsDao().searchEasyPointsByName(string)
    }

    suspend fun addLikeForPost(postId: Int) {
        database.postDao().increaseLike(postId)
    }

    suspend fun decreaseLikeForPost(postId: Int) {
        database.postDao().decreaseLike(postId)
    }

}