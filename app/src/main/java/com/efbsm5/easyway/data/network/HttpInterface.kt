package com.efbsm5.easyway.data.network

import com.efbsm5.easyway.data.models.assistModel.UpdateInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface HttpInterface {
    @GET("{path}")
    fun <E> getData(@Path("path") modelname: String): Call<List<E>>

    @GET("/checkupdate")
    fun getUpdate(): Call<UpdateInfo>
}