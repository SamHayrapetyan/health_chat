package com.dinno.health_chat.audio

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class AudioPlayer(private val context: Context) {

    private var player: MediaPlayer? = null
    private val listeners = mutableListOf<AudioPlayerListener>()

    private val mutex = Mutex()

    suspend fun playUri(uri: Uri) {
        mutex.withLock {
            player?.stop()
            player?.release()
            player = null
            MediaPlayer.create(context, uri).apply {
                setOnPreparedListener { listeners.forEach { it.onStart() } }
                setOnCompletionListener { listeners.forEach { it.onComplete() } }
                start()
                player = this
            }
        }
    }

    suspend fun stop() {
        mutex.withLock {
            listeners.forEach { it.onStop() }
            player?.stop()
            player?.release()
            player = null
        }
    }

    fun addCompletionListener(listener: AudioPlayerListener) {
        listeners.add(listener)
    }

    fun removeListeners() {
        listeners.clear()
    }
}

internal class AudioPlayerListener(
    val onStart: () -> Unit,
    val onComplete: () -> Unit,
    val onStop: () -> Unit
)