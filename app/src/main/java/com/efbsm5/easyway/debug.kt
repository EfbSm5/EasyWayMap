package com.efbsm5.easyway

import android.util.Log
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import com.efbsm5.easyway.repo.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.roundToLong

data class LatencyResult(
    val runs: Int, val timesMs: List<Long>, val avgMs: Double, val minMs: Long, val maxMs: Long
)

private const val TAG = "debug"

/**
 * 在单个事务中测量一次 Room 更新操作的延迟。
 * 如果你的更新需要事务一致性，推荐使用该方法进行评测。
 * 用法: val ms = measureRoomUpdateLatencyTx(db) { dao.update(entity) }
 */
suspend fun measureRoomUpdateLatencyTx(
    db: RoomDatabase, block: suspend () -> Unit
): Long {
    val start = System.nanoTime()
    withContext(Dispatchers.IO) {
        db.withTransaction {
            block()
        }
    }
    val end = System.nanoTime()
    return (end - start) / 1_000_000
}


/**
 * 在事务中重复测量 Room 更新延迟，适用于需要事务包裹的一组更新操作。
 * 用法:
 * val result = measureRoomUpdateLatencyRepeatTx(db, runs = 20) { dao.update(entity) }
 */
suspend fun measureRoomUpdateLatencyRepeatTx(
    db: RoomDatabase,
    runs: Int = 10,
    warmup: Int = 1,
    delayBetweenMs: Long = 50,
) {
    repeat(warmup) {
        withContext(Dispatchers.IO) {
            db.withTransaction { DataRepository.getAllPoints() }
        }
    }

    val times = mutableListOf<Long>()
    repeat(runs) {
        val ms = measureRoomUpdateLatencyTx(db) { DataRepository.getAllPoints() }
        times.add(ms)
        if (delayBetweenMs > 0) delay(delayBetweenMs)
    }

    val avg = if (times.isEmpty()) 0.0 else times.average()
    val min = times.minOrNull() ?: 0L
    val max = times.maxOrNull() ?: 0L
    val result = LatencyResult(
        runs = runs,
        timesMs = times,
        avgMs = (avg * 1000).roundToLong() / 1000.0,
        minMs = min,
        maxMs = max
    )

    Log.d(TAG, "debug: ")

    Log.d(TAG, "measureRoomUpdateLatencyRepeatTx: ${result.pretty()}")

}

/**
 * 将统计结果格式化为便于日志输出的字符串。
 */
fun LatencyResult.pretty(tag: String = "RoomLatency"): String {
    val head = "[$tag] runs=$runs, avg=${avgMs}ms, min=${minMs}ms, max=${maxMs}ms"
    val details = timesMs.joinToString(prefix = "[", postfix = "]") { it.toString() }
    return "$head times=$details"
}

