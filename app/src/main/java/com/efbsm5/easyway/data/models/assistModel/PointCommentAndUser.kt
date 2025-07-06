package com.efbsm5.easyway.data.models.assistModel

import androidx.room.Embedded
import androidx.room.Relation
import com.efbsm5.easyway.data.models.PointComment
import com.efbsm5.easyway.data.models.User

data class PointCommentAndUser(
    @Embedded val pointComment: PointComment,
    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user: User
)


