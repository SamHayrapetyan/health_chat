package com.dinno.health_chat.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

internal fun messageDateEpochToReadableString(dateEpoch: Long): String {
    val currentDate = Date(dateEpoch)
    val cal = Calendar.getInstance()
    cal.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    // If timestamp is of today, return hh:mm format
    return if (currentDate.after(cal.time)) {
        return SimpleDateFormat(UI_HOUR_FORMAT, Locale.getDefault()).format(currentDate)
    } else {
        SimpleDateFormat(UI_DATE_WITH_HOUR_FORMAT, Locale.getDefault()).format(currentDate)
    }
}

private const val UI_DATE_WITH_HOUR_FORMAT = "MM.dd.yyyy, HH:mm"
private const val UI_HOUR_FORMAT = "HH:mm"