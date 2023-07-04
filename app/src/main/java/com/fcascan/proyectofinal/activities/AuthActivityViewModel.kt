package com.fcascan.proyectofinal.activities

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fcascan.proyectofinal.enums.LoadingState
import com.fcascan.proyectofinal.enums.Result
import com.fcascan.proyectofinal.repositories.AuthManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.LocalDate

class AuthActivityViewModel : ViewModel() {
    private val _TAG = "FCC#AuthActivityViewModel"

    //LiveData for the View:
    private val _progressBar = MutableLiveData<LoadingState>()
    val progressBar: MutableLiveData<LoadingState> get() = _progressBar

    private val _userID = MutableLiveData<String>()
    val userID: MutableLiveData<String> get() = _userID

    private val _currentDate = MutableLiveData<String>()
    val currentDate: MutableLiveData<String> get() = _currentDate

    //Managers:
    private val _authManager = AuthManager()

    init {
        //Initialize Firebase Auth:
        _authManager.initializeAuth()
    }

    fun setProgressBarState(state: LoadingState) {
        Log.d("$_TAG - setProgressBarState", "Setting progressBarState: $state")
        _progressBar.postValue(state)
    }

    fun getCurrentUser() = _authManager.getCurrentUser()

    fun onSignInClicked(email: String, password: String, onComplete: (Result) -> Unit) {
        Log.d("$_TAG - onSignInClicked", "email: $email")
        _authManager.signInWithEmailAndPassword(email, password) { userID ->
            if (userID.isNotEmpty()) {
                setUserID(userID)
                setCurrentDate(LocalDate.now())
                onComplete(Result.SUCCESS)
            } else onComplete(Result.FAILURE)
        }
    }

    fun onRegisterClicked(email: String, password: String, onComplete: (Result) -> Unit) {
        Log.d("$_TAG - onRegisterClicked", "email: $email")
        _authManager.createUserWithEmailAndPassword(email, password) { userID ->
            if (userID.isNotEmpty()) {
                setUserID(userID)
                setCurrentDate(LocalDate.now())
                onComplete(Result.SUCCESS)
            } else onComplete(Result.FAILURE)
        }
    }

    fun setUserID(id: String) {
        Log.d("$_TAG - setUserID", "Setting userID: $id")
        _userID.postValue(id)
    }

    fun setCurrentDate(date: LocalDate) {
        Log.d("$_TAG - setCurrentDate", "Setting currentDate: $date")
        _currentDate.postValue(date.toString())
    }

    fun setCurrentDate(dateString: String) {
        Log.d("$_TAG - setCurrentDate", "Setting currentDate: $dateString")
        _currentDate.postValue(dateString)
    }
}