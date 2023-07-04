package com.fcascan.proyectofinal.ui.item_details

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.fcascan.proyectofinal.R
import com.fcascan.proyectofinal.entities.Item
import com.fcascan.proyectofinal.enums.LoadingState
import com.fcascan.proyectofinal.enums.PlaybackState
import com.fcascan.proyectofinal.enums.Result
import com.fcascan.proyectofinal.shared.SharedViewModel
import com.google.android.material.snackbar.Snackbar
import java.io.File

class ItemDetailFragment : Fragment() {
    private val _TAG = "FCC#ItemDetailFragment"

    //Input Parameters:
    private var editPermissions: Boolean? = null
    private var itemId : String? = null

    //View Elements:
    private lateinit var v : View
    private lateinit var btnBack : Button
    private lateinit var txtItemDetailScreenTitle: TextView
    private lateinit var txtItemDetailTitle: EditText
    private lateinit var txtItemDetailDescription: EditText
    private lateinit var spinnerItemDetailCategories: Spinner
    private lateinit var spinnerItemDetailGroup: Spinner
    private lateinit var txtFileName: EditText
    private lateinit var btnCardPlay: Button
    private lateinit var btnCardPause: Button
    private lateinit var btnCardStop: Button
    private lateinit var btnSave: Button
    private lateinit var btnClear: Button
    private lateinit var btnDelete: Button

    //ViewModels:
    private lateinit var itemDetailViewModel: ItemDetailViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        editPermissions = arguments?.getBoolean("paramEditPermissions")
        itemId = arguments?.getString("paramItemId")
        Log.d("$_TAG - onCreate", "Received paramEditPermissions: $editPermissions")
        Log.d("$_TAG - onCreate", "Received paramItemId: $itemId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //ViewModels:
        itemDetailViewModel = ViewModelProvider(this)[ItemDetailViewModel::class.java]

        v = inflater.inflate(R.layout.fragment_item_detail, container, false)
        btnBack = v.findViewById(R.id.btnBack)
        txtItemDetailScreenTitle = v.findViewById(R.id.txtActionsScreenTitle)
        txtItemDetailTitle = v.findViewById(R.id.txtItemDetailTitle)
        txtItemDetailDescription = v.findViewById(R.id.txtItemDetailDescription)
        spinnerItemDetailCategories = v.findViewById(R.id.spinnerItemDetailCategories)
        spinnerItemDetailGroup = v.findViewById(R.id.spinnerItemDetailGroup)
        txtFileName = v.findViewById(R.id.txtFileName)
        btnCardPlay = v.findViewById(R.id.btnCardPlay)
        btnCardPause = v.findViewById(R.id.btnCardPause)
        btnCardStop = v.findViewById(R.id.btnCardStop)
        btnSave = v.findViewById(R.id.btnRecordingSave)
        btnClear = v.findViewById(R.id.btnClear)
        btnDelete = v.findViewById(R.id.btnDelete)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val directory = File(context?.filesDir, "audios")

        itemDetailViewModel.spinnerCategoriesContent.observe(viewLifecycleOwner) { categories ->
            Log.d("$_TAG - onViewCreated", "spinnerCategoriesContent updated: $categories")
            if (categories != null) {
                categories.add(0, "all")
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    categories
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerItemDetailCategories.adapter = adapter
                    val category = itemId?.let { sharedViewModel.getItemById(it)?.categoryID }
                    val categoryName = category?.let { sharedViewModel.getCategoryById(it)?.name }
                    if (categoryName != null) {
                        setSpinnerSelection(spinnerItemDetailCategories, categoryName)
                    }
                }
            }
        }

