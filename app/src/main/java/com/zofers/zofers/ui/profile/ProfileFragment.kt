package com.zofers.zofers.ui.profile


import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import coil.api.load
import coil.transform.CircleCropTransformation
import com.zofers.zofers.BaseFragment
import com.zofers.zofers.R
import com.zofers.zofers.adapter.OffersAdapter
import com.zofers.zofers.databinding.ActivityProfileBinding
import com.zofers.zofers.model.Offer
import com.zofers.zofers.staff.States
import com.zofers.zofers.ui.login.LoginActivity
import com.zofers.zofers.ui.offer.OfferActivity

class ProfileFragment : BaseFragment() {

	lateinit var binding: ActivityProfileBinding
	private lateinit var profileViewModel: ProfileViewModel
	private lateinit var offersAdapter: OffersAdapter
	private lateinit var galleryAdapter: ImageGalleryAdapter
	private var progressDialog: ProgressDialog? = null

	companion object {
		private const val ARG_USER_ID = "arg_us_id"

		private const val REQ_CODE_GALLERY_AVATAR = 1001
		private const val REQ_CODE_GALLERY_PRIVATE = 1002
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}


	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		setupViewModel()

		val root = inflater.inflate(R.layout.activity_profile, container, false)

		binding = DataBindingUtil.bind(root)!!
		offersAdapter = OffersAdapter()
		setupView()
		return root
	}


	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		super.onCreateOptionsMenu(menu, inflater)
		inflater.inflate(R.menu.menu_profile, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.log_out -> {
				profileViewModel.logout()
				startActivity(Intent(context, LoginActivity::class.java))
				activity?.finish()
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
				val intent = Intent(context, OfferActivity::class.java)
				intent.putExtra(OfferActivity.EXTRA_OFFER, offer)
				startActivity(intent)
			}
		})
		galleryAdapter = ImageGalleryAdapter()
		galleryAdapter.listener = object : ImageGalleryAdapter.Listener {
			override fun onImageCLick(url: String?) {
				binding.bigImage.visibility = View.VISIBLE
				binding.bigImage.load(url)
			}

			override fun onAddClick() {
				openGallery(binding.root, REQ_CODE_GALLERY_PRIVATE)
			}
		}
		binding.galleryRecyclerView.adapter = galleryAdapter
		updateVIew()
		binding.bigImage.setOnClickListener { binding.bigImage.visibility = View.GONE }
	}


	private fun updateVIew() {
		profileViewModel.currentUser?.let { user ->
			activity?.title = user.name
			binding.userName.text = user.name
			binding.publicAbout.text = "Type something \n about you"
			binding.avatar.load(user.avatarUrl) {
				placeholder(R.drawable.ic_avatar)
				transformations(CircleCropTransformation())
			}
		}
	}

	private fun setupViewModel() {
		profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
		profileViewModel.init(arguments?.getString(ARG_USER_ID)
				?: profileViewModel.currentUser?.id.orEmpty())
		profileViewModel.offersList.observe(viewLifecycleOwner, Observer { offers ->
			offersAdapter.setItems(offers)
		})
		profileViewModel.state.observe(viewLifecycleOwner, Observer { state ->
			when (state) {
				States.LOADING -> progressDialog = ProgressDialog.show(context, "Wait a secund", "Uploading your image")
				States.NONE -> {
					progressDialog?.dismiss()
					updateVIew()
				}
			}
		})
		profileViewModel.profile.observe(viewLifecycleOwner, Observer {
			galleryAdapter.items = it.privateImages
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
			}
		}

	}


}
