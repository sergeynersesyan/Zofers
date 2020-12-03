package com.zofers.zofers.staff

import android.content.Context
import android.content.SharedPreferences

class PreferenceService(val context: Context) {

	val preferences: SharedPreferences
		get() = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

	fun setString(key: String, value: String?) {
		preferences.edit().putString(key, value).apply()
	}

	fun getString(key: String, defValue: String?): String? {
		return preferences.getString(key, defValue)
	}

	fun setBoolean(key: String, value: Boolean) {
		preferences.edit().putBoolean(key, value).apply()
	}

	fun getBoolean(key: String, defValue: Boolean): Boolean {
		return preferences.getBoolean(key, defValue)
	}
}
