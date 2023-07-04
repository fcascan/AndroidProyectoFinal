package com.fcascan.proyectofinal.activities

import android.Manifest
import android.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fcascan.proyectofinal.constants.AUTH_DAYS_TO_EXPIRE_LOGIN
import com.fcascan.proyectofinal.databinding.ActivityFileReceiverBinding
import com.fcascan.proyectofinal.entities.Item
import com.fcascan.proyectofinal.enums.LoadingState
import com.fcascan.proyectofinal.enums.Result
import com.fcascan.proyectofinal.repositories.FilesManager
import com.fcascan.proyectofinal.shared.SharedViewModel
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.time.LocalDate

class FileReceiverActivity : AppCompatActivity() {
    private val _TAG = "FCC#FileReceiverActivity"

    //View Elements:
    private lateinit var binding: ActivityFileReceiverBinding

    //ViewModels:
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var fileReceiverViewModel: FileReceiverViewModel

    //Managers:
    private val filesManager = FilesManager()

    //    https://developer.android.com/training/secure-file-sharing/share-file?hl=es-419
    private lateinit var rootDirectory: File        // The path to the root of this app's internal storage
    private lateinit var audiosDirectory: File      // The path to the "audios" subdirectory
    private lateinit var receivedDirectory: File    // The path to the "received" subdirectory
    private lateinit var receivedFile: File         // The generic filename for a received audio file

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //View Binding:
        binding = ActivityFileReceiverBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        //Initialize ViewModels:
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        fileReceiverViewModel = ViewModelProvider(this)[FileReceiverViewModel::class.java]

        //Initiate the root directory:
        rootDirectory = File(filesDir, "")
        audiosDirectory = File(rootDirectory, "audios")
        receivedDirectory = File(rootDirectory, "received")
        receivedFile = File(receivedDirectory, "receivedFile.opus")

        //Checksession:
        checkSession()
        sharedViewModel.setUserID("KaRFGsvVFwNKd8CUAb6wHSrbEdy2")

        //Check Permissions:
        checkPermissions()

        //CheckFolders Existence:
        val rootDirectory = File(this.filesDir, "")
        filesManager.checkFoldersExistence(rootDirectory)

        // Check if the activity was started by a share intent
        if (Intent.ACTION_SEND == intent.action && intent.type != null && intent.type!!.startsWith("audio/")) {
            handleSharedAudio(intent)
        } else {
            //Exit app if no file was selected
            Log.d("$_TAG - onCreate", "Empty Intent")
            Snackbar.make(binding.root, "No file received", Snackbar.LENGTH_SHORT).show()
            setResult(Activity.RESULT_CANCELED)
//            finish()  TODO() uncomment
        }

        //Observers:
        fileReceiverViewModel.spinnerCategoriesContent.observe(this) { categories ->
            Log.d("$_TAG - onViewCreated", "spinnerCategoriesContent: $categories")
            if (categories != null) {
                categories.add(0, "all")
                ArrayAdapter(
                    this,
                    R.layout.simple_spinner_item,
                    categories
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerNewAudioCategories.adapter = adapter
                    binding.spinnerNewAudioCategories.setSelection(0)
                }
            }
        }

        fileReceiverViewModel.spinnerGroupsContent.observe(this) { groups ->
            Log.d("$_TAG - onViewCreated", "spinnerGroupsContent: $groups")
            if (groups != null) {
                groups.add(0, "all")
                ArrayAdapter(
                    this,
                    R.layout.simple_spinner_item,
                    groups
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerNewAudioGroup.adapter = adapter
                    binding.spinnerNewAudioGroup.setSelection(0)
                }
            }
        }

