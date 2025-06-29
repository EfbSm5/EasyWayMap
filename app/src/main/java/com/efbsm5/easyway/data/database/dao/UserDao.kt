package com.efbsm5.easyway.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.efbsm5.easyway.data.models.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: Int): User?

    @Query("SELECT * FROM users")
    fun getAllUsers(): List<User>

    @Query("DELETE FROM users WHERE id = :id")
    fun deleteUserById(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(posts: List<User>)

    @Query("DELETE FROM users WHERE id IN (:ids)")
    fun deleteAll(ids: List<Int>)
}