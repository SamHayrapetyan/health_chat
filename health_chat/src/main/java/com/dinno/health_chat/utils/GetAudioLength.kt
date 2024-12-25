package com.dinno.health_chat.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri

internal fun Context.getAudioLength(uri: Uri): String? {
    val mediaPath = uri.path
    val mmr = MediaMetadataRetriever()
    return try {
        mmr.setDataSource(mediaPath)
        mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
    } finally {
        mmr.release()
    }
}