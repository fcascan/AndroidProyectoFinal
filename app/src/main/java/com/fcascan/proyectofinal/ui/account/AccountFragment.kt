package com.fcascan.proyectofinal.ui.account

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fcascan.proyectofinal.constants.AUTH_DAYS_TO_EXPIRE_LOGIN
import com.fcascan.proyectofinal.databinding.FragmentAccountBinding
import java.time.LocalDate

class AccountFragment : Fragment() {
    private val _TAG = "FCC#AccountFragment"

    //View:
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    //ViewModels:
    private lateinit var accountViewModel: AccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        accountViewModel = ViewModelProvider(this)[AccountViewModel::class.java]

        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Observers:
        accountViewModel.logedOut.observe(viewLifecycleOwner) {
            Log.d("$_TAG - onViewCreated", "logedOut: $it")
            if (it == true) {
                val userIDsharedPref = requireActivity().getSharedPreferences("userID", Context.MODE_PRIVATE)
                val dateSharedPref = requireActivity().getSharedPreferences("date", Context.MODE_PRIVATE)
                Log.d(_TAG, "userIDsharedPref: ${userIDsharedPref.getString("userID", "")}")
                Log.d(_TAG, "dateSharedPref: ${dateSharedPref.getString("date", "")}")
                userIDsharedPref.edit().putString("userID", "").apply()
                dateSharedPref.edit().putString("date", LocalDate.now().minusDays(2 * AUTH_DAYS_TO_EXPIRE_LOGIN).toString()).apply()
                requireActivity().finish()  //TODO() cambiar a reset
            }
        }

        //Event Listeners:
        binding.btnLogOut.setOnClickListener {
            accountViewModel.logout()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}