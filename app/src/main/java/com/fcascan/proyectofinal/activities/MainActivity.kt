package com.fcascan.proyectofinal.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.fcascan.proyectofinal.enums.LoadingState
import com.fcascan.proyectofinal.R
import com.fcascan.proyectofinal.constants.AUTH_DAYS_TO_EXPIRE_LOGIN
import com.fcascan.proyectofinal.databinding.ActivityMainBinding
import com.fcascan.proyectofinal.enums.Result
import com.fcascan.proyectofinal.shared.SharedViewModel
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private val _TAG = "FCC#MainActivity"

    //View Elements:
    private lateinit var binding: ActivityMainBinding
    private lateinit var progressBarMainActivity: ProgressBar

    //ViewModels:
    lateinit var mainActivityViewModel: MainActivityViewModel
    lateinit var sharedViewModel: SharedViewModel

    companion object {
        private const val REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //View Binding:
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        //Initialize ViewModels:
        mainActivityViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

        //Set userID:
        val userIDsharedPref = this.getSharedPreferences("userID", Context.MODE_PRIVATE)
        Log.d(_TAG, "userIDsharedPref: ${userIDsharedPref.getString("userID", "")}")
        sharedViewModel.setUserID(userIDsharedPref.getString("userID", "")!!)

        //Checkpermissions:
        checkPermissions()

        //BottomBar:
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_actions, R.id.navigation_dashboard, R.id.navigation_account
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //ProgressBar:
        progressBarMainActivity = binding.root.findViewById(R.id.progressBarMainActivity)
        sharedViewModel.screenState.observe(this) { state ->
            Log.d(_TAG, "screenState changed: $state")
            when (state) {
                LoadingState.LOADING -> {
                    progressBarMainActivity.visibility = ProgressBar.VISIBLE
                }
                LoadingState.SUCCESS -> {
                    progressBarMainActivity.visibility = ProgressBar.INVISIBLE
                }
                else -> {
                    progressBarMainActivity.visibility = ProgressBar.INVISIBLE
                    Snackbar.make(binding.root, "FAILURE LOADING", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        //Retrieve data from Firebase:
        sharedViewModel.setProgressBarState(LoadingState.LOADING)
        sharedViewModel.initiateApp(this) {result ->
            if(result == Result.SUCCESS)
                sharedViewModel.setProgressBarState(LoadingState.SUCCESS)
            else {
                Snackbar.make(binding.root, "FAILURE LOADING", Snackbar.LENGTH_LONG).show()
                sharedViewModel.setProgressBarState(LoadingState.FAILURE)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedViewModel.screenState.removeObservers(this)
        finish()
    }

    override fun onStop() {
        super.onStop()
        finish()
        exitProcess(0)
    }

    fun checkPermissions() {
        Log.d("$_TAG - checkForPermissions", "Checking for permissions...")
        val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 100
        val RECORD_STORAGE_PERMISSION_REQUEST_CODE = 101
        val recordAudioPermission = Manifest.permission.RECORD_AUDIO
        val storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE  //Includes READ_EXTERNAL_STORAGE
        val granted = PackageManager.PERMISSION_GRANTED

        val audioPermissionGranted = ContextCompat.checkSelfPermission(this, recordAudioPermission) == granted
        val storagePermissionGranted = ContextCompat.checkSelfPermission(this, storagePermission) == granted

        if (audioPermissionGranted && storagePermissionGranted) {
            Log.d("$_TAG - checkForPermissions", "Permissions already granted")
        } else {
            Log.d("$_TAG - checkForPermissions", "Asking for permissions")
            val permissionsToRequest = mutableListOf<String>()
            if (!audioPermissionGranted) {
                permissionsToRequest.add(recordAudioPermission)
            }
            if (!storagePermissionGranted) {
                permissionsToRequest.add(storagePermission)
            }
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), RECORD_AUDIO_PERMISSION_REQUEST_CODE)
            //TODO() Handle the result of the user denying the permissions
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val fileUri: Uri? = data?.data
            // Pass the selected file URI to the ViewModel
//            sharedViewModel.setSelectedFileUri(fileUri)
        }
    }
}