package com.efbsm5.easyway

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.efbsm5.easyway.data.dev.DevSeeder
import com.efbsm5.easyway.data.network.SyncWorker
import com.melody.map.gd_compose.utils.MapUtils

class EasyWayApplication : Application(), Configuration.Provider {
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().build()

    override fun onCreate() {
        super.onCreate()
        // 原先在 AppDataInitStartup 中的初始化逻辑
        SDKUtils.init(this)
        MapUtils.setMapPrivacy(this, true)
        Log.e("EasyWayApplication", "initMapPrivacy")

        // Debug 模式：填充开发初始数据（后台线程）
        if (BuildConfig.DEBUG) {
            Thread { DevSeeder.seed() }.start()
        }

        // 调度周期同步任务
        val request = SyncWorker.buildRequest(repeatIntervalMinutes = 15)
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            SyncWorker.UNIQUE_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}
