package com.dinno.health_chat.utils

import java.text.SimpleDateFormat
import java.util.Locale

internal fun Long.toDateString(): String? = runCatching {
    val outputFormat = SimpleDateFormat(UI_DATE_WITH_HOUR_FORMAT, Locale.getDefault())
    outputFormat.format(this).replaceFirstChar { it.uppercase() }
}.getOrNull()

private const val UI_DATE_WITH_HOUR_FORMAT = "MM.dd.yyyy, HH:mm"