        itemDetailViewModel.spinnerGroupsContent.observe(viewLifecycleOwner) { groups ->
            Log.d("$_TAG - onViewCreated", "spinnerGroupsContent updated: $groups")
            if (groups != null) {
                groups.add(0, "all")
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    groups
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerItemDetailGroup.adapter = adapter
                    val group = itemId?.let { sharedViewModel.getItemById(it)?.groupID }
                    val groupName = group?.let { sharedViewModel.getGroupById(it)?.name }
                    if (groupName != null) {
                        setSpinnerSelection(spinnerItemDetailGroup, groupName)
                    }
                }
            }
        }

        //LiveData Observers:
        sharedViewModel.playbackState.observe(viewLifecycleOwner) {
            changeButtonsStateWithPlayback(it)
        }

        //Button Listeners:
        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        btnCardPlay.setOnClickListener {
            playbackStarted()
            val file = File(directory, "${txtFileName.text.toString()}.opus")
            sharedViewModel.playFile(file) {
                Log.d("$_TAG - onViewCreated", "Playback finished")
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
        btnSave.setOnClickListener {
            onSavedClicked()
        }
        btnClear.setOnClickListener {
            onClearClicked()
        }
        btnDelete.setOnClickListener {
            onDeleteClicked()
        }
    }

    override fun onStart() {
        super.onStart()
        populateSpinners()
        populateFields()
        changeButtonsStateWithPlayback(sharedViewModel.playbackState.value)
        editPermissionsCheck()
    }

    private fun changeButtonsStateWithPlayback(playbackState: PlaybackState?) {
        Log.d("$_TAG - changeButtonsStateWithPlayback", "playbackState: $playbackState")
        when (playbackState) {
            PlaybackState.PLAYING -> { playbackStarted() }
            PlaybackState.PAUSED -> { playbackPaused() }
            PlaybackState.STOPPED -> { playbackStopped() }
            else -> {
                Log.d("$_TAG - onViewCreated", "PlaybackState: $playbackState")
                playbackStopped()
                Snackbar.make(v, "PlaybackState: $playbackState", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun populateSpinners() {
        Log.d("$_TAG - populateSpinners", "Populating Spinners")
        sharedViewModel.categoriesList.observe(viewLifecycleOwner) { categories ->
            Log.d("$_TAG - populateAll", "categoriesList updated: ${categories.toString()}")
            itemDetailViewModel.updateSpinnerCategories(categories)
        }
        sharedViewModel.groupsList.observe(viewLifecycleOwner) { groups ->
            Log.d("$_TAG - populateAll", "groupsList updated: ${groups.toString()}")
            itemDetailViewModel.updateSpinnerGroups(groups)
        }
    }
    private fun populateFields() {
        Log.d("$_TAG - populateFields", "Populate Fields")
        //Primero buscar en la lista el item con el id
        val item = itemId?.let { sharedViewModel.getItemById(it) }
        if (item != null) {
            txtItemDetailTitle.setText(item.title)
            txtItemDetailDescription.setText(item.description)
            spinnerItemDetailCategories.setSelection(sharedViewModel.getCategoryIndexByID(item.categoryID))
            spinnerItemDetailGroup.setSelection(sharedViewModel.getGroupIndexByID(item.groupID))
            txtFileName.setText(item.documentId)
        } else {
            Log.d("$_TAG - populateFields", "Item not found")
            clearFields()
        }
    }

    private fun setSpinnerSelection(spinner: Spinner, selection: String) {
        val adapter = spinner.adapter
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i) == selection) {
                Log.d("$_TAG - setSpinnerSelection", "Setting Spinner Selection: $selection")
                spinner.setSelection(i)
                break
            }
        }
    }

    private fun editPermissionsCheck() {
        Log.d("$_TAG - editPermissionsCheck", "Edit Permissions Check: $editPermissions")
        if(editPermissions != true) {
            txtItemDetailScreenTitle.text = "Item Details (read-only)"
            txtItemDetailTitle.focusable = View.NOT_FOCUSABLE
            txtItemDetailDescription.focusable = View.NOT_FOCUSABLE
            spinnerItemDetailCategories.isEnabled = false
            spinnerItemDetailGroup.isEnabled = false
            btnSave.visibility = View.GONE
            btnClear.visibility = View.GONE
            btnDelete.visibility = View.GONE
        }
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

    fun onSelectFileClicked() {
        Log.d("$_TAG - onSelectFileClicked", "Select File Button Clicked")
        //Ejecutar startFileSelectionActivity de MainActivity
//        TODO()
    }

    private fun onSavedClicked() {
        Log.d("$_TAG - onSavedClicked", "Save Button Clicked")
        sharedViewModel.setProgressBarState(LoadingState.LOADING)
        val itemToSave = Item(
            itemId.toString(),
            sharedViewModel.userID,
            txtItemDetailTitle.text.toString(),
            txtItemDetailDescription.text.toString(),
            sharedViewModel.getCategoryIdByIndex(spinnerItemDetailCategories.selectedItemPosition-1),
            sharedViewModel.getGroupIdByIndex(spinnerItemDetailGroup.selectedItemPosition-1)
        )
        Log.d("$_TAG - onSavedClicked", "Item to save: $itemToSave")
        sharedViewModel.updateItem(itemToSave, requireContext()) { result ->
            if (result == Result.SUCCESS) {
                Log.d("$_TAG - onSavedClicked", "Successfully saved")
                Snackbar.make(v, "Successfully saved", Snackbar.LENGTH_SHORT).show()
                sharedViewModel.setProgressBarState(LoadingState.SUCCESS)
    //        findNavController().navigateUp()
            } else {
                Log.d("$_TAG - onSavedClicked", "Error saving")
                Snackbar.make(v, "Error saving", Snackbar.LENGTH_LONG).show()
                sharedViewModel.setProgressBarState(LoadingState.FAILURE)
            }
        }
    }

    private fun onClearClicked() {
        Log.d("$_TAG - onClearClicked", "Clear Button Clicked")
        clearFields()
    }

    private fun onDeleteClicked() {
        Log.d("$_TAG - onDeleteClicked", "Delete Button Clicked")
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle("Delete Item")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                sharedViewModel.wipeItemFromEverywhere(itemId.toString(), requireContext()) { result ->
                    if (result == Result.SUCCESS) {
                        Log.d("$_TAG - onDeleteClicked", "Item deleted successfully from everywhere")
                        Snackbar.make(v, "Successfully erased", Snackbar.LENGTH_LONG).show()
                        sharedViewModel.setProgressBarState(LoadingState.SUCCESS)
                        findNavController().navigateUp()
                    } else {
                        Log.d("$_TAG - onDeleteClicked", "Error deleting item")
                        Snackbar.make(v, "Error deleting", Snackbar.LENGTH_LONG).show()
                        sharedViewModel.setProgressBarState(LoadingState.FAILURE)
                        findNavController().navigateUp()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .setCancelable(false)
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun clearFields() {
        txtItemDetailTitle.text.clear()
        txtItemDetailDescription.text.clear()
        spinnerItemDetailCategories.setSelection(0)
        spinnerItemDetailGroup.setSelection(0)
        Snackbar.make(v, "Fields Cleared", Snackbar.LENGTH_SHORT).show()
    }

}