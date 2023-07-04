package com.fcascan.proyectofinal.activities

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fcascan.proyectofinal.entities.Category
import com.fcascan.proyectofinal.entities.Group
import com.fcascan.proyectofinal.repositories.AuthManager

class FileReceiverViewModel : ViewModel() {
    private val _TAG = "FCC#FileReceiverViewModel"

    //Managers:
    private val _authManager = AuthManager()

    init {
        //Initialize Firebase Auth:
        _authManager.initializeAuth()
    }

    //LiveData for the View:
    var spinnerCategoriesContent: MutableLiveData<MutableList<String>?> = MutableLiveData()
    var spinnerGroupsContent: MutableLiveData<MutableList<String>?> = MutableLiveData()

    fun updateSpinnerCategories(list: MutableList<Category>?) {
        Log.d("$_TAG - updateSpinnerCategories", "Updating Categories Spinner Content")
        spinnerCategoriesContent.postValue(list?.map { it.name }?.toMutableList())
    }

    fun updateSpinnerGroups(list: MutableList<Group>?) {
        Log.d("$_TAG - updateSpinnerGroups", "Updating Groups Spinner Content")
        spinnerGroupsContent.postValue(list?.map { it.name }?.toMutableList())
    }
}