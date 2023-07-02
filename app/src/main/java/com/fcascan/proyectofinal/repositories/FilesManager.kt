package com.fcascan.proyectofinal.repositories

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream

class FilesManager {
    private val _className = "FCC#FilesManager"
    //Files are saved in: /data/data/com.fcascan.proyectofinal/files/audios

    fun insertFileIntoInternalMemory(bytes: ByteArray, fileName: String, context: Context) {
        Log.d("$_className - insertFileIntoInternalMemory", "fileName: $fileName")
        val directory = File(context.filesDir, "audios")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, fileName)
        if (file.exists()) {
            Log.d("$_className - insertFileIntoInternalMemory", "File already exists: $fileName")
            return
        }
        try {
            val outputStream = FileOutputStream(file)
            outputStream.write(bytes)
            outputStream.close()
            Log.d("$_className - insertFileIntoInternalMemory", "File saved: $fileName")
        } catch (e: Exception) {
            Log.e("$_className - insertFileIntoInternalMemory", "Failed to save file: $fileName, Exception: $e")
        }
    }

    suspend fun deleteFileFromInternalMemory(fileName: String, context: Context) {
        Log.d("$_className - deleteFileFromInternalMemory", "fileName: $fileName")
        val file = File(context.filesDir, "${fileName}.opus")
        if (file.exists()) {
            file.delete()
            Log.d("$_className - deleteFileFromInternalMemory", "File deleted: ${fileName}.opus")
        } else {
            Log.d("$_className - deleteFileFromInternalMemory", "File does not exist: ${fileName}.opus")
        }
    }
}