        //Event Listeners:
        binding.btnCardPlay.setOnClickListener {
            playbackStarted()
            sharedViewModel.playFile(receivedFile) {
                Log.d("$_TAG - onViewCreated", "Play Audio onCompletionListener")
                playbackStopped()
            }
        }
        binding.btnCardStop.setOnClickListener {
            playbackStopped()
            sharedViewModel.stopPlayback()
        }
        binding.btnNewFileSave.setOnClickListener {
            onSaveClicked(rootDirectory)
        }
    }

    override fun onStart() {
        super.onStart()
        binding.progressBarMainActivity.visibility = View.INVISIBLE
        populateSpinners()
    }

    private fun checkSession() {
        Log.d("$_TAG - checkSession", "Checking if user is already logged in")
        val userIDsharedPref = this.getSharedPreferences("userID", Context.MODE_PRIVATE)
        val dateSharedPref = this.getSharedPreferences("date", Context.MODE_PRIVATE)
        Log.d(_TAG, "userIDsharedPref: ${userIDsharedPref.getString("userID", "")}")
        Log.d(_TAG, "dateSharedPref: ${dateSharedPref.getString("date", "")}")

        val storedDate = dateSharedPref.getString("date", null)
        if (userIDsharedPref != null && storedDate != null &&
            LocalDate.now().minusDays(AUTH_DAYS_TO_EXPIRE_LOGIN).isBefore(LocalDate.parse(storedDate))) {
            Log.d("$_TAG - checkSession", "User is already logged in")
        } else {
            Log.d("$_TAG - checkSession", "User is not logged in")
            Snackbar.make(binding.root, "You must be logged in to use this app", Snackbar.LENGTH_SHORT).show()
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun populateSpinners() {
        Log.d("$_TAG - populateSpinners", "Populating Spinners")
        sharedViewModel.categoriesList.observe(this) { categories ->
            Log.d("$_TAG - populateAll", "categoriesList updated: ${categories.toString()}")
            fileReceiverViewModel.updateSpinnerCategories(categories)
        }
        sharedViewModel.groupsList.observe(this) { groups ->
            Log.d("$_TAG - populateAll", "groupsList updated: ${groups.toString()}")
            fileReceiverViewModel.updateSpinnerGroups(groups)
        }
    }

    override fun onBackPressed() {
        // Handle back press to cancel file selection
        Log.d("$_TAG - onBackPressed", "Clear Button Clicked")
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    override fun onDestroy() {
        // Handle activity destruction without file selection
        Log.d("$_TAG - onDestroy", "File Select Cancelled")
        setResult(Activity.RESULT_CANCELED)
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        finish()
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

    private fun handleSharedAudio(intent: Intent) {
        Log.d("$_TAG - handleSharedAudio", "Handling shared audio file")
        if (intent == null) setResult(Activity.RESULT_CANCELED, intent)
        val audioUri: Uri? = intent.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri
        if (audioUri != null) {
            val file = audioUri.path?.let { File(it) }
            Log.d("$_TAG - handleSharedAudio", "Received audio file: ${file?.absolutePath}")
            if (file != null) {
                filesManager.renameFile(file, receivedFile)
                Log.d("$_TAG - handleSharedAudio", "Renamed received audio: ${file.absolutePath}")
            }
        }
    }

    private fun onSaveClicked(rootDirectory: File) {
        Log.d("$_TAG - onSaveClicked", "Save Button Clicked")
        binding.progressBarMainActivity.visibility = View.VISIBLE
        disableViewElements()
        val selectedCategoryIndex = binding.spinnerNewAudioCategories.selectedItemPosition-1
        val categoryId = if (selectedCategoryIndex == -1) {""} else {sharedViewModel.getCategoryIdByIndex(selectedCategoryIndex)}
        val selectedGroupIndex = binding.spinnerNewAudioGroup.selectedItemPosition-1
        val groupId = if (selectedGroupIndex == -1) {""} else {sharedViewModel.getGroupIdByIndex(selectedGroupIndex)}
        val itemToSave = Item(
            "",
            sharedViewModel.userID,
            binding.txtNewAudioTitle.text.toString(),
            binding.txtNewAudioDescription.text.toString(),
            categoryId,
            groupId
        )
        Log.d("$_TAG - onSaveClicked", "Item to save: $itemToSave")
        sharedViewModel.saveItemOnEverywhere(itemToSave, rootDirectory) { result ->
            if (result == Result.SUCCESS) {
                Log.d("$_TAG - onSavedClicked", "Item saved successfully")
                binding.progressBarMainActivity.visibility = View.INVISIBLE
                Snackbar.make(binding.root, "Item saved successfully", Snackbar.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Log.d("$_TAG - onSaveClicked", "Item could not be saved")
                sharedViewModel.setProgressBarState(LoadingState.FAILURE)
                enableViewElements()
                Snackbar.make(binding.root, "Item could not be saved", Snackbar.LENGTH_LONG).show()
            }
        }
        setResult(Activity.RESULT_OK)
        finish()
    }

    fun disableViewElements() {
        binding.btnNewFileSave.isEnabled = false
        binding.spinnerNewAudioCategories.isEnabled = false
        binding.spinnerNewAudioGroup.isEnabled = false
        binding.txtNewAudioTitle.isEnabled = false
        binding.txtNewAudioDescription.isEnabled = false
    }

    fun enableViewElements() {
        binding.btnNewFileSave.isEnabled = true
        binding.spinnerNewAudioCategories.isEnabled = true
        binding.spinnerNewAudioGroup.isEnabled = true
        binding.txtNewAudioTitle.isEnabled = true
        binding.txtNewAudioDescription.isEnabled = true
    }

    fun playbackStarted() {
        binding.btnCardPlay.visibility = View.INVISIBLE
        binding.btnCardPause.visibility = View.VISIBLE
        binding.btnCardStop.visibility = View.VISIBLE
    }

    fun playbackPaused() {
        binding.btnCardPlay.visibility = View.VISIBLE
        binding.btnCardPause.visibility = View.INVISIBLE
        binding.btnCardStop.visibility = View.VISIBLE
    }

    fun playbackStopped() {
        binding.btnCardPlay.visibility = View.VISIBLE
        binding.btnCardPause.visibility = View.INVISIBLE
        binding.btnCardStop.visibility = View.INVISIBLE
    }
}