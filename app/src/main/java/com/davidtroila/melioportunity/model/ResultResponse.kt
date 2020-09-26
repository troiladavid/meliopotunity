package com.davidtroila.melioportunity.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by David Troila
 */
@Parcelize
data class ResultResponse (
    val result : List<Item>
) : Parcelable