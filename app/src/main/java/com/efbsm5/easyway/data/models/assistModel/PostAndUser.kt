package com.efbsm5.easyway.data.models.assistModel

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.User
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class PostAndUser(
    @Embedded val post: @RawValue Post, @Relation(
        parentColumn = "userId", entityColumn = "id"
    ) val user: @RawValue User
) : Parcelable
