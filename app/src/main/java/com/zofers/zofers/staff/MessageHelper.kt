package com.zofers.zofers.staff

import android.content.Context
import android.widget.Toast
import java.time.Duration

object MessageHelper {

    fun showErrorToast(context: Context?, text: String = "", duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, "something went wrong $text", duration).show()
    }

    fun showNoConnectionToast(context: Context?) {
        showErrorToast(context, "no connection")
    }
}
