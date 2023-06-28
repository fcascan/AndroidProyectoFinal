package com.fcascan.proyectofinal.ui.dashboard

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fcascan.proyectofinal.enums.LoadingState
import com.fcascan.proyectofinal.activities.MainActivityViewModel
import com.fcascan.proyectofinal.adapters.ItemsAdapter
import com.fcascan.proyectofinal.entities.Category
import com.fcascan.proyectofinal.entities.Group
import com.fcascan.proyectofinal.entities.Item

class DashboardViewModel : ViewModel() {
    private val _className = "FCC#DashboardViewModel"

    //LiveData for the View:
    var recViewContent: MutableLiveData<MutableList<ItemsAdapter.ItemObject>?> = MutableLiveData()
    var spinnerCategoriesContent: MutableLiveData<MutableList<String>?> = MutableLiveData()
    var spinnerGroupsContent: MutableLiveData<MutableList<String>?> = MutableLiveData()

    //Filtering variables:
    private var seachQuery: String = ""
    private var selectedCategoryName: String = ""
    private var selectedGroupName: String = ""
    lateinit var filteredItemsList: MutableList<Item>


    //Public functions for the FragmentView:
    fun onPlayClicked(index: Int) {
        Log.d("$_className - onPlayClicked", "Playing item ${filteredItemsList[index].title}")
//        TODO()
    }

    fun onStopClicked(index: Int) {
        Log.d("$_className - onStopClicked", "Stopping item ${filteredItemsList[index].title}")
//        TODO()
    }

    fun onShareClicked(index: Int) {
        Log.d("$_className - onShareClicked", "Sharing item ${filteredItemsList[index].title}")
//        TODO()
    }

    fun setSearchQuery(query: String?) {
        Log.d("$_className - setSearchQuery", "Query: $query")
        seachQuery = query ?: ""
    }

    fun setSelectedCategory(name: String?) {
        Log.d("$_className - setSelectedCategory", "Name: $name")
        selectedCategoryName = if (name.isNullOrEmpty() || name == "all") "" else name
    }

    fun setSelectedGroup(name: String?) {
        Log.d("$_className - setSelectedGroup", "Name: $name")
        selectedGroupName = if (name.isNullOrEmpty() || name == "all") "" else name
    }

    //Private functions:
    fun filterRecViewContent(itemsList: MutableList<Item>) {
        Log.d("$_className - filterRecViewContent", "Query: $seachQuery - Category: $selectedCategoryName - Group: $selectedGroupName")
        val filteredList1: MutableList<Item> =
            if (seachQuery.isNotEmpty()) {
                itemsList.filter { it.title!!.contains(seachQuery, ignoreCase = true) || it.description!!.contains(seachQuery, ignoreCase = true)} as MutableList<Item>
            } else {
                itemsList
            }
        val filteredList2: MutableList<Item> =
            if (selectedCategoryName.isNotEmpty()) {
                filteredList1.filter { it.categoryID == selectedCategoryName } as MutableList<Item>
                //TODO() se deberia hacer una busqueda por ID y no por nombre
            } else {
                filteredList1
            }
        val filteredList3: MutableList<Item> =
            if (selectedGroupName.isNotEmpty()) {
                filteredList2.filter { it.groupID == selectedGroupName } as MutableList<Item>
                //TODO() se deberia hacer una busqueda por ID y no por nombre
            } else {
                filteredList2
            }
        filteredItemsList = filteredList3
        Log.d("$_className - filterRecViewContent", "filteredItemsList: $filteredItemsList")
        recViewContent.postValue(convertItemsListToAdapterList(filteredItemsList))
    }

    fun updateAdapterList(itemsList: MutableList<Item>?) {
        Log.d("$_className - updateAdapterList", "itemsList: $itemsList")
        if (itemsList.isNullOrEmpty()) {
            Log.d("$_className - updateAdapterList", "itemsList is null or empty")
            recViewContent.postValue(MutableList(0) { ItemsAdapter.ItemObject("", "") })
            return
        }
        val convertedList = convertItemsListToAdapterList(itemsList.toMutableList())
        Log.d("$_className - updateAdapterList", "convertedList: $convertedList")
        recViewContent.postValue(convertedList)
    }

    fun updateSpinnerCategories(list: MutableList<Category>?) {
        Log.d("$_className - updateSpinnerCategories", "Updating Categories Spinner Content")
        spinnerCategoriesContent.postValue(list?.map { it.name }?.toMutableList())
    }

    fun updateSpinnerGroups(list: MutableList<Group>?) {
        Log.d("$_className - updateSpinnerGroups", "Updating Groups Spinner Content")
        spinnerGroupsContent.postValue(list?.map { it.name }?.toMutableList())
    }

    private fun convertItemsListToAdapterList(itemsList: MutableList<Item>?): MutableList<ItemsAdapter.ItemObject>? {
        Log.d("$_className - convertItemsListToAdapterList", "itemsList: $itemsList")
        if (itemsList.isNullOrEmpty()) {
            Log.d("$_className - convertItemsListToAdapterList", "itemsList is null or empty")
            val convertedList: MutableList<ItemsAdapter.ItemObject> = mutableListOf()
            convertedList.addAll(listOf(
            ItemsAdapter.ItemObject("Harcoded Title1", "Harcoded description1"),
            ItemsAdapter.ItemObject("Harcoded Title2", "Harcoded description2"),
            ItemsAdapter.ItemObject("Harcoded Title3", "Harcoded description3"),
            ItemsAdapter.ItemObject("Harcoded Title4", "Harcoded description4"),
            ItemsAdapter.ItemObject("Harcoded Title5", "Harcoded description5"),
            ItemsAdapter.ItemObject("Harcoded Title6", "Harcoded description6"),
            ItemsAdapter.ItemObject("Harcoded Title7", "Harcoded description7"),
            ))
            return convertedList
        }
        return itemsList.map { ItemsAdapter.ItemObject(it.title!!, it.description!!) } as? MutableList<ItemsAdapter.ItemObject>
    }
}