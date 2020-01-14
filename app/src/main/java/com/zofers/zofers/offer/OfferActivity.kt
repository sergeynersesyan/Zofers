package com.zofers.zofers.offer

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import coil.api.load
import com.google.android.material.appbar.AppBarLayout
import com.zofers.zofers.BaseActivity
import com.zofers.zofers.R
import com.zofers.zofers.databinding.ActivityOfferBinding
import com.zofers.zofers.model.Offer
import com.zofers.zofers.view.AppBarStateChangeListener

class OfferActivity : BaseActivity() {

	private var viewModel: OfferViewModel? = null
	private var binding: ActivityOfferBinding? = null
	private var favoriteMenuDrawable: Drawable? = null
	private var deleteMenuDrawable: Drawable? = null

	companion object {
		const val EXTRA_OFFER = "key_offer"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_offer)

		binding = DataBindingUtil.setContentView(this, R.layout.activity_offer)
		val toolbar = findViewById<Toolbar>(R.id.toolbar)
		setSupportActionBar(toolbar)
		title = ""

		val offer: Offer = intent.getParcelableExtra(EXTRA_OFFER)
		binding?.offer = offer
		viewModel = ViewModelProviders.of(this).get(OfferViewModel::class.java)
		viewModel?.offer = offer

		binding?.coverImage?.load(offer.imageUrl)


		setupView()
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_offer, menu)
		favoriteMenuDrawable = menu.findItem(R.id.action_pin).icon
		if (viewModel?.isCurrentUserOffer() == true) {
			val deleteItem = menu.findItem(R.id.action_delete)
			deleteMenuDrawable = deleteItem.icon
			deleteItem.isVisible = viewModel?.isCurrentUserOffer() == true
		}
		updateMenuItemColors(AppBarStateChangeListener.State.EXPANDED)
		return super.onCreateOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		when (item?.itemId) {
			R.id.action_delete -> viewModel!!.delete()
		}
		return super.onOptionsItemSelected(item)
	}

	private fun setupView() {
		val button = binding?.interestedButton
		when (viewModel?.getState()) {
			OfferState.MY ->
				button?.visibility = View.GONE
			OfferState.DEFAULT ->
				button?.setOnClickListener {
					viewModel?.onInterestedClicked()
				}
			OfferState.PENDING -> {
				button?.isEnabled = false
				button?.text = getString(R.string.pending_approval)
			}
			OfferState.APPROVED -> {
				button?.isEnabled = false
				button?.text = getString(R.string.approved)
			}
		}

		binding?.appBar?.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
			override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
				updateMenuItemColors(state)
			}
		})
	}

	private fun updateMenuItemColors(state: AppBarStateChangeListener.State) {
		when (state) {
			AppBarStateChangeListener.State.EXPANDED -> {
				favoriteMenuDrawable?.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
				deleteMenuDrawable?.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
			}
			AppBarStateChangeListener.State.COLLAPSED -> {
				favoriteMenuDrawable?.clearColorFilter()
				deleteMenuDrawable?.clearColorFilter()
			}
		}
	}
}
