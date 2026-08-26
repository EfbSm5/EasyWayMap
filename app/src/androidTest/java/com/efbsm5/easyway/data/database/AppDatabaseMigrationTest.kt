package com.efbsm5.easyway.data.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {

    @get:Rule
    val migrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDataBase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory(),
    )

    @After
    fun tearDown() {
        InstrumentationRegistry.getInstrumentation().targetContext.deleteDatabase(TEST_DATABASE)
    }

    @Test
    fun migrate1To2_fromRecentV1_preservesCommentsAndEnablesGeneratedIds() {
        migrationTestHelper.createDatabase(TEST_DATABASE, 1).apply {
            execSQL("INSERT INTO `user` (`id`, `name`, `avatar`) VALUES (1, '迁移用户', NULL)")
            execSQL(
                """
                INSERT INTO `post` (
                    `id`, `title`, `type`, `date`, `like`, `content`, `lat`, `lng`,
                    `position`, `userId`, `photo`, `likedByMe`
                ) VALUES (
                    3, '迁移帖子', 1, '2026-08-26', 5, '旧数据', 30.0, 120.0,
                    '旧位置', 1, '[]', 1
                )
                """.trimIndent()
            )
            execSQL(
                """
                INSERT INTO `postComment` (
                    `index`, `postId`, `userId`, `content`, `like`, `dislike`, `date`,
                    `likedByMe`, `dislikedByMe`
                ) VALUES
                    (4, 3, 1, '第一条', 2, 0, '2026-08-25', 1, 0),
                    (9, 3, 1, '第二条', 0, 3, '2026-08-26', 0, 1)
                """.trimIndent()
            )
            close()
        }

        val migratedDatabase = migrationTestHelper.runMigrationsAndValidate(
            TEST_DATABASE,
            2,
            true,
            AppDatabaseMigrations.MIGRATION_1_2,
        )

        migratedDatabase.query(
            """
            SELECT `index`, `postId`, `userId`, `content`, `like`, `dislike`, `date`,
                   `likedByMe`, `dislikedByMe`
            FROM `postComment`
            ORDER BY `index`
            """.trimIndent()
        ).use { cursor ->
            assertEquals(2, cursor.count)

            assertTrue(cursor.moveToFirst())
            assertEquals(4, cursor.getInt(cursor.getColumnIndexOrThrow("index")))
            assertEquals(3, cursor.getInt(cursor.getColumnIndexOrThrow("postId")))
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("userId")))
            assertEquals("第一条", cursor.getString(cursor.getColumnIndexOrThrow("content")))
            assertEquals(2, cursor.getInt(cursor.getColumnIndexOrThrow("like")))
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("dislike")))
            assertEquals("2026-08-25", cursor.getString(cursor.getColumnIndexOrThrow("date")))
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("likedByMe")))
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("dislikedByMe")))

            assertTrue(cursor.moveToNext())
            assertEquals(9, cursor.getInt(cursor.getColumnIndexOrThrow("index")))
            assertEquals(3, cursor.getInt(cursor.getColumnIndexOrThrow("postId")))
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("userId")))
            assertEquals("第二条", cursor.getString(cursor.getColumnIndexOrThrow("content")))
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("like")))
            assertEquals(3, cursor.getInt(cursor.getColumnIndexOrThrow("dislike")))
            assertEquals("2026-08-26", cursor.getString(cursor.getColumnIndexOrThrow("date")))
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("likedByMe")))
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("dislikedByMe")))
        }

        migratedDatabase.execSQL(
            """
            INSERT INTO `postComment` (
                `postId`, `userId`, `content`, `like`, `dislike`, `date`,
                `likedByMe`, `dislikedByMe`
            ) VALUES (3, 1, '迁移后评论', 0, 0, '2026-08-26', 0, 0)
            """.trimIndent()
        )
        migratedDatabase.query("SELECT MAX(`index`) FROM `postComment`").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertTrue(cursor.getInt(0) > 9)
        }
        migratedDatabase.close()
    }

    @Test
    fun migrate1To2_fromGeneratedIdV1_preservesCommentsAndSequence() {
        migrationTestHelper.createDatabase(TEST_DATABASE, 2).apply {
            execSQL("INSERT INTO `user` (`id`, `name`, `avatar`) VALUES (1, '本地用户', NULL)")
            execSQL(
                """
                INSERT INTO `post` (
                    `id`, `title`, `type`, `date`, `like`, `content`, `lat`, `lng`,
                    `position`, `userId`, `photo`, `likedByMe`
                ) VALUES (
                    5, '本地帖子', 0, '2026-08-26', 0, '本地数据', 30.0, 120.0,
                    '本地位置', 1, '[]', 0
                )
                """.trimIndent()
            )
            execSQL(
                """
                INSERT INTO `postComment` (
                    `index`, `postId`, `userId`, `content`, `like`, `dislike`, `date`,
                    `likedByMe`, `dislikedByMe`
                ) VALUES (21, 5, 1, '已有评论', 4, 1, '2026-08-26', 1, 0)
                """.trimIndent()
            )
            execSQL("PRAGMA user_version = 1")
            close()
        }

        val migratedDatabase = migrationTestHelper.runMigrationsAndValidate(
            TEST_DATABASE,
            2,
            true,
            AppDatabaseMigrations.MIGRATION_1_2,
        )

        migratedDatabase.query(
            "SELECT `index`, `content`, `like`, `dislike` FROM `postComment`"
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(21, cursor.getInt(cursor.getColumnIndexOrThrow("index")))
            assertEquals("已有评论", cursor.getString(cursor.getColumnIndexOrThrow("content")))
            assertEquals(4, cursor.getInt(cursor.getColumnIndexOrThrow("like")))
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("dislike")))
        }

        migratedDatabase.execSQL(
            """
            INSERT INTO `postComment` (
                `postId`, `userId`, `content`, `like`, `dislike`, `date`,
                `likedByMe`, `dislikedByMe`
            ) VALUES (5, 1, '新评论', 0, 0, '2026-08-26', 0, 0)
            """.trimIndent()
        )
        migratedDatabase.query("SELECT MAX(`index`) FROM `postComment`").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertTrue(cursor.getInt(0) > 21)
        }
        migratedDatabase.close()
    }

    private companion object {
        const val TEST_DATABASE = "app-database-migration-test"
    }
}
