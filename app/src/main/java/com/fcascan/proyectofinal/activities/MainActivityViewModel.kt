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
    private val _className = "FCC#MainActivityViewModel"

    //Firebase & Storage:
    private val _db = Firebase.firestore
    private val _storage = Firebase.storage

    //LiveData Variables:
    val screenState = MutableLiveData<LoadingState>()
    var itemsList: MutableLiveData<MutableList<Item>> = MutableLiveData()
    var categoriesList: MutableLiveData<MutableList<Category>> = MutableLiveData()
    var groupsList: MutableLiveData<MutableList<Group>> = MutableLiveData()

    private val _selectedFileUri = MutableLiveData<Uri?>()
    val selectedFileUri: LiveData<Uri?> get() = _selectedFileUri


    //Public functions:
    fun updateProgressBar(state : LoadingState) {
        screenState.postValue(state)
    }

    fun updateAllCollectionsLists(userID: String) = viewModelScope.launch {
        itemsList.postValue(getCollectionByUserID(userID, ITEMS_COLLECTION) as MutableList<Item>)
        categoriesList.postValue(getCollectionByUserID(userID, CATEGORIES_COLLECTION) as MutableList<Category>)
        groupsList.postValue(getCollectionByUserID(userID, GROUPS_COLLECTION) as MutableList<Group>)
    }

    //Files Management Functions:
    fun setSelectedFileUri(fileUri: Uri?) {
        _selectedFileUri.value = fileUri
    }

    fun resetSelectedFileUri() {
        _selectedFileUri.value = null
    }

    fun retrieveURIfromFile(userID: String, collection: String, fileName: String) = viewModelScope.launch {
        val storageRef = _storage.reference
        val fileRef = storageRef.child("$userID/$collection/$fileName")
        val uri = fileRef.downloadUrl.await()
        Log.d("$_className - retrieveURIfromFile", "URI: $uri")
    }

    //Coroutines:
    private suspend fun getCollectionByUserID(userID: String, collection: String): MutableList<out Any>? {
        //TODO: Revisar como hacer para traer los IDS autogenerados de los documentos, y no los campos de IDS
        Log.d("$_className - getCollectionByUserID", "Retrieving $collection data...")
        updateProgressBar(LoadingState.LOADING)
        try {
            val classType = when (collection) {
                ITEMS_COLLECTION -> Item::class.java
                CATEGORIES_COLLECTION -> Category::class.java
                GROUPS_COLLECTION -> Group::class.java
                else -> throw Exception("Invalid collection name")
            }
            val data = _db.collection(collection)
                .whereEqualTo("userID", userID)
                .get()
                .await()
                .toObjects(classType)
            Log.d("$_className - getCollectionByUserID", "$collection data retrieved: ${data}")
            updateProgressBar(LoadingState.SUCCESS)
            return data
        } catch (e: Exception) {
            updateProgressBar(LoadingState.FAILURE)
            Log.d("$_className - getCollectionByUserID", "Error Message: ${e.message}")
        }
        return null
    }
}