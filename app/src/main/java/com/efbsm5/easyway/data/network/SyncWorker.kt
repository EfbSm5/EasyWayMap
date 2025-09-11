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
 * 周期数据同步 Worker：调用 IntentRepository.syncData()
 */
class SyncWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        return try {
            IntentRepository.syncData()
            Result.success()
        } catch (t: Throwable) {
            // 网络异常等情况重试
            Result.retry()
        }
    }

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

