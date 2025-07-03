package com.efbsm5.easyway.data.models.assistModel

import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.PointComment
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.User

sealed class ResponseWrapper {
    data class UserResponse(val data: List<User>) : ResponseWrapper()
    data class DynamicPostResponse(val data: List<Post>) : ResponseWrapper()
    data class EasyPointResponse(val data: List<EasyPoint>) : ResponseWrapper()
    data class CommentResponse(val data: List<PointComment>) : ResponseWrapper()
}