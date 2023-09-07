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
import com.google.firebase.installations.Utils
import kotlinx.coroutines.launch
import java.io.File
import kotlin.jvm.internal.Intrinsics.Kotlin


class SharedViewModel : ViewModel() {
    private val _TAG = "FCC#SharedViewModel"

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
        Log.d("$_TAG - initiateApp", "Starting App...")
        try {
            setItemsList(_firestoreManager.getCollectionByUserID(userID, ITEMS_COLLECTION) as MutableList<Item>)
            setCategoriesList(_firestoreManager.getCollectionByUserID(userID, CATEGORIES_COLLECTION) as MutableList<Category>)
            setGroupsList(_firestoreManager.getCollectionByUserID(userID, GROUPS_COLLECTION) as MutableList<Group>)
            _storageManager.downloadCollectionByUserID(userID, context)
            _filesManager.checkFoldersExistence(File(context.filesDir, ""))
            onComplete(Result.SUCCESS)
        } catch (e: Exception) {
            Log.e("$_TAG - initiateApp", "Exception: $e")
            onComplete(Result.FAILURE)
        }
    }


    //Public Methods:
    fun setUserID(userID: String) {
        _userID = userID
    }

    fun setProgressBarState(screenState: LoadingState) {
        _screenState.value = screenState
    }

    fun saveReceivedItemOnEverywhere(item: Item, receivedFile: File, onComplete: (Result) -> Unit) = viewModelScope.launch {
        Log.d("$_TAG - saveReceivedItemOnEverywhere", "Saving item on everywhere...")
        var documentID: String? = null
        //1) Save as new Item in Firebase and get the document ID
        _firestoreManager.addObjectToCollection(item, ITEMS_COLLECTION) { result, docID ->
            if (result == Result.FAILURE || docID == null) {
                Log.e("$_TAG - saveReceivedItemOnEverywhere", "Error saving item in Firestore")
                onComplete(result)
            } else {
                Log.d("$_TAG - saveReceivedItemOnEverywhere", "Item saved successfully with documentID: $docID")
                documentID = docID
            }
        }

        //2) Change audio file name with the documentID and move it to the audios folder
        val newFile = File(receivedFile, "audios/$documentID.opus")
        _filesManager.renameAndCopyFile(receivedFile, newFile)

        //3) Then save the file in Storage with userID as Folder:
        val storagePath = "${userID}/$documentID.opus"
        _storageManager.uploadFile(newFile, storagePath) { result ->
            if (result == Result.FAILURE) {
                Log.e("$_TAG - saveReceivedItemOnEverywhere", "Error saving file in Storage")
                onComplete(result)
            } else {
                Log.d("$_TAG - saveReceivedItemOnEverywhere", "File saved successfully in Storage")
                onComplete(result)
            }
        }
        //4) Refresh the lists and then NavigateUp
    }

    fun saveRecordedItemOnEverywhere(item: Item, rootDirectory: File, onComplete: (Result) -> Unit) = viewModelScope.launch {
        Log.d("$_TAG - saveRecordedItemOnEverywhere", "Saving item on everywhere...")
        var documentID: String? = null
        //1) Save as new Item in Firebase and get the document ID
        _firestoreManager.addObjectToCollection(item, ITEMS_COLLECTION) { result, docID ->
            if (result == Result.FAILURE || docID == null) {
                Log.e("$_TAG - saveRecordedItemOnEverywhere", "Error saving item in Firestore")
                onComplete(result)
            } else {
                Log.d("$_TAG - saveRecordedItemOnEverywhere", "Item saved successfully with documentID: $docID")
                documentID = docID
            }
        }

        //2) Change audio file name with the documentID and move it to the audios folder
        val file = File(rootDirectory, "recordings/recorded_audio.aac")
        val newFile = File(rootDirectory, "audios/$documentID.aac")
        _filesManager.renameAndCopyFile(file, newFile)

        //3) Then save the file in Storage with userID as Folder:
        val storagePath = "${userID}/$documentID.opus"
        _storageManager.uploadFile(newFile, storagePath) { result ->
            if (result == Result.FAILURE) {
                Log.e("$_TAG - saveRecordedItemOnEverywhere", "Error saving file in Storage")
                onComplete(result)
            } else {
                Log.d("$_TAG - saveRecordedItemOnEverywhere", "File saved successfully in Storage")
                onComplete(result)
            }
        }
        //4) Refresh the lists and then NavigateUp
    }

    fun updateItem(item: Item, onComplete: (Result) -> Unit) = viewModelScope.launch {
        _firestoreManager.updateObjectInCollection(item, ITEMS_COLLECTION) { result ->
            onComplete(result)
        }
    }

    fun wipeItemFromEverywhere(itemID: String, context: Context, onComplete: (Result) -> Unit) = viewModelScope.launch {
        Log.d("$_TAG - wipeItemFromEverywhere", "Wiping item from everywhere...")
        //        TODO()
        //1) Borrar el item de la base de datos
        _firestoreManager.deleteObjectFromCollection(itemID, ITEMS_COLLECTION) { result ->
            if (result == Result.FAILURE) {
                Log.e("$_TAG - wipeItemFromEverywhere", "Error deleting item from Firestore")
                onComplete(result)
            } else Log.d("$_TAG - wipeItemFromEverywhere", "Item deleted successfully from Firestore")
        }

        //2) Borrar el archivo de audio de la carpeta de la app
        _filesManager.deleteFileFromInternalMemory(itemID, context)

        //3) Borrar el archivo de audio de Storage
        _storageManager.deleteFileFromStorage("$userID/$itemID.opus") { result ->
            if (result == Result.FAILURE) {
                Log.e("$_TAG - wipeItemFromEverywhere", "Error deleting file from Storage")
                onComplete(result)
            } else {
                //4) Volver a la pantalla anterior
                Log.d("$_TAG - wipeItemFromEverywhere", "File deleted successfully from Storage")
                onComplete(Result.SUCCESS)
            }
        }
    }


    //MediaPlayer Methods:
    fun playFile(file: File, onPlaybackComplete: () -> Unit) = viewModelScope.launch {
        Log.d("$_TAG - playFile", "Playing file: ${file.absolutePath}")
        setPlaybackState(PlaybackState.PLAYING)
        _playbackManager.playContentUri(file) {
            setPlaybackState(PlaybackState.STOPPED)
            onPlaybackComplete()
        }
    }

    fun pausePlayback() {
        Log.d("$_TAG - pausePlayback", "Pause playback clicked")
        setPlaybackState(PlaybackState.PAUSED)
        _playbackManager.pausePlayback()
    }

    fun stopPlayback() {
        Log.d("$_TAG - stopPlayback", "Stop playback clicked")
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