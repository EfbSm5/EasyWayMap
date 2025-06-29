package com.efbsm5.easyway.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.efbsm5.easyway.data.models.Comment
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(comment: Comment)

    @Query("SELECT * FROM comments WHERE commentId = :id")
    fun getCommentByCommentId(id: Int): Flow<List<Comment>>

    @Query("SELECT * FROM comments WHERE userId = :id")
    fun getCommentByUserId(id: Int): Flow<List<Comment>>

    @Query("SELECT * FROM comments")
    fun getAllComments(): List<Comment>

    @Query("DELETE FROM comments WHERE `index` = :index")
    fun deleteCommentByIndex(index: Int)

    @Query("UPDATE comments SET `like` = `like` + 1 WHERE `index` = :id")
    fun increaseLikes(id: Int)

    @Query("UPDATE comments SET `like` = `like` - 1 WHERE `index` = :id")
    fun decreaseLikes(id: Int)

    @Query("UPDATE comments SET dislike = dislike + 1 WHERE `index` = :id")
    fun increaseDislikes(id: Int)

    @Query("UPDATE comments SET dislike = dislike - 1 WHERE `index` = :id")
    fun decreaseDislikes(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(posts: List<Comment>)

    @Query("DELETE FROM comments WHERE commentId IN (:ids)")
    fun deleteAll(ids: List<Int>)

    @Query("SELECT COUNT(*) FROM comments")
    fun getCount(): Int

    @Query("SELECT COUNT(*) FROM comments WHERE commentId = :commentId ")
    fun getCountById(commentId: Int): Flow<Int>

    @Query("SELECT MAX(commentId) FROM comments")
    fun getMaxCommentId(): Int
}