package com.fcascan.proyectofinal.shared

import android.content.Context
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
import com.fcascan.proyectofinal.enums.PlaybackState
import com.fcascan.proyectofinal.enums.Result
import com.fcascan.proyectofinal.repositories.PlaybackManager
import com.fcascan.proyectofinal.repositories.FilesManager
import com.fcascan.proyectofinal.repositories.FirestoreManager
import com.fcascan.proyectofinal.repositories.StorageManager
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class SharedViewModel : ViewModel() {
    private val _className = "FCC#SharedViewModel"

    //Repositories:
    private val _firestoreManager = FirestoreManager()
    private val _storageManager = StorageManager()
    private val _playbackManager = PlaybackManager()
    private val _filesManager = FilesManager()


    //LiveData for the Views:
    private val _itemsList = MutableLiveData<MutableList<Item>>()
    val itemsList: LiveData<MutableList<Item>> get() = _itemsList

    private val _categoriesList = MutableLiveData<MutableList<Category>>()
    val categoriesList: LiveData<MutableList<Category>> get() = _categoriesList

    private val _groupsList = MutableLiveData<MutableList<Group>>()
    val groupsList: LiveData<MutableList<Group>> get() = _groupsList

    private val _screenState = MutableLiveData<LoadingState>()
    val screenState: LiveData<LoadingState> get() = _screenState

    private val _playbackState = MutableLiveData<PlaybackState>()
    val playbackState: LiveData<PlaybackState> get() = _playbackState

    private val _selectedFileUri = MutableLiveData<Uri?>()
    val selectedFileUri: LiveData<Uri?> get() = _selectedFileUri

    val confirmationDialogResponse: MutableLiveData<Boolean> = MutableLiveData()

    //User Detail:
    private var _userID: String = ""
    val userID: String get() = _userID


    //One-time Methods:
    fun initiateApp(context: Context, onComplete: (Result) -> Unit) = viewModelScope.launch {
        val userID = getUser()
        try {
            setItemsList(_firestoreManager.getCollectionByUserID(userID, ITEMS_COLLECTION) as MutableList<Item>)
            setCategoriesList(_firestoreManager.getCollectionByUserID(userID, CATEGORIES_COLLECTION) as MutableList<Category>)
            setGroupsList(_firestoreManager.getCollectionByUserID(userID, GROUPS_COLLECTION) as MutableList<Group>)
            _storageManager.downloadCollectionByUserID(userID, context)
            onComplete(Result.SUCCESS)
        } catch (e: Exception) {
            Log.e("$_className - initiateApp", "Exception: $e")
            onComplete(Result.FAILURE)
        }
    }


    //Public Methods:
    fun setUser(userID: String) {
        _userID = userID
    }

    fun getUser(): String {
        return _userID
    }

    fun setProgressBarState(screenState: LoadingState) {
        _screenState.value = screenState
    }

    fun saveItemOnEverywhere(item: Item, context: Context, onComplete: (Result) -> Unit) = viewModelScope.launch {
        Log.d("$_className - saveItemOnEverywhere", "Saving item on everywhere...")
        //        TODO()
        //1) Guardar/Pisar como Item en la base de datos y recuperar el ID del documento
        _firestoreManager.addObjectToCollection(item, ITEMS_COLLECTION) { result ->
            if (result == Result.FAILURE) {
                onComplete(result)
            }
        }

        //2) Guardar en Storage con el nombre del ID del documento
//        _storageManager.uploadFile(item.documentId, )

        //3) Guardar el archivo de audio en la carpeta de la app
//        _filesManager.insertFileIntoInternalMemory(byteArray, item.documentId, context)

        //4) Volver a la pantalla anterior

    }

    fun updateItem(item: Item, context: Context, onComplete: (Result) -> Unit) = viewModelScope.launch {
        _firestoreManager.updateObjectInCollection(item, ITEMS_COLLECTION) { result ->
            onComplete(result)
        }
    }

    fun wipeItemFromEverywhere(itemID: String, context: Context, onComplete: (Result) -> Unit) = viewModelScope.launch {
        Log.d("$_className - wipeItemFromEverywhere", "Wiping item from everywhere...")
        //        TODO()
        //1) Borrar el item de la base de datos
        //2) Borrar el archivo de audio de la carpeta de la app
        _filesManager.deleteFileFromInternalMemory(itemID, context)
        //3) Borrar el archivo de audio de Storage
        //4) Volver a la pantalla anterior
//        findNavController().navigateUp()
        onComplete(Result.SUCCESS)
    }


    //MediaPlayer Methods:
    fun playFile(fileName: String, context: Context, onPlaybackComplete: () -> Unit) = viewModelScope.launch {
        Log.d("$_className - playFile", "Playing file: $fileName")
        setPlaybackState(PlaybackState.PLAYING)
        _playbackManager.playContentUri(fileName, context) {
            setPlaybackState(PlaybackState.STOPPED)
            onPlaybackComplete()
        }
    }

    fun pausePlayback() {
        Log.d("$_className - pausePlayback", "Pause playback clicked")
        setPlaybackState(PlaybackState.PAUSED)
        _playbackManager.pausePlayback()
    }

    fun stopPlayback() {
        Log.d("$_className - stopPlayback", "Stop playback clicked")
        setPlaybackState(PlaybackState.STOPPED)
        _playbackManager.stopPlayback()
    }

    fun destroyMediaPlayer() {
        _playbackManager.destroyMediaPlayer()
    }


    //Lists Methods:
    fun getCategoryIndexByID(categoryId: String?): Int {
        return categoriesList.value?.indexOf(categoryId?.let { getCategoryById(it) }) ?: 0
    }

    fun getGroupIndexByID(groupId: String?): Int {
        return groupsList.value?.indexOf(groupId?.let { getGroupById(it) }) ?: 0
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

    fun getCategoryIdByIndex(index: Int): String? {
        return categoriesList.value?.get(index)?.documentId
    }

    fun getGroupIdByIndex(index: Int): String? {
        return groupsList.value?.get(index)?.documentId
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

    private fun setPlaybackState(playbackState: PlaybackState) {
        _playbackState.value = playbackState
    }
}