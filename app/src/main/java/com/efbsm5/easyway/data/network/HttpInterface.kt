package com.efbsm5.easyway.data.network

import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.PointComment
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.PostComment
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.data.models.assistModel.EasyPointSimplify
import com.efbsm5.easyway.data.models.assistModel.UpdateInfo
import retrofit2.Call
import retrofit2.http.GET


interface HttpInterface {
    @GET("posts")
    fun getPosts(): Call<List<Post>>

    @GET("users")
    fun getUsers(): Call<List<User>>

    @GET("pointcomments")
    fun getPointComments(): Call<List<PointComment>>

    @GET("postcomments")
    fun getPostComments(): Call<List<PostComment>>

    @GET("easypoints")
    fun getEasyPoints(): Call<List<EasyPoint>>

    @GET("easypoints")
    fun getEasyPointSimplify(): Call<List<EasyPointSimplify>>

    @GET("/checkupdate")
    fun getUpdate(): Call<UpdateInfo>
}