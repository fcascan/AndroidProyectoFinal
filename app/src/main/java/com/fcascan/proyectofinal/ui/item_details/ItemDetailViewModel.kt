package com.fcascan.proyectofinal.ui.item_details

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fcascan.proyectofinal.entities.Category
import com.fcascan.proyectofinal.entities.Group

class ItemDetailViewModel : ViewModel() {
    private val _className = "FCC#ItemDetailViewModel"

    //LiveData for the View:
    var spinnerCategoriesContent: MutableLiveData<MutableList<String>?> = MutableLiveData()
    var spinnerGroupsContent: MutableLiveData<MutableList<String>?> = MutableLiveData()

    fun updateSpinnerCategories(list: MutableList<Category>?) {
        Log.d("$_className - updateSpinnerCategories", "Updating Categories Spinner Content")
        spinnerCategoriesContent.postValue(list?.map { it.name }?.toMutableList())
    }

    fun updateSpinnerGroups(list: MutableList<Group>?) {
        Log.d("$_className - updateSpinnerGroups", "Updating Groups Spinner Content")
        spinnerGroupsContent.postValue(list?.map { it.name }?.toMutableList())
    }
}