package com.efbsm5.easyway.data.network

import com.efbsm5.easyway.data.models.ModelNames
import com.efbsm5.easyway.data.models.assistModel.UpdateInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object EasyPointNetWork {
    private val network = ServiceCreator.create<HttpInterface>()

    suspend fun <T> sendRequest(modelNames: ModelNames): List<T> {
        @Suppress("UNCHECKED_CAST")
        return when (modelNames) {
            ModelNames.Posts, ModelNames.DynamicPosts -> network.getPosts().await() as List<T>
            ModelNames.Users -> network.getUsers().await() as List<T>
            ModelNames.PointComment -> network.getPointComments().await() as List<T>
            ModelNames.PostComments -> network.getPostComments().await() as List<T>
            ModelNames.EasyPoints -> network.getEasyPoints().await() as List<T>
            ModelNames.EasyPointSimplify -> network.getEasyPointSimplify().await() as List<T>
        }
    }

    suspend fun getUpdate(): UpdateInfo? {
        return network.getUpdate().await()
    }

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}