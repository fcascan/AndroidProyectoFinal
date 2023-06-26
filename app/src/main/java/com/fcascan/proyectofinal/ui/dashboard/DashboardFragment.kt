package com.fcascan.proyectofinal.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Spinner
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcascan.proyectofinal.R
import com.fcascan.proyectofinal.activities.MainActivity
import com.fcascan.proyectofinal.adapters.ItemsAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton


class DashboardFragment : Fragment() {
    private val _className = "FCC#DashboardFragment"

//    private lateinit var dashboardViewModel: DashboardViewModel
    private val dashboardViewModel: DashboardViewModel by lazy {
        DashboardViewModel((requireActivity() as MainActivity).mainActivityViewModel)
    }

    //View Elements:
    private lateinit var v : View
    private lateinit var searchViewDashboard: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recViewAdapter: ItemsAdapter
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerGroup: Spinner
    private lateinit var fab: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.fragment_dashboard, container, false)
        searchViewDashboard = v.findViewById(R.id.searchViewDashboard)
        recyclerView = v.findViewById(R.id.recViewDashboard)
        spinnerCategory = v.findViewById(R.id.spinnerCategory)
        spinnerGroup = v.findViewById(R.id.spinnerGroup)
        fab = v.findViewById(R.id.fabDashboard)

        //RecyclerView Config:
        recyclerView.setHasFixedSize(false)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //LiveData Observers:
        dashboardViewModel.recViewContent.observe(viewLifecycleOwner) {items ->
            Log.d("$_className - onViewCreated", "adapterList updated: ${items.toString()}")
            recViewAdapter = ItemsAdapter(
                itemsList = items!!,
                onClick = { index -> onCardClicked(index) },
                onLongClick = { index -> onCardLongClicked(index) },
                onPlayClicked = { index -> dashboardViewModel.onPlayClicked(index) },
                onStopClicked = { index -> dashboardViewModel.onStopClicked(index) },
                onShareClicked = { index -> dashboardViewModel.onShareClicked(index) }
            )
            recyclerView.layoutManager = GridLayoutManager(context, 2)
            recyclerView.adapter = recViewAdapter
        }

        dashboardViewModel.spinnerCategoriesContent.observe(viewLifecycleOwner) {categories ->
            Log.d("$_className - onViewCreated", "spinnerCategoriesContent updated: ${categories.toString()}")
            if (categories != null) {
                categories.add(0, "all")
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    categories
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerCategory.adapter = adapter
                }
            }
        }

        dashboardViewModel.spinnerGroupsContent.observe(viewLifecycleOwner) {groups ->
            Log.d("$_className - onViewCreated", "spinnerGroupsContent updated: ${groups.toString()}")
            if (groups != null) {
                groups.add(0, "all")
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    groups
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerGroup.adapter = adapter
                }
            }
        }

        //Event Listeners:
        searchViewDashboard.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("$_className - onViewCreated", "searchViewDashboard pressed enter")
                if (searchViewDashboard.query.toString().isNotEmpty()) {
                    dashboardViewModel.filterItemsByName(searchViewDashboard.query.toString())
                }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("$_className - onViewCreated", "searchViewDashboard text changed")
                if (searchViewDashboard.query.isNotEmpty()) {
                    dashboardViewModel.filterItemsByName(searchViewDashboard.query.toString())
                }
                return false
            }
        })

//        searchViewDashboard.setOnKeyListener { v, keyCode, event ->
//            if (event.action == android.view.KeyEvent.ACTION_DOWN && event.keyCode == android.view.KeyEvent.KEYCODE_ENTER) {
//                Log.d("$_className - onViewCreated", "searchViewDashboard pressed enter")
//                if (searchViewDashboard. text.toString().isNotEmpty()) {
//                    dashboardViewModel.filterItemsByName(searchViewDashboard.query.toString())
//                }
//                false
//            } else {
//                false
//            }
//        }

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("$_className - onViewCreated", "spinnerCategory Nothing selected: ${spinnerCategory.selectedItem}")
                dashboardViewModel.filterItemsByCategory(null)
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.d("$_className - onViewCreated", "spinnerCategory selected: ${spinnerCategory.selectedItem}")
                dashboardViewModel.filterItemsByCategory(spinnerCategory.selectedItem.toString())
            }
        }

        spinnerGroup.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("$_className - onViewCreated", "spinnerGroup Nothing selected: ${spinnerGroup.selectedItem}")
                dashboardViewModel.filterItemsByGroup(null)
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.d("$_className - onViewCreated", "spinnerGroup selected: ${spinnerGroup.selectedItem}")
                dashboardViewModel.filterItemsByGroup(spinnerGroup.selectedItem.toString())
            }
        }

        fab.setOnClickListener {
            onFabClicked()
        }
    }

    override fun onStart() {
        super.onStart()
        dashboardViewModel.populateDashboard(this)
        clearFilters()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dashboardViewModel.recViewContent.removeObservers(this)
        dashboardViewModel.spinnerCategoriesContent.removeObservers(this)
        dashboardViewModel.spinnerGroupsContent.removeObservers(this)
    }

    //Private Functions:
    private fun clearFilters() {
        spinnerCategory.setSelection(0)
        spinnerGroup.setSelection(0)
        searchViewDashboard.setQuery("", false)
    }

    //Navigations:
    private fun onCardClicked(index: Int) {
        Log.d("$_className - onCardClicked", "Clicked on card $index. Redirecting to ItemFragment (Read-Only)")
        val bundle = Bundle()
        bundle.putBoolean("editPermissions", false)
        //TODO() esto no funciona, hay que pasarle el id del item
//        bundle.putString("itemId", dashboardViewModel.recViewContent.value!![index].id.toString())
        Navigation.findNavController(v).navigate(R.id.action_navigation_dashboard_to_itemDetailFragment, bundle)
    }

    private fun onCardLongClicked(index: Int) {
        Log.d("$_className - onCardLongClicked", "LongClicked on card $index. Redirecting to ItemFragment (Edit-Mode)")
        val bundle = Bundle()
        bundle.putBoolean("editPermissions", true)
        //TODO() esto no funciona, hay que pasarle el id del item
//        bundle.putString("itemId", itemsList[index])
        Navigation.findNavController(v).navigate(R.id.action_navigation_dashboard_to_itemDetailFragment, bundle)
    }

    private fun onFabClicked() {
        Log.d("$_className - onFabClicked", "Redirecting to AddItemFragment")
        val bundle = Bundle()
        bundle.putBoolean("editPermissions", true)
        bundle.putString("itemId", "")
        findNavController().navigate(R.id.action_navigation_dashboard_to_itemDetailFragment, bundle)
    }
}