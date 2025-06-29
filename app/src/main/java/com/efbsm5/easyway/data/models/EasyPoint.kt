package com.efbsm5.easyway.data.models

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "points")
data class EasyPoint(
    @PrimaryKey(autoGenerate = true) @SerializedName("point_id") var pointId: Int,
    @ColumnInfo(name = "name") @SerializedName("name") var name: String,
    @ColumnInfo(name = "type") @SerializedName("type") var type: String,
    @ColumnInfo(name = "info") @SerializedName("info") var info: String,
    @ColumnInfo(name = "location") @SerializedName("location") var location: String,
    @ColumnInfo(name = "photo") @SerializedName("photo") var photo: Uri?,
    @ColumnInfo(name = "refresh_time") @SerializedName("refresh_time") var refreshTime: String,
    @ColumnInfo(name = "like") @SerializedName("like") var likes: Int,
    @ColumnInfo(name = "dislike") @SerializedName("dislike") var dislikes: Int,
    @ColumnInfo(name = "lat") @SerializedName("lat") var lat: Double,
    @ColumnInfo(name = "lng") @SerializedName("lng") var lng: Double,
    @ColumnInfo(name = "user_id") @SerializedName("user_id") var userId: Int,
    @ColumnInfo(name = "comment_id") @SerializedName("comment_id") var commentId: Int,
)