package com.efbsm5.easyway.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "point", foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )], indices = [Index("userId")]
)
data class EasyPoint(
    @PrimaryKey(autoGenerate = true) @SerializedName("point_id") var pointId: Int,
    @ColumnInfo(name = "name") @SerializedName("name") var name: String,
    @ColumnInfo(name = "type") @SerializedName("type") var type: String,
    @ColumnInfo(name = "info") @SerializedName("info") var info: String,
    @ColumnInfo(name = "location") @SerializedName("location") var location: String,
    @ColumnInfo(name = "photo") @SerializedName("photo") var photo: String?,
    @ColumnInfo(name = "refresh_time") @SerializedName("refresh_time") var refreshTime: String,
    @ColumnInfo(name = "like") @SerializedName("like") var likes: Int,
    @ColumnInfo(name = "dislike") @SerializedName("dislike") var dislikes: Int,
    @ColumnInfo(name = "lat") @SerializedName("lat") var lat: Double,
    @ColumnInfo(name = "lng") @SerializedName("lng") var lng: Double,
    @ColumnInfo(name = "userId") @SerializedName("userId") var userId: Int,
)