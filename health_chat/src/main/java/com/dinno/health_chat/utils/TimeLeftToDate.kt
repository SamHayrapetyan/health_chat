package com.dinno.health_chat.utils

import java.util.concurrent.TimeUnit


fun Long.timeLeftToDate(): Pair<Long, Long>? {
    val currentTimeMillis = System.currentTimeMillis()
    val timeDifferenceMillis = this - currentTimeMillis

    // Ensure the time difference is non-negative
    if (timeDifferenceMillis <= 0) return null

    // Convert milliseconds to days and hours
    val days = TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis)
    val hours = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis) % 24

    return days to hours
}