package com.efbsm5.easyway.viewmodel.communityViewModel

import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.PostComment
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.data.models.assistModel.PostAndUser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CommunityLogicTest {

    @Test
    fun filterCommunityPosts_appliesCategoryAndKeywordTogether() {
        val posts = listOf(
            postAndUser(id = 1, type = 1, title = "无障碍活动"),
            postAndUser(id = 2, type = 2, title = "路线求助"),
            postAndUser(id = 3, type = 1, title = "周末分享"),
        )

        val result = filterCommunityPosts(
            posts = posts,
            query = "活动",
            selectedTab = 1,
        )

        assertEquals(listOf(1), result.map { it.post.id })
    }

    @Test
    fun togglePostLike_neverProducesNegativeCount() {
        val post = postAndUser(id = 1, type = 1, title = "test").post.copy(
            likedByMe = true,
            like = 0,
        )

        val result = togglePostLike(post)

        assertFalse(result.likedByMe)
        assertEquals(0, result.like)
    }

    @Test
    fun toggleCommentReaction_switchesDislikeToLikeAtomically() {
        val comment = PostComment(
            index = 7,
            postId = 1,
            userId = 1,
            content = "test",
            like = 2,
            dislike = 3,
            date = "2026-08-12",
            likedByMe = false,
            dislikedByMe = true,
        )

        val result = toggleCommentReaction(comment, CommentReaction.Like)

        assertTrue(result.likedByMe)
        assertFalse(result.dislikedByMe)
        assertEquals(3, result.like)
        assertEquals(2, result.dislike)
    }

    @Test
    fun toggleCommentReaction_removesExistingDislike() {
        val comment = PostComment(
            index = 7,
            postId = 1,
            userId = 1,
            content = "test",
            like = 0,
            dislike = 1,
            date = "2026-08-12",
            likedByMe = false,
            dislikedByMe = true,
        )

        val result = toggleCommentReaction(comment, CommentReaction.Dislike)

        assertFalse(result.dislikedByMe)
        assertEquals(0, result.dislike)
    }

    private fun postAndUser(id: Int, type: Int, title: String): PostAndUser = PostAndUser(
        post = Post(
            id = id,
            title = title,
            type = type,
            date = "2026-08-12",
            content = "content",
            lat = 30.0,
            lng = 114.0,
            position = "武汉",
            userId = 1,
            photo = emptyList(),
        ),
        user = User(id = 1, name = "本地用户", avatar = null),
    )
}
