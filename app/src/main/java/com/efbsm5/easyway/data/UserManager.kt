package com.efbsm5.easyway.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.efbsm5.easyway.SDKUtils
import com.efbsm5.easyway.data.models.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

object UserManager {
    private val prefs: SharedPreferences =
        SDKUtils.getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var userId: Int
        get() = prefs.getInt(USER_ID_KEY, 0)
        set(value) {
            prefs.edit { putInt(USER_ID_KEY, value) }
        }

    val userIdFlow: Flow<Int> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == USER_ID_KEY) {
                trySend(userId)
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(userId)
        awaitClose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }.distinctUntilChanged()

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

    private const val USER_ID_KEY = "userId"
}
