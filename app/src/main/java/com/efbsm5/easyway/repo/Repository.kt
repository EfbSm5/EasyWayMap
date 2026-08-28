package com.efbsm5.easyway.repo


import androidx.room.withTransaction
import com.efbsm5.easyway.SDKUtils
import com.efbsm5.easyway.data.UserManager
import com.efbsm5.easyway.data.database.AppDataBase
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.PointComment
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.PostComment
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.data.models.assistModel.EasyPointSimplify
import com.efbsm5.easyway.data.models.assistModel.PointWithComments
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.data.models.assistModel.PostCommentAndUser
import com.efbsm5.easyway.getCurrentFormattedTime
import com.efbsm5.easyway.getInitUser
import kotlinx.coroutines.flow.Flow

object DataRepository {

    private val database = AppDataBase.getDatabase(SDKUtils.getContext())

    private val postCommentDao get() = database.postCommentDao()
    private val postDao get() = database.postDao()
    private val pointDao get() = database.pointsDao()
    private val pointCommentDao get() = database.pointCommentDao()
    private val userDao get() = database.userDao()

    fun getAllPoints(): Result<List<EasyPointSimplify>> = runCatching { pointDao.loadAllPoints() }

    fun observeAllPoints(): Flow<List<EasyPointSimplify>> = pointDao.observeAllPoints()

    fun getPostAndUser(): Result<List<PostAndUser>> = runCatching { postDao.getPostWithUser() }
    suspend fun getPostComments(id: Int): Result<List<PostCommentAndUser>> =
        runCatching {
            postDao.getPostWithComment(id)?.comments ?: error("帖子不存在或已被删除")
        }

    fun getUserById(userId: Int): Result<User> =
        runCatching { userDao.getUserById(userId) ?: getInitUser() }

    /**
     * 将草稿规范化后一次性写入 Room。只有事务成功才返回已生成真实主键的帖子。
     */
    suspend fun uploadPost(post: Post): Result<PostAndUser> = runCatching {
        database.withTransaction {
            val author = ensureLocalUser()
            val entity = Post(
                id = 0,
                title = post.title.trim(),
                date = getCurrentFormattedTime(),
                content = post.content.trim(),
                lng = post.lng,
                lat = post.lat,
                position = post.position,
                userId = author.id,
                type = post.type,
                photo = post.photo,
            )
            val postId = postDao.insert(entity).toInt()
            PostAndUser(post = entity.copy(id = postId), user = author)
        }
    }

    suspend fun uploadPoint(easyPoint: EasyPoint): Result<EasyPoint> = runCatching {
        database.withTransaction {
            val author = ensureLocalUser()
            val entity = EasyPoint(
                pointId = 0,
                name = easyPoint.name,
                type = easyPoint.type,
                info = easyPoint.info,
                location = easyPoint.location,
                photo = easyPoint.photo,
                refreshTime = getCurrentFormattedTime(),
                lat = easyPoint.lat,
                lng = easyPoint.lng,
                userId = author.id,
            )
            val pointId = pointDao.insertAndReturnId(entity).toInt()
            entity.copy(pointId = pointId)
        }
    }

    suspend fun uploadPostComment(comment: PostComment): Result<PostCommentAndUser> = runCatching {
        database.withTransaction {
            val author = ensureLocalUser()
            val entity = comment.copy(index = 0, userId = author.id)
            val commentId = postCommentDao.insert(entity).toInt()
            PostCommentAndUser(
                postComment = entity.copy(index = commentId),
                user = author,
            )
        }
    }

    fun uploadPointComment(comment: PointComment): Result<Unit> =
        runCatching { pointCommentDao.insert(comment) }

    fun getPointFromLatLng(simplify: EasyPointSimplify): Result<EasyPoint> = runCatching {
        pointDao.getPointByLatLng(simplify.lat, simplify.lng)!!
    }

    fun getPointAndCommentByUserId(userId: Int): Result<List<PointWithComments>> =
        runCatching { pointDao.getPointWithCommentsByUserId(userId) }

    fun getPointByName(string: String): Result<List<EasyPoint>> =
        runCatching { pointDao.searchEasyPointsByName(string) }

    fun addLikeForPost(postId: Int) = runCatching { postDao.increaseLike(postId) }

    suspend fun setPostLike(postId: Int, likedByMe: Boolean, likeCount: Int): Result<Unit> =
        runCatching {
            check(postDao.updateLikeState(postId, likeCount, likedByMe) == 1) {
                "帖子不存在或已被删除"
            }
        }

    suspend fun setPostCommentReaction(comment: PostComment): Result<Unit> = runCatching {
        check(
            postCommentDao.updateReactionState(
                id = comment.index,
                likeCount = comment.like,
                dislikeCount = comment.dislike,
                likedByMe = comment.likedByMe,
                dislikedByMe = comment.dislikedByMe,
            ) == 1
        ) { "评论不存在或已被删除" }
    }


    fun getPostAndCommentsByUserId(userId: Int) =
        runCatching { postDao.getPostAndCommentsByUserId(userId) }


    fun decreaseLikeForPost(postId: Int) {
        postDao.decreaseLike(postId)
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

    suspend fun searchForPoint(string: String): Result<List<PostAndUser>> {
        return runCatching { postDao.search(string) }
    }

    /** 保证外键引用的本地用户已存在，并把自动生成的 ID 回写到偏好设置。 */
    private fun ensureLocalUser(): User {
        val preferred = UserManager.getUser()
        if (preferred.id > 0) {
            userDao.getUserById(preferred.id)?.let { return it }
        }

        val normalized = preferred.copy(
            id = preferred.id.coerceAtLeast(0),
            name = preferred.name.ifBlank { "本地用户" },
            avatar = preferred.avatar?.takeIf { it.isNotBlank() },
        )
        val storedId = userDao.insert(normalized).toInt()
        val stored = normalized.copy(id = storedId)
        UserManager.userId = stored.id
        UserManager.name = stored.name
        UserManager.avatar = stored.avatar.orEmpty()
        return stored
    }
}
