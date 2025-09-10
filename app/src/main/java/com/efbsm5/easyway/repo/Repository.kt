package com.efbsm5.easyway.repo


import com.amap.api.maps.model.LatLng
import com.efbsm5.easyway.SDKUtils
import com.efbsm5.easyway.data.UserManager
import com.efbsm5.easyway.data.database.AppDataBase
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.PointComment
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.PostComment
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.data.models.assistModel.EasyPointSimplify
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.data.models.assistModel.PostCommentAndUser
import com.efbsm5.easyway.getCurrentFormattedTime
import com.efbsm5.easyway.getInitPoint
import com.efbsm5.easyway.getInitUser

object DataRepository {

    private val database = AppDataBase.getDatabase(SDKUtils.getContext())

    private val postCommentDao get() = database.postCommentDao()
    private val postDao get() = database.postDao()
    private val pointDao get() = database.pointsDao()
    private val pointCommentDao get() = database.pointCommentDao()
    private val userDao get() = database.userDao()

    enum class ReactionType { LIKE, DISLIKE }

    enum class TargetType { POST, POINT, POST_COMMENT, POINT_COMMENT }

    fun getAllPoints(): Result<List<EasyPointSimplify>> = runCatching { pointDao.loadAllPoints() }

    fun getPostAndUser(): Result<List<PostAndUser>> = runCatching { postDao.getPostWithUser() }
    fun getPostComments(id: Int): Result<List<PostCommentAndUser>> =
        runCatching { postDao.getPostWithComment(id).comments }

    fun getUserById(userId: Int): Result<User> =
        runCatching { userDao.getUserById(userId) ?: getInitUser() }


//    private fun adjustCounters(
//        targetType: TargetType,
//        targetId: Int,
//        increment: ReactionType? = null,
//        decrement: ReactionType? = null
//    ) {
//        when (targetType) {
//            TargetType.POST -> {
//                if (increment == ReactionType.LIKE) postDao.increaseLike(targetId)
//                if (decrement == ReactionType.LIKE) postDao.decreaseLike(targetId)
//            }
//
//            TargetType.POINT -> {
//                if (increment == ReactionType.LIKE) pointDao.increaseLikes(targetId)
//                if (increment == ReactionType.DISLIKE) pointDao.increaseDislikes(targetId)
//                if (decrement == ReactionType.LIKE) pointDao.decreaseLikes(targetId)
//                if (decrement == ReactionType.DISLIKE) pointDao.decreaseDislikes(targetId)
//            }
//
//            TargetType.POST_COMMENT -> {
//                if (increment == ReactionType.LIKE) postCommentDao.increaseLikes(targetId)
//                if (increment == ReactionType.DISLIKE) postCommentDao.increaseDislikes(targetId)
//                if (decrement == ReactionType.LIKE) postCommentDao.decreaseLikes(targetId)
//                if (decrement == ReactionType.DISLIKE) postCommentDao.decreaseDislikes(targetId)
//            }
//
//            TargetType.POINT_COMMENT -> {
//                if (increment == ReactionType.LIKE) pointCommentDao.increaseLikes(targetId)
//                if (increment == ReactionType.DISLIKE) pointCommentDao.increaseDislikes(targetId)
//                if (decrement == ReactionType.LIKE) pointCommentDao.decreaseLikes(targetId)
//                if (decrement == ReactionType.DISLIKE) pointCommentDao.decreaseDislikes(targetId)
//            }
//        }
//    }

    fun uploadPost(post: Post) {
        val date = getCurrentFormattedTime()
        val entity = Post(
            title = post.title,
            date = date,
            content = post.content,
            lng = post.lng,
            lat = post.lat,
            position = post.position,
            userId = UserManager.userId,
            type = post.type,
            photo = post.photo,
        )
        postDao.insert(entity)
    }

    fun uploadPoint(easyPoint: EasyPoint) {
        val entity = EasyPoint(
            name = easyPoint.name,
            type = easyPoint.type,
            info = easyPoint.info,
            location = easyPoint.location,
            photo = easyPoint.photo,
            refreshTime = getCurrentFormattedTime(),
            lat = easyPoint.lat,
            lng = easyPoint.lng,
            userId = UserManager.userId,
        )
        pointDao.insert(entity)
    }

    fun uploadPostComment(comment: PostComment): Result<Unit> =
        runCatching { postCommentDao.insert(comment) }

    fun uploadPointComment(comment: PointComment): Result<Unit> =
        runCatching { pointCommentDao.insert(comment) }

    fun getPointFromLatLng(latLng: LatLng): Result<EasyPoint> = runCatching {
        pointDao.getPointByLatLng(latLng.latitude, latLng.longitude) ?: getInitPoint(
            latLng
        )
    }

    fun getPointByUserId(userId: Int): Result<List<EasyPoint>> =
        runCatching { pointDao.getPointByUserId(userId) }

    fun getPointByName(string: String): Result<List<EasyPoint>> =
        runCatching { pointDao.searchEasyPointsByName(string) }

    fun addLikeForPost(postId: Int) {
        runCatching { postDao.increaseLike(postId) }
    }

    fun decreaseLikeForPost(postId: Int) {
        postDao.increaseLike(postId)
    }

    fun addLikeForPointComment(commentIndex: Int) {
        pointCommentDao.increaseLikes(commentIndex)
    }

    fun decreaseLikeForPointComment(commentIndex: Int) {
        pointCommentDao.decreaseLikes(commentIndex)
    }

    fun addDisLikeForPointComment(commentIndex: Int) {
        pointCommentDao.increaseDislikes(commentIndex)
    }

    fun decreaseDisLikeForPointComment(commentIndex: Int) {
        pointCommentDao.decreaseDislikes(commentIndex)
    }

    fun addLikeForPostComment(commentIndex: Int) {
        postCommentDao.increaseLikes(commentIndex)
    }

    fun decreaseLikeForPostComment(commentIndex: Int) {
        postCommentDao.decreaseLikes(commentIndex)
    }

    fun addDisLikeForPostComment(commentIndex: Int) {
        postCommentDao.increaseDislikes(commentIndex)
    }

    fun decreaseDisLikeForPostComment(commentIndex: Int) {
        postCommentDao.decreaseDislikes(commentIndex)
    }

    fun addLikeForPoint(pointId: Int) {
        pointDao.increaseLikes(pointId)
    }

    fun decreaseLikeForPoint(pointId: Int) {
        pointDao.decreaseLikes(pointId)
    }

    fun addDisLikeForPoint(pointId: Int) {
        pointDao.increaseDislikes(pointId)
    }

    fun decreaseDisLikeForPoint(pointId: Int) {
        pointDao.decreaseDislikes(pointId)
    }


}

