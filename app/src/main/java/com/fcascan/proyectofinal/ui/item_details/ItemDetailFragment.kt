package com.fcascan.proyectofinal.ui.item_details

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.fcascan.proyectofinal.R
import com.fcascan.proyectofinal.shared.SharedViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

class ItemDetailFragment : Fragment() {
    private val _className = "FCC#ItemDetailFragment"

    //Input Parameters:
    private var editPermissions: Boolean? = null
    private var itemId : String? = null

    //View Elements:
    private lateinit var v : View
    private lateinit var txtItemDetailTitle: EditText
    private lateinit var txtItemDetailDescription: EditText
    private lateinit var spinnerItemDetailCategories: Spinner
    private lateinit var spinnerItemDetailGroup: Spinner
    private lateinit var txtFileName: EditText
    private lateinit var btnCardPlay: Button
    private lateinit var btnCardStop: Button
    private lateinit var btnSave: Button
    private lateinit var btnClear: Button
    private lateinit var btnBack: Button
    private lateinit var btnDelete: Button
    private lateinit var btnRecord: MaterialButton

    //ViewModels:
    private lateinit var itemDetailViewModel: ItemDetailViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        editPermissions = arguments?.getBoolean("paramEditPermissions")
        itemId = arguments?.getString("paramItemId")
        Log.d("$_className - onCreate", "Received paramEditPermissions: $editPermissions")
        Log.d("$_className - onCreate", "Received paramItemId: $itemId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.fragment_item_detail, container, false)
        txtItemDetailTitle = v.findViewById(R.id.txtItemDetailTitle)
        txtItemDetailDescription = v.findViewById(R.id.txtItemDetailDescription)
        spinnerItemDetailCategories = v.findViewById(R.id.spinnerItemDetailCategories)
        spinnerItemDetailGroup = v.findViewById(R.id.spinnerItemDetailGroup)
        txtFileName = v.findViewById(R.id.txtFileName)
        btnCardPlay = v.findViewById(R.id.btnCardPlay)
        btnCardStop = v.findViewById(R.id.btnCardStop)
        btnSave = v.findViewById(R.id.btnSave)
        btnClear = v.findViewById(R.id.btnClear)
        btnBack = v.findViewById(R.id.btnBack)
        btnDelete = v.findViewById(R.id.btnDelete)
        btnRecord = v.findViewById(R.id.btnRecord)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemDetailViewModel = ViewModelProvider(this)[ItemDetailViewModel::class.java]

        populateFields()
        editPermissionsCheck()

        btnRecord.setOnClickListener {
//            onRecordClicked()
            //TODO()
        }
        btnCardPlay.setOnClickListener {
            sharedViewModel.playFile(txtFileName.text.toString())
        }
        btnCardStop.setOnClickListener {
            sharedViewModel.stopPlayback()
        }
        btnSave.setOnClickListener {
            onSavedClicked()
        }
        btnClear.setOnClickListener {
            onClearClicked()
        }
        btnBack.setOnClickListener {
            onBackClicked()
        }
        btnDelete.setOnClickListener {
            onDeleteClicked()
        }
    }

    private fun populateFields() {
        Log.d("$_className - populateFields", "Populate Fields")
        //Primero buscar en la lista el item con el id
        val item = itemId?.let { sharedViewModel.getItemById(it) }
        if (item != null) {
            txtItemDetailTitle.setText(item.title)
            txtItemDetailDescription.setText(item.description)
            spinnerItemDetailCategories.setSelection(sharedViewModel.getCategoryIndexByID(item.categoryID))
            spinnerItemDetailGroup.setSelection(sharedViewModel.getGroupIndexByID(item.groupID))
            txtFileName.setText(item.documentId)
        } else {
            Log.d("$_className - populateFields", "Item not found")
            clearFields()
        }
        //Por ultimo buscar el archivo de audio y mostrar el nombre en el campo de texto
    }

    private fun editPermissionsCheck() {
        Log.d("$_className - editPermissionsCheck", "Edit Permissions Check: $editPermissions")
        if(editPermissions != true) {
            txtItemDetailTitle.focusable = View.NOT_FOCUSABLE
            txtItemDetailDescription.focusable = View.NOT_FOCUSABLE
            spinnerItemDetailCategories.isEnabled = false
            spinnerItemDetailGroup.isEnabled = false
            btnSave.visibility = View.GONE
            btnClear.visibility = View.GONE
            btnDelete.visibility = View.GONE
            btnRecord.visibility = View.GONE
        }
    }

    fun onSelectFileClicked() {
        Log.d("$_className - onSelectFileClicked", "Select File Button Clicked")
        //Ejecutar startFileSelectionActivity de MainActivity
//        TODO()
    }

    private fun onSavedClicked() {
        Log.d("$_className - onSavedClicked", "Save Button Clicked")
//        TODO()
        //1) Guardar/Pisar como Item en la base de datos y recuperar el ID del documento
        //2) Guardar/Pisar en Storage con el nombre del ID del documento
        //3) Guardar/Pisar el archivo de audio en la carpeta de la app
        //4) Volver a la pantalla anterior

        findNavController().navigateUp()
    }

    private fun onClearClicked() {
        Log.d("$_className - onClearClicked", "Clear Button Clicked")
        clearFields()
    }

    private fun onBackClicked() {
        Log.d("$_className - onBackClicked", "Back Button Clicked")
//        TODO() borrar!
        findNavController().navigateUp()
    }

    private fun onDeleteClicked() {
        Log.d("$_className - onDeleteClicked", "Delete Button Clicked")
//        TODO()
    }

    private fun clearFields() {
        txtItemDetailTitle.text.clear()
        txtItemDetailDescription.text.clear()
        spinnerItemDetailCategories.setSelection(0)
        spinnerItemDetailGroup.setSelection(0)
        Snackbar.make(v, "Fields Cleared", Snackbar.LENGTH_SHORT).show()
    }

}