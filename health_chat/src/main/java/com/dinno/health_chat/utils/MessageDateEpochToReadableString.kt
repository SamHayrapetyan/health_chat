package com.dinno.health_chat.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

internal fun messageDateEpochToReadableString(dateInMillis: Long): String = runCatching {
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val messageDate = Calendar.getInstance().apply { timeInMillis = dateInMillis }
    // If timestamp is of today, return hh:mm format
    if (messageDate.time.after(today.time)) {
        return SimpleDateFormat(UI_HOUR_FORMAT, Locale.getDefault()).format(messageDate.timeInMillis)
    } else {
        SimpleDateFormat(UI_DATE_WITH_HOUR_FORMAT, Locale.getDefault()).format(messageDate.timeInMillis)
    }
}.getOrElse { "" }

private const val UI_DATE_WITH_HOUR_FORMAT = "MM.dd.yyyy, HH:mm"
private const val UI_HOUR_FORMAT = "HH:mm"