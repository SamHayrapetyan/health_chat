package com.dinno.health_chat.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

internal fun Context.getFileSize(uri: Uri): Long? {
    return contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        cursor.moveToFirst()
        cursor.getLong(sizeIndex)
    }
}