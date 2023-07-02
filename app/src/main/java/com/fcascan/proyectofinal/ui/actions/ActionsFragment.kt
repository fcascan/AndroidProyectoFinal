package com.fcascan.proyectofinal.ui.actions

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcascan.proyectofinal.R
import com.fcascan.proyectofinal.adapters.ActionsAdapter
import com.fcascan.proyectofinal.enums.ActionsScreen
import com.fcascan.proyectofinal.shared.SharedViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ActionsFragment : Fragment() {
    private val _className = "FCC#ActionsFragment"

    //View Elements:
    private lateinit var v : View
    private lateinit var btnBack : Button
    private lateinit var txtActionsScreenTitle : TextView
    private lateinit var fabActions: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var recViewAdapter: ActionsAdapter

    //ViewModels:
    private lateinit var actionsViewModel: ActionsViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        actionsViewModel = ViewModelProvider(this)[ActionsViewModel::class.java]

        v = inflater.inflate(R.layout.fragment_actions, container, false)
        btnBack = v.findViewById(R.id.btnBack)
        txtActionsScreenTitle = v.findViewById(R.id.txtActionsScreenTitle)
        fabActions = v.findViewById(R.id.fabActions)
        recyclerView = v.findViewById(R.id.recViewActions)

        //RecyclerView Config:
        recyclerView.setHasFixedSize(true)

        return v
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
            Log.d("$_className - onViewCreated", "RecViewContent changed")
            recViewAdapter = ActionsAdapter(
                actionsList = content!!,
                onClick = { index -> onCardClicked(index) },
                onLongClick = { index -> onCardLongClicked(index) }
            )
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = recViewAdapter
        }

        actionsViewModel.currentScreen.observe(viewLifecycleOwner) { screen ->
            Log.d("$_className - onViewCreated", "CurrentScreen changed")
            actionsViewModel.changeRecViewContent()
            when(screen) {
                ActionsScreen.SCREEN_ACTIONS -> {
                    txtActionsScreenTitle.text = ActionsScreen.SCREEN_ACTIONS.title
                    fabActions.visibility = View.GONE
                    btnBack.visibility = View.GONE
                    recyclerView.layoutManager = LinearLayoutManager(context)
                }
                ActionsScreen.SCREEN_CATEGORIES -> {
                    txtActionsScreenTitle.text = ActionsScreen.SCREEN_CATEGORIES.title
                    fabActions.visibility = View.VISIBLE
                    btnBack.visibility = View.VISIBLE
                    recyclerView.layoutManager = GridLayoutManager(context, 2)
                }
                ActionsScreen.SCREEN_GROUPS -> {
                    txtActionsScreenTitle.text = ActionsScreen.SCREEN_GROUPS.title
                    fabActions.visibility = View.VISIBLE
                    btnBack.visibility = View.VISIBLE
                    recyclerView.layoutManager = GridLayoutManager(context, 2)
                }
                else -> {
                    Log.e("$_className - onViewCreated", "Error: CurrentScreen not detected")
                    actionsViewModel.setCurrentScreen(ActionsScreen.SCREEN_ACTIONS)
                }
            }
        }


        fabActions.setOnClickListener {
            Log.d("$_className - onViewCreated", "FAB clicked")
            when(actionsViewModel.currentScreen.value) {
                ActionsScreen.SCREEN_ACTIONS -> { /*TODO()*/ }
                ActionsScreen.SCREEN_CATEGORIES -> { /*TODO()*/ }
                ActionsScreen.SCREEN_GROUPS -> { /*TODO()*/ }
                else -> {
                    Log.e("$_className - onViewCreated", "Error: CurrentScreen not detected")
                    actionsViewModel.setCurrentScreen(ActionsScreen.SCREEN_ACTIONS)
                }
            }
        }

        btnBack.setOnClickListener {
            Log.d("$_className - onViewCreated", "Back button clicked")
            onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        sharedViewModel.categoriesList.observe(this) { categories ->
            Log.d("$_className - populateAll", "categoriesList updated: ${categories.toString()}")
            actionsViewModel.updateCategoriesList(categories)
        }
        sharedViewModel.groupsList.observe(this) { groups ->
            Log.d("$_className - populateAll", "groupsList updated: ${groups.toString()}")
            actionsViewModel.updateGroupsList(groups)
        }
    }

    override fun onResume() {
        super.onResume()
        actionsViewModel.setCurrentScreen(ActionsScreen.SCREEN_ACTIONS)
        Log.d("$_className - onResume", "Current Screen: ${actionsViewModel.currentScreen.value?.title}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    //Navigations:
    private fun onBackPressed() {
        when(actionsViewModel.currentScreen.value) {
            ActionsScreen.SCREEN_ACTIONS -> { /*TODO()*/ }
            ActionsScreen.SCREEN_CATEGORIES -> { actionsViewModel.setCurrentScreen(ActionsScreen.SCREEN_ACTIONS) }
            ActionsScreen.SCREEN_GROUPS -> { actionsViewModel.setCurrentScreen(ActionsScreen.SCREEN_ACTIONS) }
            else -> {
                Log.e("$_className - onViewCreated", "Error: CurrentScreen not detected")
                actionsViewModel.setCurrentScreen(ActionsScreen.SCREEN_ACTIONS)
            }
        }
    }
    private fun onCardClicked(index: Int) {
        Log.d("$_className - onCardClicked", "Current Screen: ${actionsViewModel.currentScreen.value?.title}. Clicked on card $index")
        when(actionsViewModel.currentScreen.value) {
            ActionsScreen.SCREEN_ACTIONS -> {
                when(index) {
                    0 -> { findNavController().navigate(R.id.action_navigation_actions_to_recordingFragment) }
                    1 -> { /*Import Audio from device*/ }
                    2 -> { actionsViewModel.setCurrentScreen(ActionsScreen.SCREEN_CATEGORIES) }
                    3 -> { actionsViewModel.setCurrentScreen(ActionsScreen.SCREEN_GROUPS) }
                    else -> {
                        Log.e("$_className - onCardClicked", "Error: Index out of bounds")
                        actionsViewModel.setCurrentScreen(ActionsScreen.SCREEN_ACTIONS)
                    }
                }
            }
            ActionsScreen.SCREEN_CATEGORIES -> { /* navigate to category detail screen (read-only) //TODO() */ }
            ActionsScreen.SCREEN_GROUPS -> { /* navigate to group detail screen (read-only) */ }
            else -> {
                Log.e("$_className - onCardClicked", "Error: CurrentScreen not detected")
                actionsViewModel.setCurrentScreen(ActionsScreen.SCREEN_ACTIONS)
            }
        }
    }

    private fun onCardLongClicked(index: Int) {
        Log.d("$_className - onCardLongClicked", "Current Screen: ${actionsViewModel.currentScreen.value?.title}. Long clicked on card $index.")
        Log.d("$_className - onCardClicked", "Current Screen: ${actionsViewModel.currentScreen.value?.title}. Clicked on card $index")
        when(actionsViewModel.currentScreen.value) {
            ActionsScreen.SCREEN_ACTIONS -> { /* do nothing */ }
            ActionsScreen.SCREEN_CATEGORIES -> { /* navigate to category detail screen (edit-mode) //TODO() */ }
            ActionsScreen.SCREEN_GROUPS -> { /* navigate to group detail screen (edit-mode) */ }
            else -> {
                Log.e("$_className - onCardClicked", "Error: CurrentScreen not detected")
                actionsViewModel.setCurrentScreen(ActionsScreen.SCREEN_ACTIONS)
            }
        }
    }

    private fun onFabClicked() {
        Log.d("$_className - onFabClicked", "Redirecting to CategoryGroupItemFragment")
        val bundle = Bundle()
        bundle.putBoolean("paramEditPermissions", true)
        bundle.putString("paramItemId", "")
        Log.d("$_className - onFabClicked", "Redirecting to CategoryGroupDetailFragment with bundle: $bundle")
//        findNavController().navigate(R.id.action_navigation_actions_to_categoryGroupDetailFragment, bundle)
    }
}