package com.efbsm5.easyway.data.models.assistModel

import androidx.room.Embedded
import androidx.room.Relation
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.PostComment

data class PostWithComments(
    @Embedded val post: Post,
    @Relation(
        entity = PostComment::class,
        parentColumn = "postId",
        entityColumn = "postOwnerId"
    )
    val comments: List<PointCommentAndUser>
)

data class PointWithComments(
    @Embedded val point: EasyPoint,
    @Relation(
        entity = EasyPoint::class,
        parentColumn = "postId",
        entityColumn = "postOwnerId"
    )
    val comments: List<PointCommentAndUser>
)
