package com.efbsm5.easyway.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.efbsm5.easyway.SDKUtils
import com.efbsm5.easyway.data.models.User

object UserManager {
    private val prefs: SharedPreferences =
        SDKUtils.getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var userId: Int
        get() = prefs.getInt("userId", 0)
        set(value) {
            prefs.edit { putInt("userId", value) }
        }
    var name: String
        get() = prefs.getString("name", " ") ?: ""
        set(value) {
            prefs.edit { putString("name", value) }
        }
    var avatar: String
        get() = prefs.getString("avatar", " ") ?: ""
        set(value) {
            prefs.edit { putString("avatar", value) }
        }

    fun getUser(): User {
        return User(
            id = userId,
            name = name,
            avatar = avatar
        )
    }
}