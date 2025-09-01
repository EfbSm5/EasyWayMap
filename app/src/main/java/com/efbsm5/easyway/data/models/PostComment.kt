package com.efbsm5.easyway.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "postComment",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Post::class,
        parentColumns = ["id"],
        childColumns = ["postId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("postId"), Index("userId")]
)
data class PostComment(
    @SerializedName("index") @PrimaryKey val index: Int = 0,
    @ColumnInfo(name = "postId") @SerializedName("postId") val postId: Int,
    @ColumnInfo(name = "userId") @SerializedName("userId") val userId: Int,
    @SerializedName("content") var content: String,
    @SerializedName("like") var like: Int,
    @SerializedName("dislike") var dislike: Int,
    @SerializedName("date") val date: String,
)

