package com.zofers.zofers.ui.home

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.navigation.NavArgument
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zofers.zofers.BaseActivity
import com.zofers.zofers.R
import com.zofers.zofers.adapter.OffersAdapter
import com.zofers.zofers.databinding.ActivityHomeBinding
import com.zofers.zofers.ui.BackClickHandler
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity() {

	companion object {
		const val EXTRA_OPENING_TAB = "e-o-t"

		const val OPENING_TAB_HOME = 0
		const val OPENING_TAB_NOTIFICATION = 1
		const val OPENING_TAB_PROFILE = 2

	}

	override fun onCreate(savedInstanceState: Bundle?) {

		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_home)
		val navView: BottomNavigationView = findViewById(R.id.nav_view)

		val navController = findNavController(R.id.nav_host_fragment)
		// Passing each menu ID as a set of Ids because each
		// menu should be considered as top level destinations.
		val appBarConfiguration = AppBarConfiguration(setOf(
				R.id.navigation_home, R.id.navigation_notifications, R.id.navigation_profile))
		setupActionBarWithNavController(navController, appBarConfiguration)

		navView.setupWithNavController(navController)

		if (intent.extras?.containsKey(EXTRA_OPENING_TAB) == true) {
			val navID = when(intent.getIntExtra(EXTRA_OPENING_TAB, 0)) {
				OPENING_TAB_HOME -> R.id.navigation_home
				OPENING_TAB_NOTIFICATION -> R.id.navigation_notifications
				OPENING_TAB_PROFILE -> R.id.navigation_profile
				else -> R.id.navigation_home
			}
			navController.navigate(navID)
		}

	}

	override fun onBackPressed() {
		val handled = (nav_host_fragment.childFragmentManager.fragments[0] as? BackClickHandler)?.onBackClicked() ?: false
		if (!handled) {
			super.onBackPressed()
		}
	}


}
