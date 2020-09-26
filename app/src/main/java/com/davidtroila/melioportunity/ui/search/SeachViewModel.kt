package com.davidtroila.melioportunity.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.davidtroila.melioportunity.EventWrapper
import com.davidtroila.melioportunity.model.ErrorTypes
import com.davidtroila.melioportunity.model.ResultResponse
import com.davidtroila.melioportunity.service.VolleyService
import kotlinx.coroutines.*

class SearchViewModel (private val volleyService: VolleyService): ViewModel(){

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  viewModelJob)

    private val _itemList = MutableLiveData<EventWrapper<ResultResponse>>()
    private val _onFailure = MutableLiveData<ErrorTypes>()

    val itemList: LiveData<EventWrapper<ResultResponse>>
        get() = _itemList

    val onFailure: LiveData<ErrorTypes>
        get() = _onFailure

    /***
     * Makes a get call to get an item list
     */
    fun getArticles(query: String, offset: Int?= 0, sort: String? = null){
        uiScope.launch {
            withContext(Dispatchers.Main){
                volleyService.makeGetCall(query, offset, sort, _itemList, _onFailure)
            }
        }
    }

    override fun onCleared() {
        viewModelJob.cancel()
    }

}