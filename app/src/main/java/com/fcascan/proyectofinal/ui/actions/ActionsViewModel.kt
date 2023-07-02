package com.fcascan.proyectofinal.ui.actions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fcascan.proyectofinal.adapters.ActionsAdapter
import com.fcascan.proyectofinal.entities.Category
import com.fcascan.proyectofinal.entities.Group
import com.fcascan.proyectofinal.enums.Actions
import com.fcascan.proyectofinal.enums.ActionsScreen

class ActionsViewModel : ViewModel() {
    private val _className = "FCC#ActionsViewModel"

    private val _currentScreen = MutableLiveData<ActionsScreen>()
    val currentScreen: LiveData<ActionsScreen> get() = _currentScreen

    private var _recViewContent: MutableLiveData<MutableList<ActionsAdapter.ActionsObject>?> = MutableLiveData()
    val recViewContent: LiveData<MutableList<ActionsAdapter.ActionsObject>?> get() = _recViewContent

    //LiveData for the View:
    var categoriesList: MutableList<Category>? = null
    var groupsList: MutableList<Group>? = null

    fun changeRecViewContent() {
        when (currentScreen.value) {
            ActionsScreen.SCREEN_ACTIONS -> {
                setRecViewContent(mutableListOf(
                    ActionsAdapter.ActionsObject(Actions.ACTION1.title, Actions.ACTION1.description),
                    ActionsAdapter.ActionsObject(Actions.ACTION2.title, Actions.ACTION2.description),
                    ActionsAdapter.ActionsObject(Actions.ACTION3.title, Actions.ACTION3.description),
                    ActionsAdapter.ActionsObject(Actions.ACTION4.title, Actions.ACTION4.description)
                ))
            }
            ActionsScreen.SCREEN_CATEGORIES -> {
                setRecViewContent(categoriesList?.map { ActionsAdapter.ActionsObject(it.name, "") }?.toMutableList())
            }
            ActionsScreen.SCREEN_GROUPS -> {
                setRecViewContent(groupsList?.map { ActionsAdapter.ActionsObject(it.name, "") }?.toMutableList())
            }
            else -> { setRecViewContent(mutableListOf())}
        }
    }

    fun updateCategoriesList(list: MutableList<Category>?) {
        categoriesList = list
    }

    fun updateGroupsList(list: MutableList<Group>?) {
        groupsList = list
    }

    fun setCurrentScreen(screen: ActionsScreen) {
        _currentScreen.value = screen
    }

    fun setRecViewContent(content: MutableList<ActionsAdapter.ActionsObject>?) {
        _recViewContent.value = content
    }
}