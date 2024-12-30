package com.dinno.health_chat.utils

internal fun bytesToReadableString(sizeInBytes: Long): String {
    return (sizeInBytes / 1024).toString()
}