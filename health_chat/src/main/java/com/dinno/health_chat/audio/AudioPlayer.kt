package com.dinno.health_chat.audio

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri

internal class AudioPlayer(private val context: Context) {

    private var player: MediaPlayer? = null
    private val listeners = mutableListOf<() -> Unit>()

    fun playUri(uri: Uri) {
        MediaPlayer.create(context, uri).apply {
            setOnCompletionListener { listeners.forEach { it() } }
            player = this
            start()
        }
    }

    fun stop() {
        player?.stop()
        player?.release()
        player = null
    }

    fun addCompletionListener(onComplete: () -> Unit) {
        listeners.add(onComplete)
    }

    fun removeListeners() {
        listeners.clear()
    }
}