package com.fcascan.proyectofinal.shared

import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fcascan.proyectofinal.constants.ITEMS_COLLECTION
import com.fcascan.proyectofinal.constants.CATEGORIES_COLLECTION
import com.fcascan.proyectofinal.constants.GROUPS_COLLECTION
import com.fcascan.proyectofinal.entities.Category
import com.fcascan.proyectofinal.entities.Group
import com.fcascan.proyectofinal.entities.Item
import com.fcascan.proyectofinal.enums.LoadingState
import com.fcascan.proyectofinal.repositories.FirestoreManager
import com.fcascan.proyectofinal.repositories.StorageManager
import kotlinx.coroutines.launch


class SharedViewModel : ViewModel() {
    private val _className = "FCC#SharedViewModel"

    //LiveData for the Views:
    private val _itemsList = MutableLiveData<MutableList<Item>>()
    val itemsList: LiveData<MutableList<Item>> get() = _itemsList

    private val _categoriesList = MutableLiveData<MutableList<Category>>()
    val categoriesList: LiveData<MutableList<Category>> get() = _categoriesList

    private val _groupsList = MutableLiveData<MutableList<Group>>()
    val groupsList: LiveData<MutableList<Group>> get() = _groupsList

    private val _screenState = MutableLiveData<LoadingState>()
    val screenState: LiveData<LoadingState> get() = _screenState

    private val _selectedFileUri = MutableLiveData<Uri?>()
    val selectedFileUri: LiveData<Uri?> get() = _selectedFileUri

    //Repositories:
    private val _firestoreManager = FirestoreManager()
    private val _storageManager = StorageManager()

    //MediaPlayer variables:
    private var mediaPlayer: MediaPlayer = MediaPlayer()
    private var isPlaying: Boolean = false


    //One-time Methods:
    fun updateAllCollectionsLists(userID: String) = viewModelScope.launch {
        setItemsList(_firestoreManager.getCollectionByUserID(userID, ITEMS_COLLECTION) as MutableList<Item>)
        setCategoriesList(_firestoreManager.getCollectionByUserID(userID, CATEGORIES_COLLECTION) as MutableList<Category>)
        setGroupsList(_firestoreManager.getCollectionByUserID(userID, GROUPS_COLLECTION) as MutableList<Group>)
    }


    //Public Methods:
    fun setProgressBarState(screenState: LoadingState) {
        _screenState.value = screenState
    }

    fun playFile(fileName: String) {
//        TODO()
        Log.d("$_className - playFile", "Filename: $fileName")

//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
          mediaPlayer.setDataSource(fileName)
          mediaPlayer.prepare()
          mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCategoryIndexByID(categoryId: String?): Int {
        return categoriesList.value?.indexOf(categoryId?.let { getCategoryById(it) }) ?: 0
    }

    fun getGroupIndexByID(groupId: String?): Int {
        return groupsList.value?.indexOf(groupId?.let { getGroupById(it) }) ?: 0
    }

    fun stopPlayback() {
//        TODO()
        Log.d("$_className - stopPlayback", "Stopping playback...")
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.release()
                isPlaying = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun destroyMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop()
            mediaPlayer.reset()
            mediaPlayer.release()
        }
    }

    fun getItemById(itemId: String): Item? {
        return itemsList.value?.find { it.documentId == itemId }
    }

    fun getCategoryById(categoryId: String): Category? {
        return categoriesList.value?.find { it.documentId == categoryId }
    }

    fun getGroupById(groupId: String): Group? {
        return groupsList.value?.find { it.documentId == groupId }
    }

    fun getItemsList(): MutableList<Item>? {
        return itemsList.value
    }

    fun getCategoriesList(): MutableList<Category>? {
        return categoriesList.value
    }

    fun getGroupsList(): MutableList<Group>? {
        return groupsList.value
    }


    //Private Methods:
    private fun setItemsList(items: MutableList<Item>) {
        _itemsList.value = items
    }
    private fun setCategoriesList(categories: MutableList<Category>) {
        _categoriesList.value = categories
    }
    private fun setGroupsList(groups: MutableList<Group>) {
        _groupsList.value = groups
    }



//    fun retrieveItemsList(): MutableList<Item>? {
//        mainActivityViewModel.itemsList.observe(lifecycleOwner) {
//            itemsList = it
//        }
//    }



}