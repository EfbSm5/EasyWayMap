package com.efbsm5.easyway.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.efbsm5.easyway.data.models.PointComment
import kotlinx.coroutines.flow.Flow

@Dao
interface PointCommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pointComment: PointComment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(posts: List<PointComment>)

    @Query("DELETE FROM pointComment WHERE `index` IN (:ids)")
    fun deleteAll(ids: List<Int>)

    @Query("SELECT COUNT(*) FROM pointComment WHERE `index` = :index ")
    fun getCountById(index: Int): Flow<Int>

    @Query("DELETE FROM pointComment WHERE `index` = :index")
    fun deleteCommentByIndex(index: Int)

    @Query("UPDATE pointComment SET `like` = `like` + 1 WHERE `index` = :id")
    fun increaseLikes(id: Int)

    @Query("UPDATE pointComment SET `like` = `like` - 1 WHERE `index` = :id")
    fun decreaseLikes(id: Int)

    @Query("UPDATE pointComment SET dislike = dislike + 1 WHERE `index` = :id")
    fun increaseDislikes(id: Int)

    @Query("UPDATE pointComment SET dislike = dislike - 1 WHERE `index` = :id")
    fun decreaseDislikes(id: Int)

    // 新增：获取全部 PointComment 实体列表用于 diff
    @Query("SELECT * FROM pointComment")
    fun getAll(): List<PointComment>
}