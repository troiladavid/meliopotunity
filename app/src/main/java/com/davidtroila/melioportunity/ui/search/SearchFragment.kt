package com.davidtroila.melioportunity.ui.search

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.davidtroila.melioportunity.R
import com.davidtroila.melioportunity.createDialog
import com.davidtroila.melioportunity.model.ResultResponse
import com.davidtroila.melioportunity.service.VolleyService
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.progressBar
import kotlinx.android.synthetic.main.fragment_search_result.*
import kotlinx.android.synthetic.main.search_edit_text.*
import kotlinx.android.synthetic.main.search_edit_text.searchView
import timber.log.Timber

class SearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var volleyService: VolleyService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        volleyService = VolleyService(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Screen loaded")
        val viewModelFactory = SearchViewModelFactory(volleyService)
        searchViewModel = ViewModelProvider(this, viewModelFactory).get(SearchViewModel::class.java)
        searchViewModel.itemList.observe(viewLifecycleOwner, { it.getContentIfNotHandled()?.let { response ->
            navigateToResults(response)
        } })
        searchViewModel.onFailure.observe(viewLifecycleOwner, {
            progressBar.visibility = View.GONE
            requireContext().createDialog(it) {
                searchButton.isVisible = true
                searchField.isVisible = true
            }
        })

        registerListeners()
    }

    private fun registerListeners() {
        searchButton.setOnClickListener { performSearch() }

        searchField.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(searchQuery: String): Boolean {
                performSearch()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean { return false }
        })
        Timber.d("Listeners registered")
    }

    private fun navigateToResults(result: ResultResponse){
        Timber.d("Navigating to Results")
        view?.let { Navigation.findNavController(it).navigate(
            R.id.action_searchFragment_to_searchResultFragment,
            bundleOf(SEARCH_RESULT_ARGUMENT to result, QUERY_ARGUMENT to searchField.query.toString())
        ) }
    }

    private fun performSearch() {
        Timber.d("Starting search")
        if (searchField.query.isNullOrEmpty()){
            val toast = Toast.makeText(requireContext() ,"¡Tenés que buscar algo!", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 60)
            toast.show()
        } else {
            progressBar.isVisible = true
            searchButton.isVisible = false
            searchField.isVisible = false
            searchViewModel.getArticles(searchField.query.toString())
        }
    }

    companion object{
        const val QUERY_ARGUMENT = "queryArgument"
        const val SEARCH_RESULT_ARGUMENT = "searchResultArgument"
    }
}