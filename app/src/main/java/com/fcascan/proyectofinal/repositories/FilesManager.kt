package com.fcascan.proyectofinal.repositories

import android.content.Context
import android.util.Log
import com.fcascan.proyectofinal.enums.Result
import com.google.firebase.installations.Utils
import java.io.File
import java.io.FileOutputStream

class FilesManager {
    private val _TAG = "FCC#FilesManager"
    //Audios are saved in: /data/data/com.fcascan.proyectofinal/files/audios
    //Recordings are saved in: /data/data/com.fcascan.proyectofinal/files/recordings
    //Received files are saved in: /data/data/com.fcascan.proyectofinal/files/received

    fun checkFoldersExistence(rootDirectory: File) {
        Log.d("$_TAG - checkFoldersExistence", "Checking folders existence...")
        if (rootDirectory.exists()) {

            val audiosDirectory = File(rootDirectory, "audios")
            if (!audiosDirectory.exists()) {
                audiosDirectory.mkdirs()
                Log.d("$_TAG - checkFoldersExistence", "Folder created: ${audiosDirectory.absolutePath}")
            } else {
                Log.d("$_TAG - checkFoldersExistence", "Folder already exists: ${audiosDirectory.absolutePath}")
            }
            val recordingsDirectory = File(rootDirectory, "recordings")
            if (!recordingsDirectory.exists()) {
                recordingsDirectory.mkdirs()
                Log.d("$_TAG - checkFoldersExistence", "Folder created: ${recordingsDirectory.absolutePath}")
            } else {
                Log.d("$_TAG - checkFoldersExistence", "Folder already exists: ${recordingsDirectory.absolutePath}")
            }
            val receivedDirectory = File(rootDirectory, "received")
            if (!receivedDirectory.exists()) {
                receivedDirectory.mkdirs()
                Log.d("$_TAG - checkFoldersExistence", "Folder created: ${receivedDirectory.absolutePath}")
            } else {
                Log.d("$_TAG - checkFoldersExistence", "Folder already exists: ${receivedDirectory.absolutePath}")
            }
        } else {
            Log.e("$_TAG - checkFoldersExistence", "RootDirectory not recognized: ${rootDirectory.absolutePath}")
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

    fun deleteFileFromInternalMemory(fileName: String, context: Context) {
        Log.d("$_TAG - deleteFileFromInternalMemory", "fileName: $fileName")
        val file = File(context.filesDir, "${fileName}.opus")
        if (file.exists()) {
            file.delete()
            Log.d("$_TAG - deleteFileFromInternalMemory", "File deleted: ${fileName}.opus")
        } else {
            Log.e("$_TAG - deleteFileFromInternalMemory", "File does not exist: ${fileName}.opus")
        }
    }
    fun renameAndCopyFile(file: File, newFile: File) {
        Log.d("$_TAG - renameAndCopyFile", "file: $file")
        if (!file.exists()) {
            Log.e("$_TAG - renameAndCopyFile", "File not found: ${file.name}")
            return
        }
        try {
            file.copyTo(newFile, true)
            Log.d("$_TAG - renameAndCopyFile", "File renamed and moved to: ${newFile.absoluteFile}")
        } catch (e: Exception) {
            Log.e("$_TAG - renameAndCopyFile", "Failed to rename and copy file: ${file.name}, Exception: $e")
        }
    }

    fun wipeLocalFiles(context: Context) {
        Log.d("$_TAG - wipeLocalFiles", "Wiping local files...")
        try {
            val audiosDirectory = File(context.filesDir, "audios")
            if (audiosDirectory.exists()) {
                audiosDirectory.deleteRecursively()
            }
            val recordingsDirectory = File(context.filesDir, "recordings")
            if (recordingsDirectory.exists()) {
                recordingsDirectory.deleteRecursively()
            }
            val receivedDirectory = File(context.filesDir, "received")
            if (receivedDirectory.exists()) {
                receivedDirectory.deleteRecursively()
            }
            Log.d("$_TAG - wipeLocalFiles", "Local files wiped")
        } catch (e: Exception) {
            Log.e("$_TAG - wipeLocalFiles", "Failed to wipe local files, Exception: $e")
        }
    }
}