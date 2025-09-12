package com.efbsm5.easyway.data.models.assistModel

import androidx.room.Embedded
import androidx.room.Relation
import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.PointComment

data class PointWithComments(
    @Embedded val point: EasyPoint,
    @Relation(
        entity = PointComment::class,
        parentColumn = "pointId",
        entityColumn = "pointId"
    )
    val comments: List<PointCommentAndUser>
)
