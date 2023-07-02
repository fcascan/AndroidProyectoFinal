package com.fcascan.proyectofinal.repositories

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.fcascan.proyectofinal.constants.MAX_FILE_SIZE_BYTES
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

class StorageManager {
    private val _className = "FCC#StorageManager"

    private val _storage = Firebase.storage
    private val _filesManager = FilesManager()

    suspend fun downloadCollectionByUserID(userID: String, context: Context) {
        //path: /{userID}/{itemID}.opus
        Log.d("$_className - downloadCollectionByUserID", "Downloading...")
        _storage.reference
            .child(userID)
            .listAll()
            .await().also{ listResult ->
                listResult.items.forEach { item ->
                    Log.d("$_className - downloadCollectionByUserID", "item: $item")
                    val fileName = item.name
                    item.getBytes(MAX_FILE_SIZE_BYTES)
                        .addOnSuccessListener { bytes ->
                            _filesManager.insertFileIntoInternalMemory(bytes, fileName, context)
                        }
                        .addOnFailureListener { exception ->
                            Log.e("$_className - downloadCollectionByUserID", "Failed to download item: $item, Exception: $exception")
                        }
                }
            }
//            .addOnFailureListener { exception ->
//                Log.e("$_className - downloadCollectionByUserID", "Failed to list items, Exception: $exception")
//            }
    }

    suspend fun uploadFile(path: String, callback: (String?) -> Unit) {
        Log.d("$_className - uploadFile", "path: $path")
        val storageRef = _storage.reference
        val fileRef = storageRef.child(path)
        fileRef.putFile(path.toUri())
            .addOnSuccessListener {
                callback(it.metadata?.path)
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    suspend fun downloadFile(path: String, callback: (String?) -> Unit) {
        Log.d("$_className - downloadFile", "path: $path")
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
        Log.d("$_className - deleteFile", "path: $path")
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