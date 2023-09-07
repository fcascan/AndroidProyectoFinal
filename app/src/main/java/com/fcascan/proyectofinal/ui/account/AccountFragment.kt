package com.fcascan.proyectofinal.ui.account

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.fcascan.proyectofinal.R
import com.fcascan.proyectofinal.activities.AuthActivity
import com.fcascan.proyectofinal.constants.AUTH_DAYS_TO_FORCE_EXPIRE_LOGIN
import com.fcascan.proyectofinal.databinding.FragmentAccountBinding
import com.fcascan.proyectofinal.enums.Result
import com.fcascan.proyectofinal.shared.SharedViewModel
import com.google.android.material.snackbar.Snackbar
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
        //ViewModels:
        accountViewModel = ViewModelProvider(this)[AccountViewModel::class.java]

        //Inflate:
        _binding = FragmentAccountBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Default Avatar:view.findViewById(R.id.card_item)
        binding.imgAvatar.setImageDrawable(resources.getDrawable(R.drawable.avatar_1, null))

        //Observers:
        accountViewModel.logedOut.observe(viewLifecycleOwner) {
            Log.d("$_TAG - onViewCreated", "logedOut: $it")
            if (it == true) {
                val userIDsharedPref = requireActivity().getSharedPreferences("userID", Context.MODE_PRIVATE)
                val dateSharedPref = requireActivity().getSharedPreferences("date", Context.MODE_PRIVATE)
                Log.d(_TAG, "userIDsharedPref: ${userIDsharedPref.getString("userID", "")}")
                Log.d(_TAG, "dateSharedPref: ${dateSharedPref.getString("date", "")}")
                userIDsharedPref.edit().putString("userID", "").apply()
                dateSharedPref.edit().putString("date", LocalDate.now().minusDays(AUTH_DAYS_TO_FORCE_EXPIRE_LOGIN).toString()).apply()
                requireActivity().finish()
//                startActivity(Intent(context, AuthActivity::class.java))  //TODO() creo que faltaria agregar un delay o algo
            }
        }

        //Event Listeners:
        binding.btnLogOut.setOnClickListener {
            Log.d("$_TAG - btnLogOut", "Logout Button Clicked")
            val dialogBuilder = AlertDialog.Builder(context)
                .setMessage("Are you sure you want to Logout from your account?")
                .setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                    accountViewModel.onLogoutClicked()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                }
                .setCancelable(false)
            val dialog = dialogBuilder.create()
            dialog.show()
        }

        binding.btnWipeLocalData.setOnClickListener {
            Log.d("$_TAG - btnWipeLocalData", "Wipe Local Data Button Clicked")
            val dialogBuilder = AlertDialog.Builder(context)
                .setMessage("Are you sure you want to wipe local data?")
                .setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                    context?.let { it1 -> accountViewModel.onWipeLocalDataClicked(it1) }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                }
                .setCancelable(false)
            val dialog = dialogBuilder.create()
            dialog.show()
        }

        binding.btnDeleteAccount.setOnClickListener {
            Log.d("$_TAG - btnDeleteAccount", "Delete Account Button Clicked")
            val dialogBuilder = AlertDialog.Builder(context)
                .setMessage("Are you sure you want to completely delete your account?")
                .setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                    accountViewModel.onDeleteAccountClicked { result ->
                        if (result == Result.SUCCESS)
                            Snackbar.make(binding.root, "Account deleted successfully", Snackbar.LENGTH_LONG).show()
                        else
                            Snackbar.make(binding.root, "Error deleting account", Snackbar.LENGTH_LONG).show()
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}