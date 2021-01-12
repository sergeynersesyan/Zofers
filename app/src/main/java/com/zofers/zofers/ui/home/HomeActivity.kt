package com.zofers.zofers.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zofers.zofers.BaseActivity
import com.zofers.zofers.R
import com.zofers.zofers.ui.BackClickHandler
import com.zofers.zofers.ui.login.LoginActivity
import com.zofers.zofers.ui.notifications.messenger.MessengerActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity() {

	companion object {
		const val EXTRA_OPENING_TAB = "e-o-t"
		const val EXTRA_CONV_ID = "conversationID" // this string used in backend

		const val OPENING_TAB_HOME = 0
		const val OPENING_TAB_NOTIFICATION = 1
		const val OPENING_TAB_PROFILE = 2

	}

	private lateinit var viewModel: HomeViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_home)
		val navView: BottomNavigationView = findViewById(R.id.nav_view)

		val navController = findNavController(R.id.nav_host_fragment)
		// Passing each menu ID as a set of Ids because each
		// menu should be considered as top level destinations.
		val appBarConfiguration = AppBarConfiguration(
				setOf(
						R.id.navigation_home,
						R.id.navigation_notifications,
						R.id.navigation_profile
				)
		)
		setupActionBarWithNavController(navController, appBarConfiguration)

		navView.setupWithNavController(navController)

		navView.setOnNavigationItemSelectedListener { menuItem ->
			if (viewModel.isLoggedOut() && menuItem.itemId != R.id.navigation_home) {
				LoginActivity.startForResult(this)
				false
			} else {
				navController.navigate(menuItem.itemId)
				true
			}
		}
		if (intent.extras?.containsKey(EXTRA_CONV_ID) == true) {
			MessengerActivity.start(this, intent.getStringExtra(EXTRA_CONV_ID))
		} else if (intent.extras?.containsKey(EXTRA_OPENING_TAB) == true) {
			val navID = when (intent.getIntExtra(EXTRA_OPENING_TAB, 0)) {
				OPENING_TAB_HOME -> R.id.navigation_home
				OPENING_TAB_NOTIFICATION -> R.id.navigation_notifications
				OPENING_TAB_PROFILE -> R.id.navigation_profile
				else -> R.id.navigation_home
			}
			navController.navigate(navID)
		}

	}

	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)
		if (intent?.extras?.containsKey(EXTRA_CONV_ID) == true) {
			MessengerActivity.start(this, intent.getStringExtra(EXTRA_CONV_ID))
		}
	}

	override fun onBackPressed() {
		val handled = (nav_host_fragment.childFragmentManager.fragments[0] as? BackClickHandler)?.onBackClicked()
				?: false
		if (!handled) {
			super.onBackPressed()
		}
	}


}
