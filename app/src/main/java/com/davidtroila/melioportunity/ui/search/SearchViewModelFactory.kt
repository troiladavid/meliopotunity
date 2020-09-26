package com.davidtroila.melioportunity.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.davidtroila.melioportunity.service.VolleyService

class SearchViewModelFactory(private val volleyService: VolleyService): ViewModelProvider.Factory{
    @Suppress("Unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(volleyService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}