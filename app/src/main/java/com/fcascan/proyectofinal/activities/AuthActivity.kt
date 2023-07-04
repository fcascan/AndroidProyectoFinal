package com.fcascan.proyectofinal.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import com.fcascan.proyectofinal.constants.AUTH_DAYS_TO_EXPIRE_LOGIN
import com.fcascan.proyectofinal.databinding.ActivityAuthBinding
import com.fcascan.proyectofinal.enums.LoadingState
import com.fcascan.proyectofinal.enums.Result
import com.fcascan.proyectofinal.shared.SharedViewModel
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate

class AuthActivity : AppCompatActivity() {
    private val _TAG = "FCC#AuthActivity"

    //View Elements:
    private lateinit var binding: ActivityAuthBinding

    //ViewModels:
    lateinit var authActivityViewModel: AuthActivityViewModel
    lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //View Binding:
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        //Initialize ViewModels:
        authActivityViewModel = ViewModelProvider(this)[AuthActivityViewModel::class.java]
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

        authActivityViewModel.setProgressBarState(LoadingState.SUCCESS)

        //Checksession:
        checkSession()

        //Login observer:
        authActivityViewModel.logedIn.observe(this) { logedIn ->
            Log.d("$_TAG - onCreate", "logedIn changed: $logedIn")
            if (logedIn != null) {
                if (logedIn) {
                    //Save Shared Preferences:
                    val userIDsharedPref = this.getSharedPreferences("userID", Context.MODE_PRIVATE)
                    val editor = userIDsharedPref.edit()
                    editor.putString("userID", authActivityViewModel.userID)
                    editor.apply()
                    val dateSharedPref = this.getSharedPreferences("date", Context.MODE_PRIVATE)
                    val editorDate = dateSharedPref.edit()
                    editorDate.putString("date", authActivityViewModel.currentDate)
                    editorDate.apply()
                    //Go to MainActivity:
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.d("$_TAG - onCreate", "Login Failed, logedIn: $logedIn")
                    Snackbar.make(binding.root, "Login Failed", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        //Event Listeners:
        authActivityViewModel.progressBar.observe(this) { state ->
            Log.d(_TAG, "progressBarAuthActivity changed: $state")
            when (state) {
                LoadingState.LOADING -> {
                    binding.loading.visibility = ProgressBar.VISIBLE
                }
                LoadingState.SUCCESS -> {
                    binding.loading.visibility = ProgressBar.INVISIBLE
                }
                else -> {
                    binding.loading.visibility = ProgressBar.INVISIBLE
                    Snackbar.make(binding.root, "FAILURE LOADING", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        binding.btnSignIn.setOnClickListener {
            Log.d("$_TAG - onCreate", "btnSignIn.setOnClickListener")
            authActivityViewModel.setProgressBarState(LoadingState.LOADING)
            if (binding.txtEmail.text.toString().isEmpty() ||
                binding.txtPass.text.toString().isEmpty()) {
                Snackbar.make(binding.root, "Please Complete All Fields", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            authActivityViewModel.onSignInClicked(
                binding.txtEmail.text.toString(),
                binding.txtPass.text.toString()
            ) { result ->
                if (result == Result.SUCCESS) {
                    Log.d("$_TAG - onCreate", "signInUser: SUCCESS")
                    authActivityViewModel.setProgressBarState(LoadingState.SUCCESS)
                    Snackbar.make(binding.root, "SUCCESS SIGN IN", Snackbar.LENGTH_LONG).show()
                    redirect()
                } else {
                    Log.d("$_TAG - onCreate", "signInUser: FAILURE")
                    authActivityViewModel.setProgressBarState(LoadingState.FAILURE)
                    Snackbar.make(binding.root, "FAILURE SIGN IN", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            Log.d("$_TAG - onCreate", "btnRegister.setOnClickListener")
            authActivityViewModel.setProgressBarState(LoadingState.LOADING)
            if (binding.txtEmail.text.toString().isEmpty() ||
                binding.txtPass.text.toString().isEmpty()) {
                Snackbar.make(binding.root, "Please Complete All Fields", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            authActivityViewModel.onRegisterClicked(
                binding.txtEmail.text.toString(),
                binding.txtPass.text.toString()
            ) { result ->
                if (result == Result.SUCCESS) {
                    Log.d("$_TAG - onCreate", "signUpUser: SUCCESS")
                    authActivityViewModel.setProgressBarState(LoadingState.SUCCESS)
                    Snackbar.make(binding.root, "SUCCESS SIGN UP", Snackbar.LENGTH_LONG).show()
                    redirect()
                } else {
                    Log.d("$_TAG - onCreate", "signUpUser: FAILURE")
                    authActivityViewModel.setProgressBarState(LoadingState.FAILURE)
                    Snackbar.make(binding.root, "FAILURE SIGN UP", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and redirect.
        if (authActivityViewModel.getCurrentUser() != null) {
            redirect()
        }
    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    private fun checkSession() {
        Log.d("$_TAG - checkSession", "Checking if user is already logged in")
        val userIDsharedPref = this.getSharedPreferences("userID", Context.MODE_PRIVATE)
        val dateSharedPref = this.getSharedPreferences("date", Context.MODE_PRIVATE)
        Log.d("$_TAG - checkSession", "userIDsharedPref: ${userIDsharedPref.getString("userID", "")}")
        Log.d("$_TAG - checkSession", "dateSharedPref: ${dateSharedPref.getString("date", "")}")

        //Check if user is already logged in:
        val storedDate = dateSharedPref.getString("date", null)
        if (userIDsharedPref != null && storedDate != null &&
            LocalDate.now().minusDays(AUTH_DAYS_TO_EXPIRE_LOGIN).isBefore(LocalDate.parse(storedDate))) {
            Log.d("$_TAG - checkSession", "User is already logged in")
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("userID", userIDsharedPref.getString("userID", ""))
            }
            sharedViewModel.setUserID(userIDsharedPref.getString("userID", "")!!)
            startActivity(intent)
        } else {
            Log.d("$_TAG - checkSession", "User is not logged in")
        }
    }

    private fun redirect() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}