package com.efbsm5.easyway.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.efbsm5.easyway.SDKUtils

object UserManager {
    private val prefs: SharedPreferences =
        SDKUtils.getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var userId: Int
        get() = prefs.getInt("user_id", 0)
        set(value) {
            prefs.edit { putInt("user_id", value) }
        }

}