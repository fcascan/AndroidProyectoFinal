package com.fcascan.proyectofinal.ui.recording

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.fcascan.proyectofinal.R
import com.fcascan.proyectofinal.entities.Item
import com.fcascan.proyectofinal.enums.LoadingState
import com.fcascan.proyectofinal.enums.Result
import com.fcascan.proyectofinal.shared.SharedViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import java.io.File

class RecordingFragment : Fragment() {
    private val _TAG = "FCC#RecordingFragment"

    //View Elements:
    private lateinit var v : View
    private lateinit var txtRecordingTitle : TextView
    private lateinit var txtRecordingDescription : TextView
    private lateinit var spinnerRecordingCategories : Spinner
    private lateinit var spinnerRecordingGroup : Spinner
    private lateinit var btnCardPlay : Button
    private lateinit var btnCardPause : Button
    private lateinit var btnCardStop : Button
    private lateinit var btnRecordingSave : Button
    private lateinit var btnRecord : MaterialButton
    private lateinit var progressRecording : ProgressBar

    //ViewModels:
    private lateinit var recordingViewModel: RecordingViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recordingViewModel = ViewModelProvider(this)[RecordingViewModel::class.java]

        v = inflater.inflate(R.layout.fragment_recording, container, false)
        txtRecordingTitle = v.findViewById(R.id.txtRecordingTitle)
        txtRecordingDescription = v.findViewById(R.id.txtRecordingDescription)
        spinnerRecordingCategories = v.findViewById(R.id.spinnerRecordingCategories)
        spinnerRecordingGroup = v.findViewById(R.id.spinnerRecordingGroup)
        btnCardPlay = v.findViewById(R.id.btnCardPlay)
        btnCardPause = v.findViewById(R.id.btnCardPause)
        btnCardStop = v.findViewById(R.id.btnCardStop)
        btnRecordingSave = v.findViewById(R.id.btnRecordingSave)
        btnRecord = v.findViewById(R.id.btnRecord)
        progressRecording = v.findViewById(R.id.progressRecording)
        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Files and Directories:
        val rootDirectory = File(requireContext().filesDir, "")
        val audiosDirectory = File(rootDirectory, "audios")
        val recordingsDirectory = File(rootDirectory, "recordings")
        val file = File(recordingsDirectory, "recorded_audio.aac")

