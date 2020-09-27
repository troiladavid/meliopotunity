package com.davidtroila.melioportunity

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import com.davidtroila.melioportunity.model.ErrorTypes
import kotlinx.android.synthetic.main.fragment_search_result.*

/**
 * Created by David Troila
 */
fun Context.createDialog (errorTypes: ErrorTypes, positiveAction: () -> Unit){
    val message = getErrorMessage(errorTypes)
    val builder = AlertDialog.Builder(this)
        .setTitle("Que mal")
        .setMessage(message)
        .setPositiveButton("OK") { _, _ -> positiveAction.invoke() }
    val alertDialog: AlertDialog = builder.create()
    alertDialog.setCancelable(false)
    alertDialog.show()
}

fun getErrorMessage(errorTypes: ErrorTypes): String {
    return when (errorTypes) {
        ErrorTypes.NETWORK -> "Hubo un error.\nRevisá tu conexión a internet e intentá de nuevo"
        ErrorTypes.SERVER -> "No pudimos completar la busqueda.\nIntentá mas tarde"
        ErrorTypes.TIMEOUT -> "La busqueda tomó mas de lo esperado y la cancelamos"
        else -> "Ocurrió un error inesperado"
    }
}

fun View.changeItemsView(button: View, action: () -> Unit){
    this.isEnabled = false
    button.isEnabled = true
    action.invoke()
}

fun View.hideKeyboard(){
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}