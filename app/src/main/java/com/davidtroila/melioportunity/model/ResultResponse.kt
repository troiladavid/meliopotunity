package com.davidtroila.melioportunity.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResultResponse (
    val result : List<Item>
) : Parcelable