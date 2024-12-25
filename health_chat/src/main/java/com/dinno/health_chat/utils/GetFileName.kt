package com.dinno.health_chat.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

internal fun Context.getFileName(uri: Uri): String? {
    return contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        cursor.getString(nameIndex)
    }
}