package com.fcascan.proyectofinal.ui.account

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.fcascan.proyectofinal.enums.LoadingState
import com.fcascan.proyectofinal.enums.Result
import com.fcascan.proyectofinal.repositories.AuthManager
import com.fcascan.proyectofinal.repositories.FilesManager
import com.google.android.material.snackbar.Snackbar

class AccountViewModel : ViewModel() {
    private val _TAG = "FCC#AccountViewModel"

    //LiveData for the View:
    private val _logedOut = MutableLiveData<Boolean>()
    val logedOut: MutableLiveData<Boolean> get() = _logedOut

    //Managers:
    private val _authManager = AuthManager()
    private val _filesManager = FilesManager()

    init {
        //Initialize Firebase Auth:
        _authManager.initializeAuth()
    }

    fun onLogoutClicked() {
        Log.d("$_TAG - logout", "Logging out")
        _authManager.signOut()
        setLogedOut(true)
    }

    fun onWipeLocalDataClicked(context: Context) {
        Log.d("$_TAG - wipeLocalData", "Wiping local data")
        _filesManager.wipeLocalFiles(context)
        setLogedOut(true)
    }

    fun onDeleteAccountClicked(onComplete: (Result) -> Unit) {
        Log.d("$_TAG - deleteAccount", "Deleting account")
        _authManager.deleteAccount { result ->
            if (result == Result.SUCCESS) {
                Log.d("$_TAG - deleteAccount", "Account deleted successfully")
                setLogedOut(true)
                onComplete(Result.SUCCESS)
            } else {
                Log.d("$_TAG - deleteAccount", "Error deleting account")
                onComplete(Result.FAILURE)
            }
        }
    }

    fun setLogedOut(logedOut: Boolean) {
        Log.d("$_TAG - setLogedOut", "Setting logedOut: $logedOut")
        _logedOut.postValue(logedOut)
    }
}