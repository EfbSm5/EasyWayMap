package com.efbsm5.easyway.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.assistModel.EasyPointSimplify
import com.efbsm5.easyway.data.models.assistModel.PointWithComments

@Dao
interface PointsDao {
    @Query("SELECT * FROM point")
    fun getAllPointEntities(): List<EasyPoint>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(point: EasyPoint)

    @Query("select pointId,name,lat,lng from point order by pointId desc ")
    fun loadAllPoints(): List<EasyPointSimplify>

    @Query("SELECT * FROM point WHERE pointId = :id")
    fun getPointById(id: Int): EasyPoint?

    @Query("UPDATE point SET `like` = `like` + 1 WHERE pointId = :id ")
    fun increaseLikes(id: Int)

    @Query("UPDATE point SET `like` = `like` -1 WHERE pointId = :id")
    fun decreaseLikes(id: Int)

    @Query("UPDATE point SET dislike = dislike + 1 WHERE pointId = :id")
    fun increaseDislikes(id: Int)

    @Query("UPDATE point SET dislike = dislike -1 WHERE pointId = :id")
    fun decreaseDislikes(id: Int)

    @Query("SELECT * FROM point WHERE lat = :lat AND lng = :lng")
    fun getPointByLatLng(lat: Double, lng: Double): EasyPoint?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(posts: List<EasyPoint>)

    @Query("DELETE FROM point WHERE pointId IN (:ids)")
    fun deleteAll(ids: List<Int>)

    @Transaction
    @Query("SELECT * FROM point WHERE userId=:userId")
    fun getPointWithCommentsByUserId(userId: Int): List<PointWithComments>

    @Query("SELECT * FROM point WHERE name LIKE '%' || :searchString || '%'")
    fun searchEasyPointsByName(searchString: String): List<EasyPoint>


}