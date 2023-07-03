import android.media.MediaRecorder
import java.io.File
import java.io.IOException

class AudioRecorderManager {
    private val _TAG = "FCC#AudioRecorderManager"

    private val mediaRecorder = MediaRecorder()
    private var isRecording = false

    private var outputFile: File? = null // Output file path

    fun setOutputFile(file: File) {
        outputFile = file
    }

    fun initAudioRecorder() {
        mediaRecorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile?.absolutePath)
        }
    }

    fun startRecording() {
        try {
            initAudioRecorder()
            mediaRecorder.prepare()
            mediaRecorder.start()
            isRecording = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stopRecording() {
        mediaRecorder.apply {
            stop()
            reset()
        }
        isRecording = false
    }

    fun releaseRecorder() {
        mediaRecorder.release()
    }
}