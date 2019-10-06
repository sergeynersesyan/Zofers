package com.zofers.zofers.vvm.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView

import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zofers.zofers.R
import com.zofers.zofers.adapter.OffersAdapter
import com.zofers.zofers.model.Offer
import com.zofers.zofers.staff.MessageHelper
import com.zofers.zofers.vvm.viewmodel.FeedViewModel
import com.zofers.zofers.vvm.viewmodel.States

class HomeActivity : BaseActivity(), SearchView.OnQueryTextListener, View.OnClickListener {

	private lateinit var adapter: OffersAdapter
	private lateinit var viewModel: FeedViewModel

	private lateinit var searchView: SearchView
	private var recyclerView: RecyclerView? = null
	private var profileImageView: ImageView? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_home)
		title = null
		val toolbar = findViewById<Toolbar>(R.id.toolbar)
		setSupportActionBar(toolbar)

		val fab = findViewById<FloatingActionButton>(R.id.fab)
		fab.setOnClickListener(this)

		recyclerView = findViewById(R.id.offers_recycler_view)
		adapter = OffersAdapter()
		recyclerView!!.layoutManager = LinearLayoutManager(this@HomeActivity)
		recyclerView!!.adapter = adapter

		adapter.setListener(object : OffersAdapter.Listener {
			override fun onItemClick(offer: Offer) {
				val intent = Intent(this@HomeActivity, OfferActivity::class.java)
				intent.putExtra(OfferActivity.EXTRA_OFFER, offer)
				startActivity(intent)
			}
		})

		profileImageView = findViewById(R.id.profile_button)
		profileImageView!!.setOnClickListener(this)

		viewModel = ViewModelProviders.of(this).get(FeedViewModel::class.java)
		viewModel.loadFirebase()
		viewModel.offersList.observe(this, Observer<List<Offer>> { offers ->
			adapter.setItems(offers)
		})
		viewModel.state.observe(this, Observer<Int> { state ->
			when (state) {
				States.ERROR -> MessageHelper.showErrorToast(this@HomeActivity, "")
				States.FAIL -> MessageHelper.showNoConnectionToast(this@HomeActivity)
			}
		})
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_search, menu)

		val searchItem = menu.findItem(R.id.action_search)
		searchView = MenuItemCompat.getActionView(searchItem) as SearchView
		//        searchView.setMaxWidth(Integer.MAX_VALUE);
		//        EditText searchEditText = searchView.findViewById(R.id.search_src_text);
		//        searchItem.expandActionView();

		searchView.setOnQueryTextListener(this)
		searchView.isIconified = false
		searchView.gravity = Gravity.START
		searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
			override fun onMenuItemActionExpand(item: MenuItem): Boolean {
				return false
			}

			override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
				onBackPressed()
				return true
			}
		})
		searchView.clearFocus()

		val closeButton = searchView.findViewById<View>(androidx.appcompat.R.id.search_close_btn)
		closeButton?.setOnClickListener(this)
		return super.onCreateOptionsMenu(menu)
	}

	override fun onQueryTextSubmit(s: String?): Boolean {
		viewModel.load(s)
		return false
	}

	override fun onQueryTextChange(s: String): Boolean {
		if (TextUtils.isEmpty(s)) {
			onQueryTextSubmit(null)
			return true
		}
		return false
	}

	override fun onClick(v: View) {
		when (v.id) {
			R.id.profile_button -> {
				val intent = Intent(this, ProfileActivity::class.java)
				startActivity(intent)
			}
			R.id.fab -> {
				val intent1 = Intent(this, CreateOfferActivity::class.java)
				startActivity(intent1)
			}
			androidx.appcompat.R.id.search_close_btn -> {
				searchView!!.setQuery("", false)
				searchView!!.clearFocus()
			}
		}
	}
}
