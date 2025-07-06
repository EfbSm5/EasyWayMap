package com.efbsm5.easyway.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "post",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("userId")]
)
data class Post(
    @PrimaryKey(autoGenerate = true) @SerializedName("id") var id: Int = 0,
    @SerializedName("title") var title: String,
    @SerializedName("type") var type: Int,
    @SerializedName("date") var date: String,
    @SerializedName("like") var like: Int,
    @SerializedName("content") var content: String,
    @SerializedName("lat") var lat: Double,
    @SerializedName("lng") var lng: Double,
    @SerializedName("position") var position: String,
    @SerializedName("user_id") var userId: Int,
    @SerializedName("photo") var photo: List<String>
)

