package com.efbsm5.easyway

import android.app.Application
import android.util.Log
import androidx.work.WorkManager
import com.efbsm5.easyway.data.dev.DevSeeder
import com.efbsm5.easyway.data.network.SyncWorker
import com.efbsm5.easyway.di.homePageModule
import com.melody.map.gd_compose.utils.MapUtils
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class EasyWayApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SDKUtils.init(this)
        startKoin {
            androidContext(this@EasyWayApplication)
            modules(homePageModule)
        }
        MapUtils.setMapPrivacy(this, true)
        Log.e("EasyWayApplication", "initMapPrivacy")

        // Debug 模式：填充开发初始数据（后台线程）
        if (BuildConfig.DEBUG) {
            Thread { DevSeeder.seedIfEmpty() }.start()
        }

        // 本地优先模式：取消历史版本已经登记的远端覆盖式同步任务。
        // SyncWorker 实现暂时保留，等未来定义可靠的上传/冲突协议后再启用。
        WorkManager.getInstance(this).cancelUniqueWork(SyncWorker.UNIQUE_NAME)
    }
}
