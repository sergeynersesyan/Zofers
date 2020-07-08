package com.zofers.zofers.ui.offer

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import coil.api.load
import coil.transform.CircleCropTransformation
import com.google.android.material.appbar.AppBarLayout
import com.zofers.zofers.BaseActivity
import com.zofers.zofers.R
import com.zofers.zofers.databinding.ActivityOfferBinding
import com.zofers.zofers.model.Offer
import com.zofers.zofers.model.Profile
import com.zofers.zofers.staff.MessageHelper
import com.zofers.zofers.staff.States
import com.zofers.zofers.staff.disable
import com.zofers.zofers.ui.profile.ProfileActivity
import com.zofers.zofers.view.AppBarStateChangeListener
import com.zofers.zofers.view.LoadingDialog

class OfferActivity : BaseActivity() {

	private var loadingDialog: LoadingDialog? = null
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

		initViewModel(offer)
		setupView(offer)
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_offer, menu)
		val favoriteItem = menu.findItem(R.id.action_pin)
		favoriteMenuDrawable = favoriteItem.icon
		favoriteItem.isVisible = false // implement on new release
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

	private fun initViewModel(offer: Offer) {
		viewModel = ViewModelProvider(this).get(OfferViewModel::class.java)
		viewModel?.init(offer)
		viewModel?.offer?.observe(this, Observer<Offer> { ofik ->
			updateView(ofik)
		})
		viewModel?.user?.observe(this, Observer<Profile> { user ->
			binding?.userName?.text = user.name
			binding?.avatar?.load(user.avatarUrl) {
				placeholder(R.drawable.ic_avatar)
				fallback(R.drawable.ic_avatar)
				transformations(CircleCropTransformation())
			}
		})

		viewModel?.state?.observe(this, Observer {state ->
			loadingDialog?.dismiss()
			when (state) {
				States.FINISH -> finish()
				States.ERROR -> MessageHelper.showErrorToast(this)
				States.LOADING -> loadingDialog = LoadingDialog().apply {
					show(supportFragmentManager, null)
				}
			}
		})
	}

	private fun setupView(offer: Offer) {
		binding?.appBar?.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
			override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
				updateMenuItemColors(state)
			}
		})
		binding?.interestedButton?.setOnClickListener {
			when (viewModel?.getState()) {
				OfferState.DEFAULT -> {
					viewModel?.onInterestedClicked()
				}
				OfferState.PENDING -> {
					viewModel?.onInterestedCancelled()
				}
				OfferState.MY,
				OfferState.APPROVED -> {
					//ignore
				}
			}
		}

		val forText = StringBuilder()
		if (offer.peopleCount > 0) {
			forText.append(resources.getQuantityString(offer.peopleTextResource, offer.peopleCount, offer.peopleCount))
		}
		if(!offer.requirements.isNullOrEmpty()) {
			forText.append(", ")
			forText.append(offer.requirements)
		}
		if(!offer.availability.isNullOrEmpty()) {
			forText.append(", ")
			forText.append(offer.availability)
		}
		if (forText.isEmpty()) {
			binding?.forTextContainer?.visibility = View.GONE
		} else {
			binding?.forTextViewData?.text = forText
		}
		binding?.avatar?.setOnClickListener { ProfileActivity.start(this, offer.userID) }
	}

	private fun updateView(offer: Offer) {
		val button = binding?.interestedButton
		when (viewModel?.getState()) {
			OfferState.MY ->
				button?.visibility = View.GONE
			OfferState.DEFAULT -> {
				button?.text = getString(R.string.interested)
			}
			OfferState.PENDING -> {
				button?.text = getString(R.string.pending_approval)
			}
			OfferState.APPROVED -> {
				button?.disable()
				button?.text = getString(R.string.approved)
			}
		}

		binding?.offer = offer
		binding?.coverImage?.load(offer.imageUrl)
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
