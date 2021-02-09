package com.zofers.zofers.ui.profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coil.api.load
import coil.transform.CircleCropTransformation
import com.facebook.login.LoginManager
import com.zofers.zofers.BaseActivity
import com.zofers.zofers.BaseFragment
import com.zofers.zofers.R
import com.zofers.zofers.adapter.OffersAdapter
import com.zofers.zofers.databinding.FragmentProfileBinding
import com.zofers.zofers.model.Offer
import com.zofers.zofers.model.Profile
import com.zofers.zofers.staff.MessageHelper
import com.zofers.zofers.staff.States
import com.zofers.zofers.ui.BackClickHandler
import com.zofers.zofers.ui.create.CreateOfferActivity
import com.zofers.zofers.ui.edit_password.EditPasswordActivity
import com.zofers.zofers.ui.edit_profile.EditProfileActivity
import com.zofers.zofers.ui.login.LoginActivity
import com.zofers.zofers.ui.offer.OfferActivity


class ProfileFragment : BaseFragment(), BackClickHandler {

	lateinit var binding: FragmentProfileBinding
	private lateinit var profileViewModel: ProfileViewModel
	private lateinit var offersAdapter: OffersAdapter
	private lateinit var galleryAdapter: ImageGalleryAdapter
	private var progressDialog: ProgressDialog? = null
	private var bigImage: ImageView? = null

	companion object {
		const val ARG_USER_ID = "arg_us_id"

		private const val REQ_CODE_GALLERY_AVATAR = 1001
		private const val REQ_CODE_GALLERY_PRIVATE = 1002
		private const val REQ_CODE_EDIT_PROFILE = 1003
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}


	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		setupViewModel()

		val root = inflater.inflate(R.layout.fragment_profile, container, false)

