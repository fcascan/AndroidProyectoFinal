package com.fcascan.proyectofinal.repositories

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fcascan.proyectofinal.enums.PlaybackState
import java.io.File
import java.io.IOException

class PlaybackManager {
    private val _className = "FCC#PlaybackManager"

    private var mediaPlayer: MediaPlayer = MediaPlayer()

    private val _playBackState = MutableLiveData<PlaybackState>()
    val playBackState: LiveData<PlaybackState> get() = _playBackState

    var currentFilePlaying = ""

    init {
        mediaPlayer.setOnErrorListener { _, _, _ ->
            setPlaybackState(PlaybackState.ERROR)
            false
        }
        setPlaybackState(PlaybackState.STOPPED)
    }

    fun playContentUri(filename: String, context: Context, onPlaybackComplete: () -> Unit) {
        Log.d("$_className - playContentUri", "filename: $filename")
        val directory = File(context.filesDir, "audios")
        val file = File(directory, "${filename}.opus")
        if (getPlaybackState() == PlaybackState.PLAYING && currentFilePlaying == filename) {
            pausePlayback()
            return
        }
        if(getPlaybackState() == PlaybackState.PLAYING && currentFilePlaying != filename) {
            stopPlayback()
        }
        if (getPlaybackState() == PlaybackState.PAUSED && currentFilePlaying == filename) {
            resumePlayback()
            return
        }
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(file.absolutePath)
            mediaPlayer.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            mediaPlayer.setOnCompletionListener {
                Log.d("$_className - playContentUri", "Playback completed")
                setPlaybackState(PlaybackState.STOPPED)
                currentFilePlaying = ""
                onPlaybackComplete()
            }
            mediaPlayer.prepare()
            mediaPlayer.start()
            setPlaybackState(PlaybackState.PLAYING)
            currentFilePlaying = filename
        } catch (exception: IOException) {
            Log.e("$_className - playContentUri", "Exception: $exception")
            setPlaybackState(PlaybackState.ERROR)
            mediaPlayer.release()
        }
    }

    private fun resumePlayback() {
        Log.d("$_className - resumePlayback", "Resuming playback of ${currentFilePlaying}")
        try {
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
                setPlaybackState(PlaybackState.PLAYING)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            setPlaybackState(PlaybackState.ERROR)
        }
    }

    fun pausePlayback() {
        Log.d("$_className - pausePlayback", "Pausing playback...")
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                setPlaybackState(PlaybackState.PAUSED)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            setPlaybackState(PlaybackState.ERROR)
        }
    }

    fun stopPlayback() {
        Log.d("$_className - stopPlayback", "Stopping playback...")
        try {
            mediaPlayer.stop()
            mediaPlayer.reset()
            currentFilePlaying = ""
            setPlaybackState(PlaybackState.STOPPED)
        } catch (e: Exception) {
            e.printStackTrace()
            setPlaybackState(PlaybackState.ERROR)
        }
    }

    fun destroyMediaPlayer() {
        setPlaybackState(PlaybackState.STOPPED)
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.release()
    }

    //Private Methods:
    private fun setPlaybackState(state: PlaybackState) {
        Log.d("$_className - setPlaybackState", "PlaybackState: $state")
        _playBackState.value = state
    }

    private fun getPlaybackState(): PlaybackState {
        return _playBackState.value!!
    }
}