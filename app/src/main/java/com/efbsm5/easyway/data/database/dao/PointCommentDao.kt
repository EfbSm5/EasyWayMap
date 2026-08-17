package com.efbsm5.easyway.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.efbsm5.easyway.data.models.PointComment

@Dao
interface PointCommentDao {
    @Query("SELECT * FROM pointComment")
    fun getAll(): List<PointComment>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pointComment: PointComment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(posts: List<PointComment>)

    @Query("DELETE FROM pointComment WHERE `index` IN (:ids)")
    fun deleteAll(ids: List<Int>)

    @Query("UPDATE pointComment SET `like` = `like` + 1 WHERE `index` = :id")
    fun increaseLikes(id: Int)

    @Query("UPDATE pointComment SET `like` = CASE WHEN `like` > 0 THEN `like` - 1 ELSE 0 END WHERE `index` = :id")
    fun decreaseLikes(id: Int)

    @Query("UPDATE pointComment SET dislike = dislike + 1 WHERE `index` = :id")
    fun increaseDislikes(id: Int)

    @Query("UPDATE pointComment SET dislike = CASE WHEN dislike > 0 THEN dislike - 1 ELSE 0 END WHERE `index` = :id")
    fun decreaseDislikes(id: Int)

}
