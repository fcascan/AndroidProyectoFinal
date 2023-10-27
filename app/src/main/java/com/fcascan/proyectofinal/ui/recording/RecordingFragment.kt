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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.fcascan.proyectofinal.databinding.FragmentRecordingBinding
import com.fcascan.proyectofinal.entities.Item
import com.fcascan.proyectofinal.enums.LoadingState
import com.fcascan.proyectofinal.enums.Result
import com.fcascan.proyectofinal.shared.SharedViewModel
import com.google.android.material.snackbar.Snackbar
import java.io.File

class RecordingFragment : Fragment() {
    private val _TAG = "FCC#RecordingFragment"

    //View Elements:
    private var _binding : FragmentRecordingBinding? = null
    private val binding get() = _binding!!

    //ViewModels:
    private lateinit var recordingViewModel: RecordingViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //ViewModels:
        recordingViewModel = ViewModelProvider(this)[RecordingViewModel::class.java]

        //Inflate:
        _binding = FragmentRecordingBinding.inflate(inflater, container, false)

        return binding.root
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
                    binding.spinnerRecordingCategories.adapter = adapter
                    binding.spinnerRecordingCategories.setSelection(0)
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
                    binding.spinnerRecordingGroup.adapter = adapter
                    binding.spinnerRecordingGroup.setSelection(0)
                }
            }
        }

        //Button Listeners:
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnCardPlay.setOnClickListener {
            playbackStarted()
            sharedViewModel.playFile(file) {
                Log.d("$_TAG - onViewCreated", "Play Audio onCompletionListener")
                playbackStopped()
            }
        }
        binding.btnCardPause.setOnClickListener {
            playbackPaused()
            sharedViewModel.pausePlayback()
        }
        binding.btnCardStop.setOnClickListener {
            playbackStopped()
            sharedViewModel.stopPlayback()
        }
        binding.btnRecordingSave.setOnClickListener {
            onSaveClicked(rootDirectory)
        }
        binding.btnRecord.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                recordingViewModel.onRecordLongClicked(requireContext())
                binding.progressRecording.visibility = View.VISIBLE
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                binding.progressRecording.visibility = View.INVISIBLE
                recordingViewModel.onRecordLongReleased()
            }
            view.performClick() // Call performClick() to handle click behavior
            true // Return true to indicate that the touch event is consumed
        }
    }

    override fun onStart() {
        super.onStart()
        binding.progressRecording.visibility = View.INVISIBLE
        recordingViewModel.initAudioRecorder(requireContext())
        populateSpinners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        val selectedCategoryIndex = binding.spinnerRecordingCategories.selectedItemPosition-1
        val categoryId = if (selectedCategoryIndex == -1) {""} else {sharedViewModel.getCategoryIdByIndex(selectedCategoryIndex)}
        val selectedGroupIndex = binding.spinnerRecordingGroup.selectedItemPosition-1
        val groupId = if (selectedGroupIndex == -1) {""} else {sharedViewModel.getGroupIdByIndex(selectedGroupIndex)}
        val itemToSave = Item(
            "",
            sharedViewModel.userID,
            binding.txtRecordingTitle.text.toString(),
            binding.txtRecordingDescription.text.toString(),
            categoryId,
            groupId
        )
        Log.d("$_TAG - onSaveClicked", "Item to save: $itemToSave")
        sharedViewModel.saveRecordedItemOnEverywhere(itemToSave, rootDirectory) { result ->
            if (result == Result.SUCCESS) {
                Log.d("$_TAG - onSavedClicked", "Item saved successfully")
                sharedViewModel.setProgressBarState(LoadingState.SUCCESS)
                Snackbar.make(binding.root, "Item saved successfully", Snackbar.LENGTH_SHORT).show()
                sharedViewModel.initiateApp(requireContext()) {
                    findNavController().navigateUp()
                }
            } else {
                Log.d("$_TAG - onSaveClicked", "Item could not be saved")
                sharedViewModel.setProgressBarState(LoadingState.FAILURE)
                enableViewElements()
                Snackbar.make(binding.root, "Item could not be saved", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    fun disableViewElements() {
        binding.btnRecordingSave.isEnabled = false
        binding.btnRecord.isEnabled = false
        binding.spinnerRecordingCategories.isEnabled = false
        binding.spinnerRecordingGroup.isEnabled = false
        binding.txtRecordingTitle.isEnabled = false
        binding.txtRecordingDescription.isEnabled = false
    }

    fun enableViewElements() {
        binding.btnRecordingSave.isEnabled = true
        binding.btnRecord.isEnabled = true
        binding.spinnerRecordingCategories.isEnabled = true
        binding.spinnerRecordingGroup.isEnabled = true
        binding.txtRecordingTitle.isEnabled = true
        binding.txtRecordingDescription.isEnabled = true
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