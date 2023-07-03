package com.fcascan.proyectofinal.ui.recording

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.fcascan.proyectofinal.R
import com.fcascan.proyectofinal.adapters.ItemsAdapter
import com.fcascan.proyectofinal.shared.SharedViewModel
import com.google.android.material.button.MaterialButton
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

        progressRecording.visibility = View.INVISIBLE
        val directory = File(requireContext().filesDir, "recordings")

        //Event Listeners:
        btnCardPlay.setOnClickListener {
            playbackStarted()
            val file = File(directory, "recorded_audio.aac")
            sharedViewModel.playFile(file) {
                Log.d("$_TAG - onViewCreated", "Play Audio onCompletionListener")
//                val viewHolder = recyclerView.findViewHolderForAdapterPosition(index) as ItemsAdapter.ItemsHolder
//                viewHolder.resetPlayButton()
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
//            onSavedClicked()
            TODO()
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
        recordingViewModel.initAudioRecorder(requireContext())
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