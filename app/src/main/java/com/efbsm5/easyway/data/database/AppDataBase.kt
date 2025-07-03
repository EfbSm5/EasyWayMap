package com.efbsm5.easyway.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.efbsm5.easyway.data.database.dao.PointCommentDao
import com.efbsm5.easyway.data.database.dao.PointsDao
import com.efbsm5.easyway.data.database.dao.PostCommentDao
import com.efbsm5.easyway.data.database.dao.PostDao
import com.efbsm5.easyway.data.database.dao.UserDao
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.PointComment
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.PostComment
import com.efbsm5.easyway.data.models.User

@Database(
    version = 1,
    entities = [EasyPoint::class, User::class, PointComment::class, Post::class, PostComment::class],
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun pointsDao(): PointsDao
    abstract fun pointCommentDao(): PointCommentDao
    abstract fun dynamicPostDao(): PostDao
    abstract fun userDao(): UserDao
    abstract fun postCommentDao(): PostCommentDao

    companion object {
        private var instance: AppDataBase? = null

        @Synchronized
        fun getDatabase(context: Context): AppDataBase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext, AppDataBase::class.java, "app_database"
            ).build().apply { instance = this }
        }
    }
}