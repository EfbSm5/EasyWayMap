package com.efbsm5.easyway.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import com.efbsm5.easyway.data.models.assistModel.PostWithComments

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dynamicPost: Post)

    @Transaction
    @Query("SELECT * FROM post WHERE id = :id")
    fun getPostById(id: Int): PostAndUser

    @Transaction
    @Query("SELECT * FROM post")
    fun getAllPosts(): List<PostAndUser>


    @Query("DELETE FROM post WHERE id = :id")
    fun deletePostById(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(posts: List<Post>)

    @Query("DELETE FROM post WHERE id IN (:ids)")
    fun deleteAll(ids: List<Int>)

    @Query("SELECT COUNT(*) FROM post")
    fun getCount(): Int

    @Transaction
    @Query("SELECT * FROM post")
    fun getPostWithUser(): List<PostAndUser>

    @Transaction
    @Query("SELECT * FROM post WHERE`id` = :id")
    fun getPostWithComment(id: Int): PostWithComments

    @Query("UPDATE post SET `like` = `like` + 1 WHERE `id` = :id")
    fun increaseLike(id: Int)

    @Query("UPDATE post SET `like` = `like` - 1 WHERE id = :id")
    fun decreaseLike(id: Int)

    @Transaction
    @Query("SELECT * FROM post WHERE title LIKE '%' || :keyword || '%'")
    suspend fun search(keyword: String): List<PostAndUser>

    // 新增：获取全部 Post 实体列表用于 diff
    @Query("SELECT * FROM post")
    fun getAllPostEntities(): List<Post>

    @Transaction
    @Query("SELECT * FROM post WHERE userId = :userId")
    fun getPostAndCommentsByUserId(userId: Int): List<PostWithComments>
}