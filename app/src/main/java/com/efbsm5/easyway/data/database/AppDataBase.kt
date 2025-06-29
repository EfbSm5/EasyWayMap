package com.efbsm5.easyway.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.efbsm5.easyway.data.database.dao.CommentDao
import com.efbsm5.easyway.data.database.dao.DynamicPostDao
import com.efbsm5.easyway.data.database.dao.PointsDao
import com.efbsm5.easyway.data.database.dao.UserDao
import com.efbsm5.easyway.data.models.Comment
import com.efbsm5.easyway.data.models.DynamicPost
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.User

@Database(
    version = 1,
    entities = [EasyPoint::class, User::class, Comment::class, DynamicPost::class],
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun pointsDao(): PointsDao
    abstract fun commentDao(): CommentDao
    abstract fun dynamicPostDao(): DynamicPostDao
    abstract fun userDao(): UserDao

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