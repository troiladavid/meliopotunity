package com.davidtroila.melioportunity.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.davidtroila.melioportunity.R
import com.davidtroila.melioportunity.createDialog
import com.davidtroila.melioportunity.model.ItemAdapter
import com.davidtroila.melioportunity.model.ResultResponse
import com.davidtroila.melioportunity.service.VolleyService
import kotlinx.android.synthetic.main.fragment_search_result.*
import timber.log.Timber

class SearchResultFragment : Fragment() {

    private var itemList: ResultResponse? = null
    private var query: String? = ""
    private var offset = 0
    private var userSelect = false
    private lateinit var searchResultViewModel : SearchViewModel
    private lateinit var adapter: ItemAdapter
    private lateinit var manager: GridLayoutManager
    private lateinit var volleyService: VolleyService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
           itemList = it.getParcelable(SEARCH_RESULT_ARGUMENT)
            query = it.getString(QUERY_ARGUMENT)
        }
        volleyService = VolleyService(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_search_result, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Screen loaded")

        //Setup viewModel
        val viewModelFactory = SearchViewModelFactory(volleyService)
        searchResultViewModel = ViewModelProvider(this, viewModelFactory).get(SearchViewModel::class.java)

        listButton.isEnabled = false

        //setup RecyclerView
        manager = GridLayoutManager(activity, 1)
        searchItemsList.layoutManager = manager

        adapter = ItemAdapter(manager, requireContext())
        searchItemsList.adapter = adapter
        itemList?.let { loadSearchResults(it) }
        populateSortSpinner()
        registerListeners()
        registerObservers()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    /***
     * Add options to sort spinner
     */
    private fun populateSortSpinner(){
        val sortOptions = resources.getStringArray(R.array.filter_sorted_by)
        sortSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            sortOptions
        )
    }

    /***
     * Sets up all listeners for this view
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun registerListeners() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(searchQuery: String): Boolean {
                progressBar.isVisible = true
                offset = 0
                query = searchQuery
                searchResultViewModel.getArticles(searchQuery)
                return false
            }
        })

        sortSpinner.setOnTouchListener { _, _ ->
            userSelect = true
            false
        }

        sortSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (userSelect) {
                    Timber.d("Spinner item selected")
                    val sortArray = resources.getStringArray(R.array.sort_array)
                    val sort = when (p2) {
                        1 -> null
                        2 -> sortArray[0]
                        3 -> sortArray[1]
                        else -> ""
                    }
                    query?.let { searchResultViewModel.getArticles(it, sort = sort) }
                    userSelect = false
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        gridButton.setOnClickListener{
            gridButton.isEnabled = false
            manager.spanCount = 2
            adapter.notifyItemRangeChanged(0, adapter.itemCount)
            listButton.isEnabled = true
        }

        listButton.setOnClickListener{
            listButton.isEnabled = false
            manager.spanCount = 1
            adapter.notifyItemRangeChanged(0, adapter.itemCount)
            gridButton.isEnabled = true
        }

        moreButton.setOnClickListener {
            progressBar.isVisible = true
            offset += 20
            progressBar.visibility = View.VISIBLE
            query?.let {
                searchResultViewModel.getArticles(it, offset = offset)}
        }

        backButton.setOnClickListener {
            progressBar.isVisible = true
            offset -= 20
            query?.let {
                searchResultViewModel.getArticles(it, offset = offset)}
        }
        Timber.d("Listeners registered")
    }


    /***
     * Sets up observers over viewModel LiveData
     */
    private fun registerObservers(){
        searchResultViewModel.itemList.observe(viewLifecycleOwner, {
            loadSearchResults(it.peekContent())
        })
        searchResultViewModel.onFailure.observe(viewLifecycleOwner, {
            progressBar.isVisible = false
            requireContext().createDialog(it) { backButton.isVisible = offset != 0 }
        })
        Timber.d("Observers registered")
    }

    /***
     * Loads search results on screen with the call response
     */
    private fun loadSearchResults(response: ResultResponse){
        progressBar.isVisible = false
        if (response.result.isEmpty()){
            Timber.d("Results is empty")
            shrugImageView.isVisible = true
            noResultsTextView.isVisible = true
            searchItemsList.isVisible = false
        } else {
            Timber.d("Showing Results")
            backButton.isVisible = offset !=0
            shrugImageView.isVisible = false
            noResultsTextView.isVisible = false
            searchItemsList.isVisible = true
            adapter.submitList(response.result)
        }
    }

    companion object{
        const val QUERY_ARGUMENT = "queryArgument"
        const val SEARCH_RESULT_ARGUMENT = "searchResultArgument"
    }
}