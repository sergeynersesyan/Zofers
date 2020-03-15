package com.zofers.zofers.staff

import android.content.Context
import android.widget.Toast

object MessageHelper {

    fun showErrorToast(context: Context?, text: String = "") {
        Toast.makeText(context, "something went wrong $text", Toast.LENGTH_SHORT).show()
    }

    fun showNoConnectionToast(context: Context?) {
        showErrorToast(context, "no connection")
    }
}
