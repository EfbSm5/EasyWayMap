package com.efbsm5.easyway.data.models.assistModel

import androidx.room.Embedded
import androidx.room.Relation
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.User

data class PostAndUser(
    @Embedded val post: Post, @Relation(
        parentColumn = "userId", entityColumn = "id"
    ) val user: User
)
