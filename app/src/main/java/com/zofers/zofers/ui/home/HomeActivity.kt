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

class HomeActivity : BaseActivity() {

	private lateinit var adapter: OffersAdapter
	private lateinit var viewModel: FeedViewModel

	private lateinit var searchView: SearchView
	private lateinit var binding: ActivityHomeBinding

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

	}


}
