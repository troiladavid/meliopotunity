package com.davidtroila.melioportunity.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Item (
    val id: String,
    val state: String,
    val city: String,
    var imageURL: String,
    var acceptMercadoPago: String,
    var permalink: String,
    val price: String,
    var shipping: String,
    val title: String
): Parcelable