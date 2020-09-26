package com.davidtroila.melioportunity

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import com.davidtroila.melioportunity.model.ErrorTypes

fun Context.createDialog (errorTypes: ErrorTypes, positiveAction: () -> Unit){
    val message = getErrorMessage(errorTypes)
    val builder = AlertDialog.Builder(this)
        .setTitle("QUE MAL")
        .setMessage(message)
        .setPositiveButton("OK") { _, _ -> positiveAction.invoke() }
    val alertDialog: AlertDialog = builder.create()
    alertDialog.setCancelable(false)
    alertDialog.show()
}

fun getErrorMessage(errorTypes: ErrorTypes): String {
    return when (errorTypes) {
        ErrorTypes.NETWORK -> "Hubo un error. \n Revisá tu conexión a internet e intentá de nuevo"
        ErrorTypes.SERVER -> "No pudimos completar la busqueda. Intentá mas tarde"
        ErrorTypes.TIMEOUT -> "La busqueda tomó mas de lo esperado y la cancelamos"
        else -> "Ocurrió un error inesperado"
    }
}

fun View.hideKeyboard(){
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}