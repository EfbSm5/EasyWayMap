package com.efbsm5.easyway.data.models

enum class ModelNames(val modelName: String) {
    DynamicPosts("dynamicposts"), Users("users"), Comments("comments"), EasyPoints("easypoints"), EasyPointSimplify(
        "easypoints"
    );

    fun replacePath(): String {
        return "/$modelName"
    }
}
