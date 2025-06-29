package com.efbsm5.easyway.data.models.assistModel

import com.efbsm5.easyway.data.models.DynamicPost
import com.efbsm5.easyway.data.models.User

class DynamicPostAndUser(
    val dynamicPost: DynamicPost,
    val user: User,
    val commentCount: Int,
)