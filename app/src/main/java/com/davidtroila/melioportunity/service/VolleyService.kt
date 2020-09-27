package com.davidtroila.melioportunity.service

import android.content.Context
import android.net.Uri.Builder
import androidx.lifecycle.MutableLiveData
import com.android.volley.*
import com.android.volley.toolbox.*
import com.davidtroila.melioportunity.EventWrapper
import com.davidtroila.melioportunity.model.ErrorTypes
import com.davidtroila.melioportunity.model.Item
import com.davidtroila.melioportunity.model.ResultResponse
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

/**
 * Created by David Troila
 */
class VolleyService(private val context: Context): VolleyInterface{
    private lateinit var requestQueue: RequestQueue

    override suspend fun makeGetCall(
        query: String,
        offset: Int?,
        sort: String?,
        onSuccess: MutableLiveData<EventWrapper<ResultResponse>>,
        onFailure: MutableLiveData<ErrorTypes>?
    ){
        val url = buildURL(query, offset, sort)

        requestQueue = VolleySingleton.getInstance(context).requestQueue

        requestQueue.cancelAll("TAG")
        val itemList = mutableListOf<Item>()
        val stringRequest = StringRequest(url,
            { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val jsonArray = jsonObject.getJSONArray("results")
                    for (i in 0 until jsonArray.length()) {
                        val c = jsonArray.getJSONObject(i)
                        val item = parseItems(c)
                        itemList.add(item)
                    }
                    Timber.d("Result: $itemList")
                    onSuccess.value = EventWrapper(ResultResponse(itemList))
                } catch (e: JSONException) {
                    Timber.d("JSONExcepton ${e.printStackTrace()}")
                }
            },
            { error ->
                Timber.d("VolleyException: ${error.localizedMessage}")
                onFailure?.value = onErrorResponse(error) })

        stringRequest.retryPolicy = DefaultRetryPolicy(5000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
        Timber.d("Search URL: $url")
    }

    private fun buildURL(query: String, offset: Int?, sort: String?): String{
        val builder = Builder()
        builder.scheme("https")
            .authority("api.mercadolibre.com")
            .appendPath("sites")
            .appendPath("MLA")
            .appendPath("search")
            .appendQueryParameter("q", query)
            .appendQueryParameter("offset", offset.toString())
            .appendQueryParameter("limit", "20")
        sort?.let { builder.appendQueryParameter("sort",sort) }
        return builder.build().toString()
    }

    private fun parseItems(c: JSONObject): Item{
        return Item(
            id = c.getString("id"),
            state = c.getString("condition"),
            title = c.getString("title"),
            imageURL = c.getString("thumbnail"),
            acceptMercadoPago = c.getString("accepts_mercadopago"),
            permalink = c.getString("permalink"),
            city = (c.getJSONObject("address").getString("city_name")),
            price = c.getInt("price").toString(),
            shipping = c.getJSONObject("shipping").getString("free_shipping")
        )
    }

    private fun onErrorResponse(error: VolleyError): ErrorTypes{
        return when (error) {
            is TimeoutError -> ErrorTypes.TIMEOUT
            is ServerError -> ErrorTypes.SERVER
            is NetworkError -> ErrorTypes.NETWORK
            else -> ErrorTypes.DEFAULT
        }
    }
}