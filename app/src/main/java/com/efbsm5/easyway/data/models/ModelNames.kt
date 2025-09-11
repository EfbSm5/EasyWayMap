package com.efbsm5.easyway.data.models

enum class ModelNames(val modelName: String) {
    Posts("posts"),
    DynamicPosts("posts"),
    Users("users"),
    PointComment("pointcomments"), // PointComment
    PostComments("postcomments"),
    EasyPoints("easypoints"),
    EasyPointSimplify("easypoints");

    fun replacePath(): String {
        return "/$modelName"
    }
}
