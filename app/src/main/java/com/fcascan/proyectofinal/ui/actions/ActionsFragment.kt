package com.fcascan.proyectofinal.ui.actions

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.fcascan.proyectofinal.R
import com.fcascan.proyectofinal.adapters.ActionsAdapter
import com.fcascan.proyectofinal.databinding.FragmentActionsBinding
import com.fcascan.proyectofinal.enums.ActionsScreen
import com.fcascan.proyectofinal.shared.SharedViewModel

class ActionsFragment : Fragment() {
    private val _TAG = "FCC#ActionsFragment"

    //View Elements:
    private var _binding: FragmentActionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var recViewAdapter: ActionsAdapter

    //ViewModels:
    private lateinit var actionsViewModel: ActionsViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //ViewModels:
        actionsViewModel = ViewModelProvider(this)[ActionsViewModel::class.java]

        //Inflate:
        _binding = FragmentActionsBinding.inflate(inflater, container, false)

        //RecyclerView Config:
        binding.recViewActions.setHasFixedSize(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actionsViewModel.setCurrentScreen(ActionsScreen.SCREEN_ACTIONS)
        actionsViewModel.changeRecViewContent()

        val onBackCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackCallback)

        actionsViewModel.recViewContent.observe(viewLifecycleOwner) { content ->
            Log.d("$_TAG - onViewCreated", "RecViewContent changed")
            recViewAdapter = ActionsAdapter(
                actionsList = content!!,
                onClick = { index -> onCardClicked(index) },
                onLongClick = { index -> onCardLongClicked(index) }
            )
            binding.recViewActions.layoutManager = LinearLayoutManager(context)
            binding.recViewActions.adapter = recViewAdapter
        }

        actionsViewModel.currentScreen.observe(viewLifecycleOwner) { screen ->
            Log.d("$_TAG - onViewCreated", "CurrentScreen changed")
            actionsViewModel.changeRecViewContent()
            when(screen) {
                ActionsScreen.SCREEN_ACTIONS -> {
                    binding.txtActionsScreenTitle.text = ActionsScreen.SCREEN_ACTIONS.title
                    binding.fabActions.visibility = View.GONE
                    binding.btnBack.visibility = View.GONE
                    binding.recViewActions.layoutManager = LinearLayoutManager(context)
                }
                ActionsScreen.SCREEN_CATEGORIES -> {
                    binding.txtActionsScreenTitle.text = ActionsScreen.SCREEN_CATEGORIES.title
                    binding.fabActions.visibility = View.VISIBLE
                    binding.btnBack.visibility = View.VISIBLE
                    binding.recViewActions.layoutManager = GridLayoutManager(context, 2)
                }
                ActionsScreen.SCREEN_GROUPS -> {
                    binding.txtActionsScreenTitle.text = ActionsScreen.SCREEN_GROUPS.title
                    binding.fabActions.visibility = View.VISIBLE
                    binding.btnBack.visibility = View.VISIBLE
                    binding.recViewActions.layoutManager = GridLayoutManager(context, 2)
                }
                else -> {
                    Log.e("$_TAG - onViewCreated", "Error: CurrentScreen not detected")
                    actionsViewModel.setCurrentScreen(ActionsScreen.SCREEN_ACTIONS)
                }
            }
        }

        binding.fabActions.setOnClickListener {
            Log.d("$_TAG - onViewCreated", "FAB clicked")
            when(actionsViewModel.currentScreen.value) {
                ActionsScreen.SCREEN_ACTIONS -> { /*TODO()*/ }
                ActionsScreen.SCREEN_CATEGORIES -> { /*TODO()*/ }
                ActionsScreen.SCREEN_GROUPS -> { /*TODO()*/ }
                else -> {
                    Log.e("$_TAG - onViewCreated", "Error: CurrentScreen not detected")
                    actionsViewModel.setCurrentScreen(ActionsScreen.SCREEN_ACTIONS)
                }
            }
        }

