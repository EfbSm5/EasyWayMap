package com.efbsm5.easyway.data.network

import com.efbsm5.easyway.data.models.EasyPoint
import com.efbsm5.easyway.data.models.ModelNames
import com.efbsm5.easyway.data.models.PointComment
import com.efbsm5.easyway.data.models.Post
import com.efbsm5.easyway.data.models.User
import com.efbsm5.easyway.data.models.assistModel.EasyPointSimplify
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
        return when (modelNames) {
            ModelNames.DynamicPosts -> network.getData<Post>(modelNames.replacePath()).await()

            ModelNames.Users -> network.getData<User>(modelNames.replacePath()).await()
            ModelNames.Comments -> network.getData<PointComment>(modelNames.replacePath()).await()
            ModelNames.EasyPoints -> network.getData<EasyPoint>(modelNames.replacePath()).await()

            ModelNames.EasyPointSimplify -> network.getData<EasyPointSimplify>(modelNames.replacePath())
                .await()
        } as List<T>
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
                    else continuation.resumeWithException(
                        RuntimeException("response body is null")
                    )
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    fun go(block: () -> Unit) {
        try {
            block
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    private val gson: Gson =
//        GsonBuilder().registerTypeAdapter(Uri::class.java, UriTypeAdapter()).create()
//
//
//    private fun uriToFile(context: Context, uri: Uri): File {
//        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
//        val tempFile = File(context.cacheDir, "temp_image")
//        val outputStream: OutputStream = FileOutputStream(tempFile)
//        inputStream?.use { input ->
//            outputStream.use { output ->
//                input.copyTo(output)
//            }
//        }
//        return tempFile
//    }
//
//    fun uploadImage(context: Context, uri: Uri, callback: (Uri?) -> Unit) {
//        val file = uriToFile(context, uri)
//        val mediaType = "image/jpeg".toMediaType()
//        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
//            .addFormDataPart("photo", file.name, file.asRequestBody(mediaType)).build()
//        val request = Request.Builder().url("$baseUrl/upload").post(requestBody).build()
//        client.newCall(request).enqueue(getCallback { json ->
//            var url: Uri? = null
//            if (json != null) {
//                val json = JSONObject(json)
//                url = json.getString("url").toUri()
//            }
//            callback(url)
//        })
//    }
//
//    fun uploadData(data: Any, callback: (Boolean) -> Unit) {
//        val mediaType = "application/json; charset=utf-8".toMediaType()
//        val requestBody = gson.toJson(data).toRequestBody(mediaType)
//        var uploadType = when (data) {
//            is User -> ModelNames.Users
//            is Comment -> ModelNames.Comments
//            is DynamicPost -> ModelNames.DynamicPosts
//            is EasyPoint -> ModelNames.EasyPoints
//            else -> throw IllegalArgumentException("unsupported data")
//        }
//        val request = Request.Builder().url("$baseUrl:5000/$uploadType").post(requestBody).build()
//
//        client.newCall(request).enqueue(getCallback { })
//    }
}