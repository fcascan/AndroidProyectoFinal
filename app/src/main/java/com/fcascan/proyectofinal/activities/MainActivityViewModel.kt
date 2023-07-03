package com.fcascan.proyectofinal.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fcascan.proyectofinal.constants.CATEGORIES_COLLECTION
import com.fcascan.proyectofinal.constants.GROUPS_COLLECTION
import com.fcascan.proyectofinal.constants.ITEMS_COLLECTION
import com.fcascan.proyectofinal.entities.Category
import com.fcascan.proyectofinal.entities.Group
import com.fcascan.proyectofinal.entities.Item
import com.fcascan.proyectofinal.enums.LoadingState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class MainActivityViewModel : ViewModel() {
    private val _TAG = "FCC#MainActivityViewModel"


    //Files Management Functions:
//    fun setSelectedFileUri(fileUri: Uri?) {
//        _selectedFileUri.value = fileUri
//    }

//    fun resetSelectedFileUri() {
//        _selectedFileUri.value = null
//    }

//    fun retrieveURIfromFile(userID: String, collection: String, fileName: String) = viewModelScope.launch {
//        val storageRef = _storage.reference
//        val fileRef = storageRef.child("$userID/$collection/$fileName")
//        val uri = fileRef.downloadUrl.await()
//        Log.d("$_TAG - retrieveURIfromFile", "URI: $uri")
//    }


}