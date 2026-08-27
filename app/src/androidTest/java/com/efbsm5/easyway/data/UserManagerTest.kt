package com.efbsm5.easyway.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserManagerTest {

    @Test
    fun userIdFlow_emitsDistinctPreferenceChanges() = runBlocking {
        val originalUserId = UserManager.userId
        val nextUserId = if (originalUserId == Int.MAX_VALUE) 1 else originalUserId + 1
        val observedUserIds = mutableListOf<Int>()

        try {
            val collection = launch(start = CoroutineStart.UNDISPATCHED) {
                UserManager.userIdFlow.take(2).toList(observedUserIds)
            }
            UserManager.userId = originalUserId
            UserManager.userId = nextUserId
            withTimeout(2_000) {
                collection.join()
            }

            assertEquals(listOf(originalUserId, nextUserId), observedUserIds)
        } finally {
            UserManager.userId = originalUserId
        }
    }
}
