package com.efbsm5.easyway

import android.app.Application
import com.amap.api.maps.MapsInitializer
import com.amap.apis.utils.core.api.AMapUtilCoreApi

class SDKUtils private constructor(
    private val application: Application
) {

    companion object {
        private var instance: SDKUtils? = null

        fun init(application: Application) {
            if (null == instance) {
                instance = SDKUtils(application)
            }
            MapsInitializer.updatePrivacyShow(instance!!.application, true, true)
            MapsInitializer.updatePrivacyAgree(instance!!.application, true)
            AMapUtilCoreApi.setCollectInfoEnable(true)
        }

        fun getContext(): Application {
            return instance?.application ?: throw NullPointerException("SDKUtils instance == null")
        }
    }

}