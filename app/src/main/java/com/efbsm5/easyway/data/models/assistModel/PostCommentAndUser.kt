package com.efbsm5.easyway.data.models.assistModel

import androidx.room.Embedded
import androidx.room.Relation
import com.efbsm5.easyway.data.models.PostComment
import com.efbsm5.easyway.data.models.User

data class PostCommentAndUser(
    @Embedded val postComment: PostComment,
    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user: User
)