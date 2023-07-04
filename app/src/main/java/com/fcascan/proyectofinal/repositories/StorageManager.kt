package com.fcascan.proyectofinal.repositories

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.fcascan.proyectofinal.constants.MAX_FILE_SIZE_BYTES
import com.fcascan.proyectofinal.enums.Result
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.File

class StorageManager {
    private val _TAG = "FCC#StorageManager"

    private val _storage = Firebase.storage
    private val _filesManager = FilesManager()

    suspend fun downloadCollectionByUserID(userID: String, context: Context) {
        //path: /{userID}/{itemID}.opus
        Log.d("$_TAG - downloadCollectionByUserID", "Downloading...")
        _storage.reference
            .child(userID)
            .listAll()
            .await().also{ listResult ->
                listResult.items.forEach { item ->
                    Log.d("$_TAG - downloadCollectionByUserID", "item: $item")
                    val fileName = item.name
                    item.getBytes(MAX_FILE_SIZE_BYTES)
                        .addOnSuccessListener { bytes ->
                            _filesManager.insertFileIntoInternalMemory(bytes, fileName, context)
                        }
                        .addOnFailureListener { exception ->
                            Log.e("$_TAG - downloadCollectionByUserID", "Failed to download item: $item, Exception: $exception")
                        }
                }
            }
//            .addOnFailureListener { exception ->
//                Log.e("$_TAG - downloadCollectionByUserID", "Failed to list items, Exception: $exception")
//            }
    }

    suspend fun uploadFile(file: File, storagePath: String, callback: (Result) -> Unit) {
        Log.d("$_TAG - uploadFile", "Upload to storagePath: $storagePath, File: ${file.name}")
        val fileUri = Uri.fromFile(file)
        val storageRef = _storage.reference
        try {
            val uploadTask = storageRef.child(storagePath)
                .putFile(fileUri)
                .await()
            Log.d("$_TAG - uploadFile", "Upload successful: ${uploadTask.metadata}")
            callback(Result.SUCCESS)
        } catch (e: Exception) {
            Log.e("$_TAG - uploadFile", "Upload failed with exception: $e")
            callback(Result.FAILURE)
        }
    }

    suspend fun downloadFile(path: String, callback: (String?) -> Unit) {
        Log.d("$_TAG - downloadFile", "path: $path")
        val storageRef = _storage.reference
        val fileRef = storageRef.child(path)
        fileRef.downloadUrl
            .addOnSuccessListener {
                callback(it.toString())
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    suspend fun deleteFile(path: String, callback: (Boolean) -> Unit) {
        Log.d("$_TAG - deleteFile", "path: $path")
        val storageRef = _storage.reference
        val fileRef = storageRef.child(path)
        fileRef.delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}