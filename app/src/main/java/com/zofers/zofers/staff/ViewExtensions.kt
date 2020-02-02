package com.zofers.zofers.staff

import android.widget.Button
import com.zofers.zofers.R

fun Button.disable () {
	this.isEnabled = false
	this.setTextColor(this.context.resources.getColor(R.color.gray_black))
}

fun Button.enable () {
	this.isEnabled = true
	this.setTextColor(this.context.resources.getColor(R.color.white))
}