package com.davidtroila.melioportunity.service

import android.content.Context
import android.net.Uri.Builder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.davidtroila.melioportunity.EventWrapper
import com.davidtroila.melioportunity.model.ErrorTypes
import com.davidtroila.melioportunity.model.Item
import com.davidtroila.melioportunity.model.ResultResponse
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber


class VolleyService(private val context: Context): VolleyInterface{
    private lateinit var requestQueue: RequestQueue

    override suspend fun makeGetCall(
        query: String,
        offset: Int?,
        sort: String?,
        onSuccess: MutableLiveData<EventWrapper<ResultResponse>>,
        onFailure: MutableLiveData<ErrorTypes>?
    ){
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
        val url: String = builder.build().toString()

        requestQueue = Volley.newRequestQueue(context)
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

        requestQueue.add(stringRequest)
        Timber.d("Search URL: $url")
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
            shipping = c.getJSONObject("shipping").getString("free_shipping"),
            //brand = c.getJSONArray("attributes").getJSONObject(0).getString("value_name")
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