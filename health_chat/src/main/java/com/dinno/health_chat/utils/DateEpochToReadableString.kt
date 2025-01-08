package com.dinno.health_chat.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal fun dateEpochToReadableString(dateInMillis: Long): String {
    val currentDate = Date(dateInMillis)
    val outputFormat = SimpleDateFormat(UI_DATE_WITH_HOUR_FORMAT, Locale.getDefault())
    return outputFormat.format(currentDate).replaceFirstChar { it.uppercase() }
}

private const val UI_DATE_WITH_HOUR_FORMAT = "MM.dd.yyyy, HH:mm"