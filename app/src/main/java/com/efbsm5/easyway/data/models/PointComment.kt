package com.efbsm5.easyway.data.models


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "pointComment", foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = EasyPoint::class,
        parentColumns = ["pointId"],
        childColumns = ["pointId"],
        onDelete = ForeignKey.CASCADE
    )], indices = [Index("pointId"), Index("userId")]
)
data class PointComment(
    @ColumnInfo(name = "index") @SerializedName("index") @PrimaryKey(autoGenerate = true) val index: Int = 0,
    @ColumnInfo(name = "pointId") @SerializedName("pointId") val pointId: Int,
    @ColumnInfo(name = "userId") @SerializedName("userId") val userId: Int,
    @ColumnInfo(name = "content") @SerializedName("content") var content: String,
    @ColumnInfo(name = "like") @SerializedName("like") var like: Int = 0,
    @ColumnInfo(name = "dislike") @SerializedName("dislike") val dislike: Int = 0,
    @ColumnInfo(name = "date") @SerializedName("date") val date: String,
)
