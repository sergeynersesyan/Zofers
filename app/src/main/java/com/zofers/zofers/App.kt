package com.zofers.zofers

import android.app.Application
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.zofers.zofers.firebase.FirebaseService
import com.zofers.zofers.service.android.FirebaseMessagingService
import com.zofers.zofers.staff.PreferenceService
import com.zofers.zofers.staff.UserManager

/**
 * Created by Mr Nersesyan on 26/08/2018.
 */

class App : Application() {
	lateinit var preferenceService: PreferenceService
	lateinit var userManager: UserManager
	var isMessengerActive = false

	override fun onCreate() {
		super.onCreate()
		initFCM()
		preferenceService = PreferenceService(applicationContext)
		userManager = UserManager(preferenceService)
		instance = this
	}

	companion object {
		lateinit var instance: App
	}

	private fun initFCM() {
		val token = FirebaseInstanceId.getInstance().token
		FirebaseService().saveDeviceToken(token)
	}

}
