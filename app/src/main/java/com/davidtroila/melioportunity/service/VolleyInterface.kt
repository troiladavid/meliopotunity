package com.davidtroila.melioportunity.service

import androidx.lifecycle.MutableLiveData
import com.davidtroila.melioportunity.EventWrapper
import com.davidtroila.melioportunity.model.ErrorTypes
import com.davidtroila.melioportunity.model.ResultResponse

/**
 * Created by David Troila
 */
interface VolleyInterface {

    suspend fun makeGetCall(query: String, offset: Int? = 0, sort: String? = null, onSuccess: MutableLiveData<EventWrapper<ResultResponse>>, onFailure: MutableLiveData<ErrorTypes>?)

}