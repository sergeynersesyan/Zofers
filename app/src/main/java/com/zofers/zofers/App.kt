package com.zofers.zofers

import android.app.Application
import com.zofers.zofers.staff.PreferenceService
import com.zofers.zofers.staff.UserManager

/**
 * Created by Mr Nersesyan on 26/08/2018.
 */

class App : Application() {
	lateinit var preferenceService: PreferenceService
	lateinit var userManager: UserManager

	override fun onCreate() {
		super.onCreate()
		preferenceService = PreferenceService(applicationContext)
		userManager = UserManager(preferenceService)
		instance = this
	}

	companion object {
		lateinit var instance: App
	}
}
