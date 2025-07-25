package com.efbsm5.easyway.data.database.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.efbsm5.easyway.data.models.PostComment
import kotlinx.coroutines.flow.Flow

interface PostCommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(postComment: PostComment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(posts: List<PostComment>)

    @Query("DELETE FROM postComment WHERE `index` IN (:ids)")
    fun deleteAll(ids: List<Int>)

    @Query("SELECT COUNT(*) FROM postComment WHERE `index` = :index ")
    fun getCountById(index: Int): Flow<Int>

    @Query("DELETE FROM postComment WHERE `index` = :index")
    fun deleteCommentByIndex(index: Int)

    @Query("UPDATE postComment SET `like` = `like` + 1 WHERE `index` = :id")
    fun increaseLikes(id: Int)

    @Query("UPDATE postComment SET `like` = `like` - 1 WHERE `index` = :id")
    fun decreaseLikes(id: Int)

    @Query("UPDATE postComment SET dislike = dislike + 1 WHERE `index` = :id")
    fun increaseDislikes(id: Int)

    @Query("UPDATE postComment SET dislike = dislike - 1 WHERE `index` = :id")
    fun decreaseDislikes(id: Int)
}