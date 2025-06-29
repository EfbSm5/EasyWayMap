package com.efbsm5.easyway.data.network

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SyncWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    @OptIn(DelicateCoroutinesApi::class)
    override fun doWork(): Result {
        return try {
            GlobalScope.launch {
                IntentRepository.syncData()
            }
            Result.success()
        } catch (_: Exception) {
            Result.failure()
        }
    }
}