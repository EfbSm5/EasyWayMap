package com.efbsm5.easyway.data.network

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

/**
 * 兼容旧版本已注册的周期任务。
 *
 * 当前版本以 Room 为唯一数据源，旧任务即使尚未被 WorkManager 取消，也不能再覆盖本地数据。
 */
class SyncWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = Result.success()

    companion object {
        const val UNIQUE_NAME = "PeriodicSync"

        fun buildRequest(
            repeatIntervalMinutes: Long = 15,
        ): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()
            return PeriodicWorkRequestBuilder<SyncWorker>(repeatIntervalMinutes, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .addTag(UNIQUE_NAME)
                .build()
        }
    }
}
