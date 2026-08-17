package com.efbsm5.easyway.viewmodel.communityViewModel

import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.PostComment

internal enum class CommentReaction {
    Like,
    Dislike,
}

internal fun togglePostLike(post: Post): Post {
    val targetLiked = !post.likedByMe
    val delta = if (targetLiked) 1 else -1
    return post.copy(
        likedByMe = targetLiked,
        like = (post.like + delta).coerceAtLeast(0),
    )
}

/** 点赞和点踩互斥，并保证计数不会低于 0。 */
internal fun toggleCommentReaction(
    comment: PostComment,
    reaction: CommentReaction,
): PostComment = when (reaction) {
    CommentReaction.Like -> {
        val targetLiked = !comment.likedByMe
        comment.copy(
            likedByMe = targetLiked,
            like = (comment.like + if (targetLiked) 1 else -1).coerceAtLeast(0),
            dislikedByMe = if (targetLiked) false else comment.dislikedByMe,
            dislike = if (targetLiked && comment.dislikedByMe) {
                (comment.dislike - 1).coerceAtLeast(0)
            } else {
                comment.dislike
            },
        )
    }

    CommentReaction.Dislike -> {
        val targetDisliked = !comment.dislikedByMe
        comment.copy(
            dislikedByMe = targetDisliked,
            dislike = (comment.dislike + if (targetDisliked) 1 else -1).coerceAtLeast(0),
            likedByMe = if (targetDisliked) false else comment.likedByMe,
            like = if (targetDisliked && comment.likedByMe) {
                (comment.like - 1).coerceAtLeast(0)
            } else {
                comment.like
            },
        )
    }
}
