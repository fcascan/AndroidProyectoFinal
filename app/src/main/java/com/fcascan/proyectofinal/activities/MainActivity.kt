package com.fcascan.proyectofinal.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ProgressBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.fcascan.proyectofinal.enums.LoadingState
import com.fcascan.proyectofinal.R
import com.fcascan.proyectofinal.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private val _className = "FCC#MainActivity"

    private lateinit var binding: ActivityMainBinding
    lateinit var mainActivityViewModel: MainActivityViewModel

    //View Elements:
    lateinit var progressBarMainActivity: ProgressBar

    companion object {
        private const val REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //View Binding:
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainActivityViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        supportActionBar?.hide()

        //BottomBar:
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_account
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //ProgressBar:
        progressBarMainActivity = binding.root.findViewById(R.id.progressBarMainActivity)
        mainActivityViewModel.screenState.observe(this) { state ->
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
        mainActivityViewModel.updateAllCollectionsLists("KaRFGsvVFwNKd8CUAb6wHSrbEdy2")

        //Observe the selected file URI from the ViewModel
        mainActivityViewModel.selectedFileUri.observe(this) { fileUri ->
            fileUri?.let {
                // Handle the selected file URI here
                Snackbar.make(binding.root, "Selected file: $fileUri", Snackbar.LENGTH_SHORT).show()
                // Reset the selected file URI to avoid processing it again
                mainActivityViewModel.resetSelectedFileUri()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivityViewModel.screenState.removeObservers(this)
    }

    fun startFileSelectionActivity() {
        val intent = Intent(this, FileSelectActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val fileUri: Uri? = data?.data
            // Pass the selected file URI to the ViewModel
            mainActivityViewModel.setSelectedFileUri(fileUri)
        }
    }
}