        //Observers:
        recordingViewModel.spinnerCategoriesContent.observe(viewLifecycleOwner) { categories ->
            Log.d("$_TAG - onViewCreated", "spinnerCategoriesContent: $categories")
            if (categories != null) {
                categories.add(0, "all")
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    categories
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerRecordingCategories.adapter = adapter
                    spinnerRecordingCategories.setSelection(0)
                }
            }
        }

        recordingViewModel.spinnerGroupsContent.observe(viewLifecycleOwner) { groups ->
            Log.d("$_TAG - onViewCreated", "spinnerGroupsContent: $groups")
            if (groups != null) {
                groups.add(0, "all")
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    groups
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerRecordingGroup.adapter = adapter
                    spinnerRecordingGroup.setSelection(0)
                }
            }
        }

        //Event Listeners:
        btnCardPlay.setOnClickListener {
            playbackStarted()
            sharedViewModel.playFile(file) {
                Log.d("$_TAG - onViewCreated", "Play Audio onCompletionListener")
                playbackStopped()
            }
        }
        btnCardPause.setOnClickListener {
            playbackPaused()
            sharedViewModel.pausePlayback()
        }
        btnCardStop.setOnClickListener {
            playbackStopped()
            sharedViewModel.stopPlayback()
        }
        btnRecordingSave.setOnClickListener {
            onSaveClicked(rootDirectory)
        }
        btnRecord.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                recordingViewModel.onRecordLongClicked(requireContext())
                progressRecording.visibility = View.VISIBLE
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                progressRecording.visibility = View.INVISIBLE
                recordingViewModel.onRecordLongReleased()
            }
            view.performClick() // Call performClick() to handle click behavior
            true // Return true to indicate that the touch event is consumed
        }
    }

    override fun onStart() {
        super.onStart()
        progressRecording.visibility = View.INVISIBLE
        recordingViewModel.initAudioRecorder(requireContext())
        populateSpinners()
    }

    private fun populateSpinners() {
        Log.d("$_TAG - populateSpinners", "Populating Spinners")
        sharedViewModel.categoriesList.observe(viewLifecycleOwner) { categories ->
            Log.d("$_TAG - populateAll", "categoriesList updated: ${categories.toString()}")
            recordingViewModel.updateSpinnerCategories(categories)
        }
        sharedViewModel.groupsList.observe(viewLifecycleOwner) { groups ->
            Log.d("$_TAG - populateAll", "groupsList updated: ${groups.toString()}")
            recordingViewModel.updateSpinnerGroups(groups)
        }
    }

    fun onSaveClicked(rootDirectory: File) {
        Log.d("$_TAG - onSaveClicked", "Save Button Clicked")
        sharedViewModel.setProgressBarState(LoadingState.LOADING)
        disableViewElements()
        val selectedCategoryIndex = spinnerRecordingCategories.selectedItemPosition-1
        val categoryId = if (selectedCategoryIndex == -1) {""} else {sharedViewModel.getCategoryIdByIndex(selectedCategoryIndex)}
        val selectedGroupIndex = spinnerRecordingGroup.selectedItemPosition-1
        val groupId = if (selectedGroupIndex == -1) {""} else {sharedViewModel.getGroupIdByIndex(selectedGroupIndex)}
        val itemToSave = Item(
            "",
            sharedViewModel.userID,
            txtRecordingTitle.text.toString(),
            txtRecordingDescription.text.toString(),
            categoryId,
            groupId
        )
        Log.d("$_TAG - onSaveClicked", "Item to save: $itemToSave")
        sharedViewModel.saveItemOnEverywhere(itemToSave, rootDirectory) { result ->
            if (result == Result.SUCCESS) {
                Log.d("$_TAG - onSavedClicked", "Item saved successfully")
                sharedViewModel.setProgressBarState(LoadingState.SUCCESS)
                Snackbar.make(v, "Item saved successfully", Snackbar.LENGTH_SHORT).show()
                sharedViewModel.initiateApp(requireContext()) {
                    findNavController().navigateUp()
                }
            } else {
                Log.d("$_TAG - onSaveClicked", "Item could not be saved")
                sharedViewModel.setProgressBarState(LoadingState.FAILURE)
                enableViewElements()
                Snackbar.make(v, "Item could not be saved", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    fun disableViewElements() {
        btnRecordingSave.isEnabled = false
        btnRecord.isEnabled = false
        spinnerRecordingCategories.isEnabled = false
        spinnerRecordingGroup.isEnabled = false
        txtRecordingTitle.isEnabled = false
        txtRecordingDescription.isEnabled = false
    }

    fun enableViewElements() {
        btnRecordingSave.isEnabled = true
        btnRecord.isEnabled = true
        spinnerRecordingCategories.isEnabled = true
        spinnerRecordingGroup.isEnabled = true
        txtRecordingTitle.isEnabled = true
        txtRecordingDescription.isEnabled = true
    }

    fun playbackStarted() {
        btnCardPlay.visibility = View.INVISIBLE
        btnCardPause.visibility = View.VISIBLE
        btnCardStop.visibility = View.VISIBLE
    }

    fun playbackPaused() {
        btnCardPlay.visibility = View.VISIBLE
        btnCardPause.visibility = View.INVISIBLE
        btnCardStop.visibility = View.VISIBLE
    }

    fun playbackStopped() {
        btnCardPlay.visibility = View.VISIBLE
        btnCardPause.visibility = View.INVISIBLE
        btnCardStop.visibility = View.INVISIBLE
    }
}