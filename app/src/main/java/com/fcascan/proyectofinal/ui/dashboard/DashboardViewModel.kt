package com.fcascan.proyectofinal.ui.dashboard

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fcascan.proyectofinal.adapters.ItemsAdapter
import com.fcascan.proyectofinal.entities.Category
import com.fcascan.proyectofinal.entities.Group
import com.fcascan.proyectofinal.entities.Item

class DashboardViewModel : ViewModel() {
    private val _TAG = "FCC#DashboardViewModel"

    //LiveData for the View:
    var recViewContent: MutableLiveData<MutableList<ItemsAdapter.ItemObject>?> = MutableLiveData()
    var spinnerCategoriesContent: MutableLiveData<MutableList<String>?> = MutableLiveData()
    var spinnerGroupsContent: MutableLiveData<MutableList<String>?> = MutableLiveData()

    //Lists Copies:
    var categoriesList: MutableList<Category>? = null
    var groupsList: MutableList<Group>? = null

    //Filtering variables:
    private var seachQuery: String = ""
    private var selectedCategoryIndex: Int = 0
    private var selectedGroupIndex: Int = 0
    lateinit var filteredItemsList: MutableList<Item>


    //Public functions for the FragmentView:
    fun onShareClicked(index: Int) {
        Log.d("$_TAG - onShareClicked", "Sharing item ${filteredItemsList[index].title}")
//        TODO()
    }

    fun setSearchQuery(query: String?) {
        Log.d("$_TAG - setSearchQuery", "Query: $query")
        seachQuery = query ?: ""
    }

    fun setSelectedCategory(index: Int) {
        Log.d("$_TAG - setSelectedCategory", "Index: $index")
        selectedCategoryIndex = index
    }

    fun setSelectedGroup(index: Int) {
        Log.d("$_TAG - setSelectedGroup", "Index: $index")
        selectedGroupIndex = index
    }

    //Private functions:
    fun filterRecViewContent(itemsList: MutableList<Item>) {
        val selectedCategory = if(selectedCategoryIndex > 0) categoriesList?.get(selectedCategoryIndex-1) else null
        val selectedGroup = if(selectedGroupIndex > 0) groupsList?.get(selectedGroupIndex-1) else null
        Log.d("$_TAG - filterRecViewContent", "Query: $seachQuery - Category: $selectedCategory - Group: $selectedGroup")
        val filteredList1: MutableList<Item> =
            if (seachQuery.isNotEmpty()) {
                itemsList.filter { it.title!!.contains(seachQuery, ignoreCase = true) || it.description!!.contains(seachQuery, ignoreCase = true)} as MutableList<Item>
            } else {
                itemsList
            }
        val filteredList2: MutableList<Item> =
            if (selectedCategory != null) {
                filteredList1.filter { it.categoryID == selectedCategory.documentId } as MutableList<Item>
            } else {
                filteredList1
            }
        val filteredList3: MutableList<Item> =
            if (selectedGroup != null) {
                filteredList2.filter { it.groupID == selectedGroup.documentId } as MutableList<Item>
            } else {
                filteredList2
            }
        filteredItemsList = filteredList3
        Log.d("$_TAG - filterRecViewContent", "filteredItemsList: $filteredItemsList")
        recViewContent.postValue(convertItemsListToAdapterList(filteredItemsList))
    }

    fun updateAdapterList(itemsList: MutableList<Item>?) {
        Log.d("$_TAG - updateAdapterList", "itemsList: $itemsList")
        if (itemsList.isNullOrEmpty()) {
            Log.d("$_TAG - updateAdapterList", "itemsList is null or empty")
            recViewContent.postValue(MutableList(0) { ItemsAdapter.ItemObject("", "") })
            return
        }
        val convertedList = convertItemsListToAdapterList(itemsList.toMutableList())
        Log.d("$_TAG - updateAdapterList", "convertedList: $convertedList")
        recViewContent.postValue(convertedList)
    }

    fun updateCategoriesList(list: MutableList<Category>?) {
        Log.d("$_TAG - updateCategoriesList", "list: $list")
        categoriesList = list
        updateSpinnerCategories()
    }

    fun updateGroupsList(list: MutableList<Group>?) {
        Log.d("$_TAG - updateGroupsList", "list: $list")
        groupsList = list
        updateSpinnerGroups()
    }

    private fun updateSpinnerCategories() {
        Log.d("$_TAG - updateSpinnerCategories", "Updating Categories Spinner Content")
        spinnerCategoriesContent.postValue(categoriesList?.map { it.name }?.toMutableList())
    }

    private fun updateSpinnerGroups() {
        Log.d("$_TAG - updateSpinnerGroups", "Updating Groups Spinner Content")
        spinnerGroupsContent.postValue(groupsList?.map { it.name }?.toMutableList())
    }

    private fun convertItemsListToAdapterList(itemsList: MutableList<Item>?): MutableList<ItemsAdapter.ItemObject>? {
        Log.d("$_TAG - convertItemsListToAdapterList", "itemsList: $itemsList")
        if (itemsList.isNullOrEmpty()) {
            Log.d("$_TAG - convertItemsListToAdapterList", "itemsList is null or empty")
            return MutableList(0) { ItemsAdapter.ItemObject("", "") }
        }
        return itemsList.map { ItemsAdapter.ItemObject(it.title!!, it.description!!) } as? MutableList<ItemsAdapter.ItemObject>
    }
}