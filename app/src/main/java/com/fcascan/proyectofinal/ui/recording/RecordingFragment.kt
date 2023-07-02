package com.fcascan.proyectofinal.ui.recording

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.fcascan.proyectofinal.R
import com.fcascan.proyectofinal.shared.SharedViewModel
import com.google.android.material.button.MaterialButton

class RecordingFragment : Fragment() {
    private val _className = "FCC#RecordingFragment"

    //View Elements:
    private lateinit var v : View
    private lateinit var btnRecord: MaterialButton

    companion object {
        fun newInstance() = RecordingFragment()
    }
    //ViewModels:
    private lateinit var recordingViewModel: RecordingViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.fragment_recording, container, false)
        btnRecord = v.findViewById(R.id.btnRecord)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recordingViewModel = ViewModelProvider(this).get(RecordingViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnRecord.setOnClickListener {
            recordingViewModel.onRecordClicked()
        }
    }

}