        binding.btnBack.setOnClickListener {
            Log.d("$_TAG - onViewCreated", "Back button clicked")
            onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        sharedViewModel.categoriesList.observe(this) { categories ->
            Log.d("$_TAG - populateAll", "categoriesList updated: ${categories.toString()}")
            actionsViewModel.updateCategoriesList(categories)
        }
        sharedViewModel.groupsList.observe(this) { groups ->
            Log.d("$_TAG - populateAll", "groupsList updated: ${groups.toString()}")
            actionsViewModel.updateGroupsList(groups)
        }
    }

    override fun onResume() {
        super.onResume()
        actionsViewModel.setCurrentScreen(ActionsScreen.SCREEN_ACTIONS)
        Log.d("$_TAG - onResume", "Current Screen: ${actionsViewModel.currentScreen.value?.title}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Navigations:
    private fun onBackPressed() {
        when(actionsViewModel.currentScreen.value) {
            ActionsScreen.SCREEN_ACTIONS -> { /*TODO()*/ }
            ActionsScreen.SCREEN_CATEGORIES -> { actionsViewModel.setCurrentScreen(ActionsScreen.SCREEN_ACTIONS) }
            ActionsScreen.SCREEN_GROUPS -> { actionsViewModel.setCurrentScreen(ActionsScreen.SCREEN_ACTIONS) }
            else -> {
                Log.e("$_TAG - onViewCreated", "Error: CurrentScreen not detected")
                actionsViewModel.setCurrentScreen(ActionsScreen.SCREEN_ACTIONS)
            }
        }
    }
    private fun onCardClicked(index: Int) {
        Log.d("$_TAG - onCardClicked", "Current Screen: ${actionsViewModel.currentScreen.value?.title}. Clicked on card $index")
        when(actionsViewModel.currentScreen.value) {
            ActionsScreen.SCREEN_ACTIONS -> {
                when(index) {
                    0 -> { findNavController().navigate(R.id.action_navigation_actions_to_recordingFragment) }
                    1 -> { /*Import Audio from device*/ }
                    2 -> { actionsViewModel.setCurrentScreen(ActionsScreen.SCREEN_CATEGORIES) }
                    3 -> { actionsViewModel.setCurrentScreen(ActionsScreen.SCREEN_GROUPS) }
                    else -> {
                        Log.e("$_TAG - onCardClicked", "Error: Index out of bounds")
                        actionsViewModel.setCurrentScreen(ActionsScreen.SCREEN_ACTIONS)
                    }
                }
            }
            ActionsScreen.SCREEN_CATEGORIES -> { /* navigate to category detail screen (read-only) //TODO() */ }
            ActionsScreen.SCREEN_GROUPS -> { /* navigate to group detail screen (read-only) */ }
            else -> {
                Log.e("$_TAG - onCardClicked", "Error: CurrentScreen not detected")
                actionsViewModel.setCurrentScreen(ActionsScreen.SCREEN_ACTIONS)
            }
        }
    }

    private fun onCardLongClicked(index: Int) {
        Log.d("$_TAG - onCardLongClicked", "Current Screen: ${actionsViewModel.currentScreen.value?.title}. Long clicked on card $index.")
        Log.d("$_TAG - onCardClicked", "Current Screen: ${actionsViewModel.currentScreen.value?.title}. Clicked on card $index")
        when(actionsViewModel.currentScreen.value) {
            ActionsScreen.SCREEN_ACTIONS -> { /* do nothing */ }
            ActionsScreen.SCREEN_CATEGORIES -> { /* navigate to category detail screen (edit-mode) //TODO() */ }
            ActionsScreen.SCREEN_GROUPS -> { /* navigate to group detail screen (edit-mode) */ }
            else -> {
                Log.e("$_TAG - onCardClicked", "Error: CurrentScreen not detected")
                actionsViewModel.setCurrentScreen(ActionsScreen.SCREEN_ACTIONS)
            }
        }
    }

    private fun onFabClicked() {
        Log.d("$_TAG - onFabClicked", "Redirecting to CategoryGroupItemFragment")
        val bundle = Bundle()
        bundle.putBoolean("paramEditPermissions", true)
        bundle.putString("paramItemId", "")
        Log.d("$_TAG - onFabClicked", "Redirecting to CategoryGroupDetailFragment with bundle: $bundle")
//        findNavController().navigate(R.id.action_navigation_actions_to_categoryGroupDetailFragment, bundle)
    }
}