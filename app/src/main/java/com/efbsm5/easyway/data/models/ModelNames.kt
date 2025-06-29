package com.efbsm5.easyway.data.models

enum class ModelNames(val modelName: String) {
    DynamicPosts("dynamicposts"), Users("users"), Comments("comments"), EasyPoints("easypoints"), EasyPointSimplify(
        "easypoints"
    );

    fun replacePath(): String {
        return "/$modelName"
    }
}

sealed class ResponseWrapper {
    data class UserResponse(val data: List<User>) : ResponseWrapper()
    data class DynamicPostResponse(val data: List<DynamicPost>) : ResponseWrapper()
    data class EasyPointResponse(val data: List<EasyPoint>) : ResponseWrapper()
    data class CommentResponse(val data: List<Comment>) : ResponseWrapper()
}
