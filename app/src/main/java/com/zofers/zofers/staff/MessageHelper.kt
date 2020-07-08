package com.zofers.zofers.staff

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar


object MessageHelper {

    fun showErrorToast(context: Context?, text: String = "", duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, "something went wrong $text", duration).show()
    }

    fun showNoConnectionToast(context: Context?) {
        showErrorToast(context, "no connection")
    }

    fun showSnackBar(view: View, text: String, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show()
    }


}
