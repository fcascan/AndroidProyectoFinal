package com.fcascan.proyectofinal.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.fcascan.proyectofinal.R
import java.io.File

class FileSelectActivity : AppCompatActivity() {
    private val _className = "FCC#FileSelectActivity"

//    https://developer.android.com/training/secure-file-sharing/share-file?hl=es-419
    private lateinit var privateRootDir: File   // The path to the root of this app's internal storage
    private lateinit var audiosDir: File    // The path to the "audios" subdirectory
    private lateinit var audiosFiles: Array<File>   // Array of files in the audios subdirectory
    private lateinit var audiosFilenames: Array<String>     // Array of filenames corresponding to audioFiles

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_select)

        // Get the files/ subdirectory of internal storage
        privateRootDir = filesDir
        // Get the files/audios subdirectory;
        audiosDir = File(privateRootDir, "audios")
        Log.d(_className, "audiosDir: $audiosDir")
        // Get the files in the audios subdirectory
        audiosFiles = audiosDir.listFiles()
        Log.d(_className, "audiosFiles: $audiosFiles")
        // Set the Activity's result to null to begin with
        setResult(Activity.RESULT_CANCELED, null)

    }

    override fun onBackPressed() {
        // Handle back press to cancel file selection
        Log.d("$_className - onBackPressed", "Clear Button Clicked")
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    override fun onDestroy() {
        // Handle activity destruction without file selection
        Log.d("$_className - onDestroy", "File Select Cancelled")
        setResult(Activity.RESULT_CANCELED)
        super.onDestroy()
    }

    private fun selectFile(fileUri: Uri) {
        // Set up an Intent to send back to apps that request a file
        Log.d("$_className - selectFile", "fileUri: $fileUri")
        val resultIntent = Intent("android.intent.action.ACTION_RETURN_FILE").apply {
            data = fileUri
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}