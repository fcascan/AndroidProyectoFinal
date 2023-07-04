package com.fcascan.proyectofinal.ui.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcascan.proyectofinal.R
import com.fcascan.proyectofinal.activities.MainActivity
import com.fcascan.proyectofinal.adapters.ItemsAdapter
import com.fcascan.proyectofinal.enums.LoadingState
import com.fcascan.proyectofinal.enums.PlaybackState
import com.fcascan.proyectofinal.shared.SharedViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.io.File


class DashboardFragment : Fragment() {
    private val _TAG = "FCC#DashboardFragment"

    //View Elements:
    private lateinit var v : View
    private lateinit var txtNoContent: TextView
    private lateinit var searchViewDashboard: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recViewAdapter: ItemsAdapter
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerGroup: Spinner

    //ViewModels:
    private lateinit var dashboardViewModel: DashboardViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //ViewModel:
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]

        v = inflater.inflate(R.layout.fragment_dashboard, container, false)
        txtNoContent = v.findViewById(R.id.txtNoContent)
        searchViewDashboard = v.findViewById(R.id.searchViewDashboard)
        recyclerView = v.findViewById(R.id.recViewDashboard)
        spinnerCategory = v.findViewById(R.id.spinnerCategory)
        spinnerGroup = v.findViewById(R.id.spinnerGroup)

        //RecyclerView Config:
        recyclerView.setHasFixedSize(false)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val directory = File(requireContext().filesDir, "audios")

        //LiveData Observers:
        dashboardViewModel.recViewContent.observe(viewLifecycleOwner) {items ->
            Log.d("$_TAG - onViewCreated", "adapterList updated: ${items.toString()}")
            if (items?.isEmpty() == true) { txtNoContent.visibility = View.VISIBLE }
            else { txtNoContent.visibility = View.INVISIBLE }
            recViewAdapter = ItemsAdapter(
                itemsList = items!!,
                onClick = { index -> onCardClicked(index) },
                onLongClick = { index -> onCardLongClicked(index) },
                onPlayClicked = { index ->
                    val file = File(directory, "${dashboardViewModel.filteredItemsList[index].documentId!!}.opus")
                    sharedViewModel.playFile(file) {
                        Log.d("$_TAG - onViewCreated", "Play Audio onCompletionListener index: $index")
                        val viewHolder = recyclerView.findViewHolderForAdapterPosition(index) as? ItemsAdapter.ItemsHolder
                        viewHolder?.resetPlayButton()
                    }
                },
                onPauseClicked = { index -> sharedViewModel.pausePlayback() },
                onStopClicked = { index -> sharedViewModel.stopPlayback() },
                onShareClicked = { index ->
                    val file = File(directory, "${dashboardViewModel.filteredItemsList[index].documentId!!}.opus")
                    val fileUri = FileProvider.getUriForFile(requireContext(), "com.fcascan.proyectofinal.fileprovider", file)
                    onShareClicked(fileUri)
                },
            )
            recyclerView.layoutManager = GridLayoutManager(context, 2)
            recyclerView.adapter = recViewAdapter
        }

        dashboardViewModel.spinnerCategoriesContent.observe(viewLifecycleOwner) {categories ->
            Log.d("$_TAG - onViewCreated", "spinnerCategoriesContent updated: ${categories.toString()}")
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
            Log.d("$_TAG - onViewCreated", "spinnerGroupsContent updated: ${groups.toString()}")
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
                Log.d("$_TAG - onViewCreated", "searchViewDashboard pressed enter")
                dashboardViewModel.setSearchQuery(searchViewDashboard.query.toString())
                sharedViewModel.getItemsList()?.let { dashboardViewModel.filterRecViewContent(it) }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("$_TAG - onViewCreated", "searchViewDashboard text changed")
                dashboardViewModel.setSearchQuery(searchViewDashboard.query.toString())
                sharedViewModel.getItemsList()?.let { dashboardViewModel.filterRecViewContent(it) }
                return false
            }
        })

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("$_TAG - onViewCreated", "spinnerCategory Nothing selected: ${spinnerCategory.selectedItem}")
                dashboardViewModel.setSelectedCategory(0)
                sharedViewModel.getItemsList()?.let { dashboardViewModel.filterRecViewContent(it) }
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.d("$_TAG - onViewCreated", "spinnerCategory selected: ${spinnerCategory.selectedItem}")
                dashboardViewModel.setSelectedCategory(spinnerCategory.selectedItemPosition)
                sharedViewModel.getItemsList()?.let { dashboardViewModel.filterRecViewContent(it) }
            }
        }

        spinnerGroup.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("$_TAG - onViewCreated", "spinnerGroup Nothing selected: ${spinnerGroup.selectedItem}")
                dashboardViewModel.setSelectedGroup(0)
                sharedViewModel.getItemsList()?.let { dashboardViewModel.filterRecViewContent(it) }
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.d("$_TAG - onViewCreated", "spinnerGroup selected: ${spinnerGroup.selectedItem}")
                dashboardViewModel.setSelectedGroup(spinnerGroup.selectedItemPosition)
                sharedViewModel.getItemsList()?.let { dashboardViewModel.filterRecViewContent(it) }

            }
        }
    }

    override fun onStart() {
        super.onStart()
        populateAll()
        clearFilters()
    }

    override fun onResume() {
        super.onResume()
        dashboardViewModel.setSearchQuery("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dashboardViewModel.recViewContent.removeObservers(this)
        dashboardViewModel.spinnerCategoriesContent.removeObservers(this)
        dashboardViewModel.spinnerGroupsContent.removeObservers(this)
//        sharedViewModel.destroyMediaPlayer()
    }


    //Private Functions:
    private fun populateAll() {
        sharedViewModel.itemsList.observe(this) { items ->
            Log.d("$_TAG - populateAll", "itemsList updated: ${items.toString()}")
            dashboardViewModel.updateAdapterList(items)
        }
        sharedViewModel.categoriesList.observe(this) { categories ->
            Log.d("$_TAG - populateAll", "categoriesList updated: ${categories.toString()}")
            dashboardViewModel.updateCategoriesList(categories)
        }
        sharedViewModel.groupsList.observe(this) { groups ->
            Log.d("$_TAG - populateAll", "groupsList updated: ${groups.toString()}")
            dashboardViewModel.updateGroupsList(groups)
        }
    }

    private fun clearFilters() {
        spinnerCategory.setSelection(0)
        spinnerGroup.setSelection(0)
        searchViewDashboard.setQuery("", false)
    }


    //Navigations:
    private fun onCardClicked(index: Int) {
        Log.d("$_TAG - onCardClicked", "Clicked on card $index. Redirecting to ItemFragment (Read-Only)")
        val bundle = Bundle()
        bundle.putBoolean("paramEditPermissions", false)
        bundle.putString("paramItemId", dashboardViewModel.filteredItemsList[index].documentId.toString())
        Log.d("$_TAG - onCardClicked", "Redirecting to ItemDetailFragment with bundle: $bundle")
        Navigation.findNavController(v).navigate(R.id.action_navigation_dashboard_to_itemDetailFragment, bundle)
    }

    private fun onCardLongClicked(index: Int) {
        Log.d("$_TAG - onCardLongClicked", "LongClicked on card $index. Redirecting to ItemFragment (Edit-Mode)")
        val bundle = Bundle()
        bundle.putBoolean("paramEditPermissions", true)
        bundle.putString("paramItemId", dashboardViewModel.filteredItemsList[index].documentId.toString())
        Log.d("$_TAG - onCardLongClicked", "Redirecting to ItemDetailFragment with bundle: $bundle")
        Navigation.findNavController(v).navigate(R.id.action_navigation_dashboard_to_itemDetailFragment, bundle)
    }

    fun onShareClicked(fileUri: Uri) {
        Log.d("$_TAG - onShareClicked", "Sharing fileUri: ${fileUri}")
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "audio/opus"
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val chooserIntent = Intent.createChooser(shareIntent, "Share via")
        if (shareIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(chooserIntent)
        }
    }
}