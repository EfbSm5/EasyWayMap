package com.efbsm5.easyway.data.models

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "post", foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )], indices = [Index("userId")]
)
@Immutable
data class Post(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) @SerializedName("id") var id: Int = 0,
    @ColumnInfo(name = "title") @SerializedName("title") var title: String,
    @ColumnInfo(name = "type") @SerializedName("type") var type: Int,
    @ColumnInfo(name = "date") @SerializedName("date") var date: String,
    @ColumnInfo(name = "like") @SerializedName("like") var like: Int = 0,
    @ColumnInfo(name = "content") @SerializedName("content") var content: String,
    @SerializedName("lat") var lat: Double,
    @SerializedName("lng") var lng: Double,
    @SerializedName("position") var position: String,
    @ColumnInfo(name = "userId") @SerializedName("userId") var userId: Int,
    @SerializedName("photo") var photo: List<String>,
    @SerializedName("likedByMe") var likedByMe: Boolean = false
)

