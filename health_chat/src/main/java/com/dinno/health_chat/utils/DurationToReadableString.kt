package com.dinno.health_chat.utils

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
internal fun durationToReadableString(duration: Long): String {
    val hours = duration / (1000 * 60 * 60)
    val minutes = (duration % (1000 * 60 * 60)) / (1000 * 60)
    val seconds = (duration % (1000 * 60)) / 1000

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}