package com.fcascan.proyectofinal.repositories

import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class StorageManager {
    private val _className = "FCC#StorageManager"

    private val _storage = Firebase.storage

    fun uploadFile(path: String, callback: (String?) -> Unit) {
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

    fun downloadFile(path: String, callback: (String?) -> Unit) {
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

    fun deleteFile(path: String, callback: (Boolean) -> Unit) {
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