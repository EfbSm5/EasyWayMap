package com.efbsm5.easyway.data.models


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "comments")
data class Comment(
    @SerializedName("index") @PrimaryKey val index: Int,
    @SerializedName("comment_id") val commentId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("content") var content: String,
    @SerializedName("like") var like: Int,
    @SerializedName("dislike") val dislike: Int,
    @SerializedName("date") val date: String,
)
