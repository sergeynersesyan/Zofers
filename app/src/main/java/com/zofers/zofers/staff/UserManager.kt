package com.zofers.zofers.staff

import android.content.Intent

import com.google.gson.Gson
import com.zofers.zofers.ui.login.LoginActivity
import com.zofers.zofers.model.Profile

class UserManager(private val preferences: PreferenceService) {
	private val KEY_AUTH_TOKEN = "k_a_tok"
	private val KEY_PROFILE = "k_prof"
	private var authToken: String? = null

	var userProfile: Profile?
		get() {
			return Gson().fromJson(preferences.getString(KEY_PROFILE, null), Profile::class.java)
		}
		set(value) {
			preferences.setString(KEY_PROFILE, Gson().toJson(value))
		}

	fun getAuthToken(): String? {
		if (authToken == null || authToken!!.length == 0) {
			authToken = preferences.getString(KEY_AUTH_TOKEN, null)
		}
		return authToken
	}

	fun setAuthToken(authToken: String?) {
		this.authToken = authToken
		preferences.setString(KEY_AUTH_TOKEN, authToken)
	}

	fun hasAuthorized(): Boolean {
		return getAuthToken() != null
	}

	fun asumaUnauthorised() {
		setAuthToken(null)
		val intent = Intent(preferences.context, LoginActivity::class.java)
		preferences.context.startActivity(intent)
	}
}
