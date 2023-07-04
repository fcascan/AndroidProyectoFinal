package com.fcascan.proyectofinal.repositories

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream

class FilesManager {
    private val _TAG = "FCC#FilesManager"
    //Audios are saved in: /data/data/com.fcascan.proyectofinal/files/audios
    //Recordings are saved in: /data/data/com.fcascan.proyectofinal/files/recordings
    //Received files are saved in: /data/data/com.fcascan.proyectofinal/files/received

    fun checkFoldersExistence(rootDirectory: File) {
        Log.d("$_TAG - checkFolders", "Checking folders existence...")
        if (rootDirectory.exists()) {
            val audiosDirectory = File(rootDirectory, "audios")
            if (!audiosDirectory.exists()) {
                audiosDirectory.mkdirs()
                Log.d("$_TAG - checkFolders", "Folder created: ${audiosDirectory.absolutePath}")
            } else {
                Log.d("$_TAG - checkFolders", "Folder already exists: ${audiosDirectory.absolutePath}")
            }
            val recordingsDirectory = File(rootDirectory, "recordings")
            if (!recordingsDirectory.exists()) {
                recordingsDirectory.mkdirs()
                Log.d("$_TAG - checkFolders", "Folder created: ${recordingsDirectory.absolutePath}")
            } else {
                Log.d("$_TAG - checkFolders", "Folder already exists: ${recordingsDirectory.absolutePath}")
            }
            val receivedDirectory = File(rootDirectory, "received")
            if (!receivedDirectory.exists()) {
                receivedDirectory.mkdirs()
                Log.d("$_TAG - checkFolders", "Folder created: ${receivedDirectory.absolutePath}")
            } else {
                Log.d("$_TAG - checkFolders", "Folder already exists: ${receivedDirectory.absolutePath}")
            }
        } else {
            Log.d("$_TAG - checkFolders", "RootDirectory not recognized: ${rootDirectory.absolutePath}")
        }
    }

    fun insertFileIntoInternalMemory(bytes: ByteArray, fileName: String, context: Context) {
        Log.d("$_TAG - insertFileIntoInternalMemory", "fileName: $fileName")
        val directory = File(context.filesDir, "audios")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, fileName)
        if (file.exists()) {
            Log.d("$_TAG - insertFileIntoInternalMemory", "File already exists: $fileName")
            return
        }
        try {
            val outputStream = FileOutputStream(file)
            outputStream.write(bytes)
            outputStream.close()
            Log.d("$_TAG - insertFileIntoInternalMemory", "File saved: $fileName")
        } catch (e: Exception) {
            Log.e("$_TAG - insertFileIntoInternalMemory", "Failed to save file: $fileName, Exception: $e")
        }
    }

    suspend fun deleteFileFromInternalMemory(fileName: String, context: Context) {
        Log.d("$_TAG - deleteFileFromInternalMemory", "fileName: $fileName")
        val file = File(context.filesDir, "${fileName}.opus")
        if (file.exists()) {
            file.delete()
            Log.d("$_TAG - deleteFileFromInternalMemory", "File deleted: ${fileName}.opus")
        } else {
            Log.d("$_TAG - deleteFileFromInternalMemory", "File does not exist: ${fileName}.opus")
        }
    }

    fun renameFile(file: File, newFile: File) {
        Log.d("$_TAG - renameFile", "file: $file")
        if (!file.exists()) {
            Log.d("$_TAG - renameFile", "File not found: ${file.name}")
            return
        }
        try {
            file.copyTo(newFile, true)
            Log.d("$_TAG - renameFile", "File renamed and moved to: ${newFile.absoluteFile}")
        } catch (e: Exception) {
            Log.e("$_TAG - renameFile", "Failed to rename file: ${file.name}, Exception: $e")
        }
    }
}