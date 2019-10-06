package com.zofers.zofers.vvm.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.zofers.zofers.R
import com.zofers.zofers.adapter.OffersAdapter
import com.zofers.zofers.databinding.ActivityProfileBinding
import com.zofers.zofers.vvm.viewmodel.ProfileViewModel

class ProfileActivity : BaseActivity() {
	lateinit var binding: ActivityProfileBinding
	lateinit var profileViewModel: ProfileViewModel
	lateinit var offersAdapter: OffersAdapter

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
		offersAdapter = OffersAdapter()
		setupViewModel()
		setupView()
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_profile, menu)
		return super.onCreateOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		when (item?.itemId) {
			R.id.log_out -> {
				profileViewModel.logout()
				startActivity(Intent(this, LoginActivity::class.java))
				finish()
			}
		}
		return super.onOptionsItemSelected(item)
	}

	private fun setupView () {
		binding.offersRecyclerView.adapter = offersAdapter
		offersAdapter.itemResId = R.layout.item_offer_small
		profileViewModel.currentUser?.let {user ->
			binding.userName.setText(user.displayName)
			binding.publicAbout.setText("Type something \n about you")
		}

	}

	private fun setupViewModel () {
		profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
		profileViewModel.loadUserOffers()
		profileViewModel.offersList.observe(this, Observer { offers ->
			offersAdapter.setItems(offers)
		})
	}
}
