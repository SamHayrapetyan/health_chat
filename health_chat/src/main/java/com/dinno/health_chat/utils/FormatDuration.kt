package com.dinno.health_chat.utils

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
internal fun formatDuration(durationMillis: String?): String {
    if (durationMillis.isNullOrEmpty()) return "00:00"

    val duration = durationMillis.toLongOrNull() ?: return "00:00"
    val hours = duration / (1000 * 60 * 60)
    val minutes = (duration % (1000 * 60 * 60)) / (1000 * 60)
    val seconds = (duration % (1000 * 60)) / 1000

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}