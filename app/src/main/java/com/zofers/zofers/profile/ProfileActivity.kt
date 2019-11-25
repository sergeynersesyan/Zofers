package com.zofers.zofers.profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.zofers.zofers.BaseActivity
import com.zofers.zofers.R
import com.zofers.zofers.adapter.OffersAdapter
import com.zofers.zofers.databinding.ActivityProfileBinding
import com.zofers.zofers.login.LoginActivity
import com.zofers.zofers.model.Offer
import com.zofers.zofers.offer.OfferActivity
import com.zofers.zofers.staff.States

class ProfileActivity : BaseActivity() {
	lateinit var binding: ActivityProfileBinding
	private lateinit var profileViewModel: ProfileViewModel
	private lateinit var offersAdapter: OffersAdapter
	private lateinit var galleryAdapter: ImageGalleryAdapter
	private var progressDialog: ProgressDialog? = null

	companion object {
		private const val REQ_CODE_GALLERY_AVATAR = 1001
		private const val REQ_CODE_GALLERY_PRIVATE = 1002

	}

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

	private fun setupView() {
		binding.offersRecyclerView.adapter = offersAdapter
		offersAdapter.itemResId = R.layout.item_offer_small
		binding.avatar.setOnClickListener { openGallery(binding.root, REQ_CODE_GALLERY_AVATAR) }
		offersAdapter.setListener(object : OffersAdapter.Listener {
			override fun onItemClick(offer: Offer) {
				val intent = Intent(this@ProfileActivity, OfferActivity::class.java)
				intent.putExtra(OfferActivity.EXTRA_OFFER, offer)
				startActivity(intent)
			}
		})
		galleryAdapter = ImageGalleryAdapter()
		galleryAdapter.listener = object : ImageGalleryAdapter.Listener {
			override fun onImageCLick(url: String?) {
			}

			override fun onAddClick() {
				openGallery(binding.root, REQ_CODE_GALLERY_PRIVATE)
			}
		}
		binding.galleryRecyclerView.adapter = galleryAdapter
		updateVIew()
	}

	private fun updateVIew () {
		profileViewModel.currentUser?.let { user ->
			setTitle(user.displayName)
			binding.userName.text = user.displayName
			binding.publicAbout.text = "Type something \n about you"
			binding.avatar.setImageURI(user.photoUrl, null)
		}
	}

	private fun setupViewModel() {
		profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
		profileViewModel.init()
		profileViewModel.offersList.observe(this, Observer { offers ->
			offersAdapter.setItems(offers)
		})
		profileViewModel.state.observe(this, Observer { state ->
			when (state) {
				States.LOADING -> progressDialog = ProgressDialog.show(this, "Wait a secund", "Uploading your image")
				States.NONE -> {
					progressDialog?.dismiss()
					updateVIew()
				}
			}
		})
		profileViewModel.profile.observe(this, Observer {
			galleryAdapter.items = it.privateImages

		})
	}

	public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQ_CODE_GALLERY_AVATAR) {
				data?.data?.let {
					profileViewModel.onNewProfileImage(applicationContext, it)
				}

			}
			if (requestCode == REQ_CODE_GALLERY_PRIVATE) {
				data?.data?.let {
					profileViewModel.onNewPrivateImage(applicationContext, it)
				}

			}
		}
	}
}
