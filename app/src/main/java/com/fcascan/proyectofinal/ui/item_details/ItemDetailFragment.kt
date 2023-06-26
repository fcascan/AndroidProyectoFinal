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
import androidx.navigation.fragment.findNavController
import com.fcascan.proyectofinal.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

class ItemDetailFragment : Fragment() {
    private val _className = "FCC#ItemDetailFragment"

    //Input Parameters:
    private var editPermissions: Boolean = false
    private var itemId : Int? = null

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

    private lateinit var viewModel: ItemDetailViewModel

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

        viewModel = ViewModelProvider(this).get(ItemDetailViewModel::class.java)

        populateFields()
        editPermissionsCheck()

        btnCardPlay.setOnClickListener {
            //TODO()
        }
        btnCardStop.setOnClickListener {
            //TODO()
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

    fun populateFields() {
        Log.d("$_className - populateFields", "Populate Fields")
        //Primero buscar en la lista el item con el id
        //Luego llenar los campos con los datos del item
        //Por ultimo buscar el archivo de audio y mostrar el nombre en el campo de texto
    }

    fun editPermissionsCheck() {
        Log.d("$_className - editPermissionsCheck", "Edit Permissions Check: $editPermissions")
        if(!editPermissions) {
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
        TODO()
    }

    fun onSavedClicked() {
        Log.d("$_className - onSavedClicked", "Save Button Clicked")
//        TODO()
        //1) Guardar los datos en la base de datos
        //2) Guardar/Pisar el archivo de audio en la carpeta de la app
        //3) Guardar/Pisar el archivo de audio en la nube
        //4) Volver a la pantalla anterior

        findNavController().navigateUp()
    }

    fun onClearClicked() {
        Log.d("$_className - onClearClicked", "Clear Button Clicked")
        txtItemDetailTitle.text.clear()
        txtItemDetailDescription.text.clear()
        spinnerItemDetailCategories.setSelection(0)
        spinnerItemDetailGroup.setSelection(0)
        Snackbar.make(v, "Fields Cleared", Snackbar.LENGTH_SHORT).show()
    }

    fun onBackClicked() {
        Log.d("$_className - onBackClicked", "Back Button Clicked")
//        TODO()
        findNavController().navigateUp()
    }

    fun onDeleteClicked() {
        Log.d("$_className - onDeleteClicked", "Delete Button Clicked")
        TODO()
    }

}