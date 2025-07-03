package com.efbsm5.easyway.data.models


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "pointComment",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Post::class,
        parentColumns = ["id"],
        childColumns = ["pointId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("pointId"), Index("userId")]
)
data class PointComment(
    @SerializedName("index") @PrimaryKey val index: Int,
    @SerializedName("point_id") val pointId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("content") var content: String,
    @SerializedName("like") var like: Int,
    @SerializedName("dislike") val dislike: Int,
    @SerializedName("date") val date: String,
)