		binding = DataBindingUtil.bind(root)!!
		setupView()
		return root
	}


	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		super.onCreateOptionsMenu(menu, inflater)
		if (profileViewModel.isCurrentUser) {
			inflater.inflate(R.menu.menu_profile, menu)
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		profileViewModel.destroy()
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.log_out -> {
				profileViewModel.logout()
				LoginManager.getInstance().logOut() // facebook account
				val intent = Intent(context, LoginActivity::class.java)
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
				startActivity(intent)
				activity?.finish()
			}
			R.id.action_settings -> {
				EditProfileActivity.startForResult(this, REQ_CODE_EDIT_PROFILE)
			}
			R.id.edit_password -> {
				context?.let { EditPasswordActivity.start(it) }
			}
		}
		return super.onOptionsItemSelected(item)
	}


	private fun setupView() {
		offersAdapter = OffersAdapter().apply {
			itemResId = R.layout.item_offer_small
			setListener(object : OffersAdapter.Listener {
				override fun onItemClick(offer: Offer) {
					val intent = Intent(context, OfferActivity::class.java)
					intent.putExtra(OfferActivity.EXTRA_OFFER, offer)
					startActivity(intent)
				}

				override fun loadMore() {

				}
			})
		}
		binding.offersRecyclerView.adapter = offersAdapter
		if (profileViewModel.isCurrentUser) {
			binding.avatar.setOnClickListener { openGallery(binding.root, REQ_CODE_GALLERY_AVATAR) }
			binding.publicAboutInfo.setOnClickListener {
				EditProfileActivity.startForResult(this, REQ_CODE_EDIT_PROFILE)
			}
		} else {
			binding.addAvatar.visibility = View.GONE
		}
		galleryAdapter = ImageGalleryAdapter().apply {
			listener = object : ImageGalleryAdapter.Listener {
				override fun onImageCLick(url: String?) {
					createBigImage(url)
				}

				override fun onAddClick() {
					openGallery(binding.root, REQ_CODE_GALLERY_PRIVATE)
				}

				override fun onImageLongClick(url: String?) {
					activity?.let { activity ->
						AlertDialog.Builder(activity)
								.setMessage("Do you want to delete this image?")
								.setPositiveButton(android.R.string.yes) { _, _ -> profileViewModel.deleteImage(url) }
								.setNegativeButton(android.R.string.no) { _, _ -> }
								.show()
					}
				}
			}
			showAddButton = profileViewModel.isCurrentUser
		}
		binding.galleryRecyclerView.adapter = galleryAdapter
		binding.createButton.setOnClickListener {
			startActivity(Intent(activity, CreateOfferActivity::class.java))
		}
	}

	private fun createBigImage(url: String?) {
		activity?.let { activity ->
			val vg = activity.window?.decorView?.rootView as? ViewGroup
			bigImage = ImageView(activity)
			vg?.addView(bigImage, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
			bigImage?.load(url)
			bigImage?.setOnClickListener {
				destroyBigImage()
			}
			bigImage?.setBackgroundColor(activity.resources?.getColor(R.color.gray_transparent) ?: 0)
		}

	}

	private fun destroyBigImage() {
		bigImage?.let {
			(activity?.window?.decorView?.rootView as? ViewGroup)?.removeView(it)
			bigImage = null
		}
	}


	private fun updateProfileDependingView(user: Profile) {
		(activity as? BaseActivity)?.supportActionBar?.title = user.name
		if (user.description.isNullOrEmpty()) {
			binding.publicAboutInfo.text = context?.getString(R.string.no_description)
			binding.publicAboutInfo.setTextColor(context?.resources?.getColor(R.color.gray_transparent) ?: 0)
		} else {
			binding.publicAboutInfo.text = user.description
		}
		binding.avatar.load(user.avatarURL) {
			placeholder(R.drawable.ic_avatar)
			fallback(R.drawable.ic_avatar)
			transformations(CircleCropTransformation())
		}
		galleryAdapter.items = user.privateImages
		setupPrivateSection()
	}

	private fun setupPrivateSection () {
		val privateVisibility = if (
				profileViewModel.isOffersLoaded
				&& (profileViewModel.isCurrentUser || profileViewModel.isConnected)
		)
			View.VISIBLE
		else
			View.GONE

		binding.privateTitle.visibility = privateVisibility
		binding.dividerPrivate.visibility = privateVisibility
		binding.galleryRecyclerView.visibility = privateVisibility
		binding.galleryEmptyText.visibility = if (galleryAdapter.items.isEmpty() && !profileViewModel.isCurrentUser) privateVisibility else View.GONE
	}

	private fun setupViewModel() {
		profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
		profileViewModel.init(arguments?.getString(ARG_USER_ID)
				?: profileViewModel.currentUser?.id.orEmpty())
		profileViewModel.offersList.observe(viewLifecycleOwner, Observer { offers ->
			binding.emptyOffersContainer.visibility = if (offers.isNullOrEmpty() && profileViewModel.isCurrentUser) View.VISIBLE else View.GONE
			offersAdapter.setItems(offers)
			setupPrivateSection()
		})
		profileViewModel.state.observe(viewLifecycleOwner, Observer { state ->
			progressDialog?.dismiss()
			when (state) {
				States.LOADING -> progressDialog = ProgressDialog.show(context, "Wait a second", "Uploading your image")
				States.NONE -> {
				}
				States.DONE -> MessageHelper.showSnackBar(binding.root, "successfully deleted")
				States.ERROR -> MessageHelper.showErrorToast(activity)
			}
		})
		profileViewModel.profile.observe(viewLifecycleOwner, Observer {
			updateProfileDependingView(it)
		})
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		context?.let { context ->
			if (resultCode == Activity.RESULT_OK) {
				if (requestCode == REQ_CODE_GALLERY_AVATAR) {
					data?.data?.let {
						profileViewModel.onNewProfileImage(context.applicationContext, it)
					}

				}
				if (requestCode == REQ_CODE_GALLERY_PRIVATE) {
					data?.data?.let {
						profileViewModel.onNewPrivateImage(context.applicationContext, it)
					}

				}
				if (requestCode == REQ_CODE_EDIT_PROFILE) {
					profileViewModel.refreshProfile()
				}
			}
		}

	}

	override fun onBackClicked(): Boolean {
		return bigImage?.let {
			destroyBigImage()
			true
		} ?: false
	}

}
