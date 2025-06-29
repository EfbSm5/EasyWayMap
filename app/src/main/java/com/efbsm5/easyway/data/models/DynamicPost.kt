package com.efbsm5.easyway.data.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "dynamicposts")
data class DynamicPost(
    @PrimaryKey @SerializedName("id") var id: Int,
    @SerializedName("title") var title: String,
    @SerializedName("type") var type: Int,
    @SerializedName("date") var date: String,
    @SerializedName("like") var like: Int,
    @SerializedName("content") var content: String,
    @SerializedName("lat") var lat: Double,
    @SerializedName("lng") var lng: Double,
    @SerializedName("position") var position: String,
    @SerializedName("user_id") var userId: Int,
    @SerializedName("comment_id") var commentId: Int,
    @SerializedName("photo") var photo: List<Uri>
)

