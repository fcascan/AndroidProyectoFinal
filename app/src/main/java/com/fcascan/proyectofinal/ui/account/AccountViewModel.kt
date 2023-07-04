package com.fcascan.proyectofinal.ui.account

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fcascan.proyectofinal.repositories.AuthManager

class AccountViewModel : ViewModel() {
    private val _TAG = "FCC#AccountViewModel"

    //LiveData for the View:
    private val _logedOut = MutableLiveData<Boolean>()
    val logedOut: MutableLiveData<Boolean> get() = _logedOut

    //Managers:
    private val _authManager = AuthManager()

    init {
        //Initialize Firebase Auth:
        _authManager.initializeAuth()
    }

    fun logout() {
        Log.d("$_TAG - logout", "Logging out")
        _authManager.signOut()
        setLogedOut(true)
    }

    fun setLogedOut(logedOut: Boolean) {
        Log.d("$_TAG - setLogedOut", "Setting logedOut: $logedOut")
        _logedOut.postValue(logedOut)
    }
}