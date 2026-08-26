package com.efbsm5.easyway.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object AppDatabaseMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `postComment_new` (
                    `index` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `postId` INTEGER NOT NULL,
                    `userId` INTEGER NOT NULL,
                    `content` TEXT NOT NULL,
                    `like` INTEGER NOT NULL,
                    `dislike` INTEGER NOT NULL,
                    `date` TEXT NOT NULL,
                    `likedByMe` INTEGER NOT NULL,
                    `dislikedByMe` INTEGER NOT NULL,
                    FOREIGN KEY(`userId`) REFERENCES `user`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                    FOREIGN KEY(`postId`) REFERENCES `post`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO `postComment_new` (
                    `index`, `postId`, `userId`, `content`, `like`, `dislike`, `date`,
                    `likedByMe`, `dislikedByMe`
                )
                SELECT
                    `index`, `postId`, `userId`, `content`, `like`, `dislike`, `date`,
                    `likedByMe`, `dislikedByMe`
                FROM `postComment`
                """.trimIndent()
            )
            db.execSQL("DROP TABLE `postComment`")
            db.execSQL("ALTER TABLE `postComment_new` RENAME TO `postComment`")
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_postComment_postId` " +
                    "ON `postComment` (`postId`)"
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_postComment_userId` " +
                    "ON `postComment` (`userId`)"
            )
        }
    }
}
