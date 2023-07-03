package com.fcascan.proyectofinal.ui.recording

import AudioRecorderManager
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
//import com.fcascan.proyectofinal.repositories.NewAudioRecorderManager
import java.io.File

class RecordingViewModel : ViewModel() {
    private val _TAG = "FCC#RecordingViewModel"

    private val _audioRecorderManager = AudioRecorderManager()

    private val _isRecording = MutableLiveData<Boolean>()
    val isRecording: LiveData<Boolean> get() = _isRecording

    private val _recordingReady = MutableLiveData<Boolean>()
    val recordingReady: LiveData<Boolean> get() = _recordingReady


    fun initAudioRecorder(context: Context) {
        Log.d("$_TAG - initAudioRecorder", "Initializing Audio Recorder...")
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("$_TAG - initAudioRecorder", "Permissions granted")
            setRecordingReady(true)
            val directory = File(context.filesDir, "recordings")
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val outputFile = File(directory, "recorded_audio.aac") // Output file path
            _audioRecorderManager.setOutputFile(outputFile)
        } else {
            Log.d("$_TAG - initAudioRecorder", "No permissions granted")
            setRecordingReady(false)
        }
    }

    fun onRecordLongClicked(context: Context) {
        Log.d("$_TAG - onRecordClicked", "Recording button is being pressed")
        setIsRecording(true)
        _audioRecorderManager.startRecording()
    }

    fun onRecordLongReleased() {
        Log.d("$_TAG - onRecordReleased", "Recording button released")
        setIsRecording(false)
        _audioRecorderManager.stopRecording()
    }

    private fun setIsRecording(value: Boolean) {
        _isRecording.postValue(value)
    }

    private fun setRecordingReady(value: Boolean) {
        _recordingReady.postValue(value)
    }

}