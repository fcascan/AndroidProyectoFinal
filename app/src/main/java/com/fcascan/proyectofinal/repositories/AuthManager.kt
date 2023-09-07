package com.fcascan.proyectofinal.repositories

import android.util.Log
import com.fcascan.proyectofinal.enums.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthManager {
    private val _TAG = "FCC#AuthManager"

    //Auth instance:
    //https://firebase.google.com/docs/auth/android/start?hl=es-419
    private lateinit var auth: FirebaseAuth

    fun initializeAuth() {
        auth = Firebase.auth
    }

    fun getCurrentUser() = auth.currentUser

    fun createUserWithEmailAndPassword(email: String, password: String, onComplete: (String) -> Unit) {
        Log.d("$_TAG - createNewUser", "email: $email")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("$_TAG - createUserWithEmailAndPassword", "createUser success")
                    onComplete(auth.currentUser?.uid ?: "")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("$_TAG - createUserWithEmailAndPassword", "createUser failure", task.exception)
                    onComplete("")
                }
            }
            .addOnFailureListener {exception ->
                Log.d("$_TAG - createUserWithEmailAndPassword", "createUser failure", exception)
                onComplete("")
            }
    }

    fun signInWithEmailAndPassword(email: String, password: String, onComplete: (String) -> Unit) {
        Log.d("$_TAG - signInWithEmailAndPassword", "signInUser: $email")
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("$_TAG - signInWithEmailAndPassword", "signInUser success")
                    onComplete(auth.currentUser?.uid ?: "")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("$_TAG - signInWithEmailAndPassword", "signInUser failure", task.exception)
                    onComplete("")
                }
            }
            .addOnFailureListener {exception ->
                Log.d("$_TAG - signInWithEmailAndPassword", "signInUser failure", exception)
                onComplete("")
            }
    }

    fun signOut() {
        auth.signOut()
    }

    fun deleteAccount(onComplete: (Result) -> Unit) {
        auth.currentUser?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("$_TAG - deleteAccount", "User account deleted.")
                    onComplete(Result.SUCCESS)
                } else {
                    Log.d("$_TAG - deleteAccount", "User account deletion failed.")
                    onComplete(Result.FAILURE)
                }
            }
    }
}