package com.efbsm5.easyway.data.dev

import com.efbsm5.easyway.SDKUtils
import com.efbsm5.easyway.data.database.AppDataBase
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.PointComment
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.PostComment
import com.efbsm5.easyway.data.models.User

/**
 * 简单的开发环境种子数据填充器。
 * 删除现有数据后，按外键顺序插入：Users -> Posts/EasyPoints -> Comments。
 */
object DevSeeder {
    fun seed() {
        val db = AppDataBase.getDatabase(SDKUtils.getContext())
        val userDao = db.userDao()
        val postDao = db.postDao()
        val pointsDao = db.pointsDao()
        val postCommentDao = db.postCommentDao()
        val pointCommentDao = db.pointCommentDao()

        db.runInTransaction {
            // 1) 先清空子表，再清空父表
            run {
                val allPostComments = postCommentDao.getAll()
                if (allPostComments.isNotEmpty()) {
                    postCommentDao.deleteAll(allPostComments.map { it.index })
                }
                val allPointComments = pointCommentDao.getAll()
                if (allPointComments.isNotEmpty()) {
                    pointCommentDao.deleteAll(allPointComments.map { it.index })
                }
                val allPosts = postDao.getAllPostEntities()
                if (allPosts.isNotEmpty()) {
                    postDao.deleteAll(allPosts.map { it.id })
                }
                val allPoints = pointsDao.getAllPointEntities()
                if (allPoints.isNotEmpty()) {
                    pointsDao.deleteAll(allPoints.map { it.pointId })
                }
                val allUsers = userDao.getAllUsers()
                if (allUsers.isNotEmpty()) {
                    userDao.deleteAll(allUsers.map { it.id })
                }
            }

            // 2) 插入 Users
            val users = listOf(
                User(
                    id = 0,
                    name = "Test",
                    avatar = "https://bkimg.cdn.bcebos.com/pic/5882b2b7d0a20cf43289a8ce7d094b36acaf9981?x-bce-process=image/format,f_auto/watermark,image_d2F0ZXIvYmFpa2UyNzI,g_7,xp_5,yp_5,P_20/resize,m_lfit,limit_1,h_1080"
                ),
                User(
                    id = 1,
                    name = "Cindy",
                    avatar = "https://bkimg.cdn.bcebos.com/pic/5882b2b7d0a20cf43289a8ce7d094b36acaf9981?x-bce-process=image/format,f_auto/watermark,image_d2F0ZXIvYmFpa2UyNzI,g_7,xp_5,yp_5,P_20/resize,m_lfit,limit_1,h_1080"
                ),
                User(
                    id = 2, name = "Bob", avatar = null
                ),
                User(id = 3, name = "Carol", avatar = null),
            )
            userDao.insertAll(users)

            // 3) 插入 Posts（引用 userId）
            val posts = listOf(
                Post(
                    id = 101,
                    title = "欢迎来到 EasyWay 社区",
                    type = 0,
                    date = "2025-09-11 10:00:00",
                    like = 3,
                    content = "第一条测试帖子，包含图片与定位。",
                    lat = 30.57,
                    lng = 114.30,
                    position = "武汉·洪山区",
                    userId = 1,
                    photo = listOf(
                        "https://bkimg.cdn.bcebos.com/pic/5882b2b7d0a20cf43289a8ce7d094b36acaf9981?x-bce-process=image/format,f_auto/watermark,image_d2F0ZXIvYmFpa2UyNzI,g_7,xp_5,yp_5,P_20/resize,m_lfit,limit_1,h_1080",
                        "https://bkimg.cdn.bcebos.com/pic/5882b2b7d0a20cf43289a8ce7d094b36acaf9981?x-bce-process=image/format,f_auto/watermark,image_d2F0ZXIvYmFpa2UyNzI,g_7,xp_5,yp_5,P_20/resize,m_lfit,limit_1,h_1080"
                    ),
                    likedByMe = false
                ),
                Post(
                    id = 102,
                    title = "出行建议求助",
                    type = 1,
                    date = "2025-09-11 10:10:00",
                    like = 1,
                    content = "从光谷到汉口的最佳路线？",
                    lat = 30.52,
                    lng = 114.32,
                    position = "武汉·东湖高新区",
                    userId = 2,
                    photo = emptyList(),
                    likedByMe = false
                )
            )
            postDao.insertAll(posts)

            // 4) 插入 EasyPoints（引用 userId）
            val points = listOf(
                EasyPoint(
                    pointId = 201,
                    name = "葡萄园咖啡",
                    type = "Cafe",
                    info = "安静的角落，适合学习和办公。",
                    location = "武汉·武昌区",
                    photo = null,
                    refreshTime = "2025-09-11 09:50:00",
                    likes = 5,
                    dislikes = 0,
                    lat = 30.55,
                    lng = 114.31,
                    userId = 1
                ),
                EasyPoint(
                    pointId = 202,
                    name = "江滩公园入口",
                    type = "Park",
                    info = "风景优美，适合散步。",
                    location = "武汉·江岸区",
                    photo = null,
                    refreshTime = "2025-09-11 09:55:00",
                    likes = 2,
                    dislikes = 0,
                    lat = 30.60,
                    lng = 114.29,
                    userId = 3
                )
            )
            pointsDao.insertAll(points)

            // 5) 插入 PostComments（引用 postId/userId）
            val postComments = listOf(
                PostComment(
                    index = 1001,
                    postId = 101,
                    userId = 2,
                    content = "欢迎发帖！",
                    like = 1,
                    dislike = 0,
                    date = "2025-09-11 10:20:00",
                    likedByMe = false,
                    dislikedByMe = false
                ),
                PostComment(
                    index = 1002,
                    postId = 102,
                    userId = 1,
                    content = "建议坐地铁2号线换乘6号线。",
                    like = 0,
                    dislike = 0,
                    date = "2025-09-11 10:25:00",
                    likedByMe = false,
                    dislikedByMe = false
                )
            )
            postCommentDao.insertAll(postComments)

            // 6) 插入 PointComments（引用 pointId/userId）
            val pointComments = listOf(
                PointComment(
                    index = 3001,
                    pointId = 201,
                    userId = 2,
                    content = "咖啡不错，人不多。",
                    like = 0,
                    dislike = 0,
                    date = "2025-09-11 10:05:00"
                ),
                PointComment(
                    index = 3002,
                    pointId = 202,
                    userId = 1,
                    content = "周末人多，建议早上去。",
                    like = 0,
                    dislike = 0,
                    date = "2025-09-11 10:15:00"
                )
            )
            pointCommentDao.insertAll(pointComments)
        }
    }
}

