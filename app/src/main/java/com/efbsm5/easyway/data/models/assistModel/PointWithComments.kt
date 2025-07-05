package com.efbsm5.easyway.data.models.assistModel

import androidx.room.Embedded
import androidx.room.Relation
import com.efbsm5.easyway.data.models.EasyPoint

data class PointWithComments(
    @Embedded val point: EasyPoint,
    @Relation(
        entity = EasyPoint::class,
        parentColumn = "pointId",
        entityColumn = "pointId"
    )
    val comments: List<PointCommentAndUser>
)
