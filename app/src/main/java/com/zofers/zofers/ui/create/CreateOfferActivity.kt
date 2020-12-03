package com.zofers.zofers.ui.create

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import com.zofers.zofers.BaseActivity
import com.zofers.zofers.R
import com.zofers.zofers.event.OfferCreateEvent
import com.zofers.zofers.model.Offer
import com.zofers.zofers.service.ServiceCallback
import com.zofers.zofers.staff.MessageHelper.showNoConnectionToast
import com.zofers.zofers.ui.create.CreateOfferActivity
import com.zofers.zofers.ui.offer.OfferActivity
import com.zofers.zofers.view.LoadingDialog
import org.greenrobot.eventbus.EventBus

class CreateOfferActivity : BaseActivity(), View.OnClickListener {
	private var nextButton: Button? = null
	private var progressBar: ProgressBar? = null
	private var fragmentContainer: FrameLayout? = null
	private val loadingDialog = LoadingDialog()
	private var fragment: CreateOfferBaseFragment? = null
	private var viewModel: OfferCreationFirebaseViewModel? = null

	companion object {
		const val KEY_OFFER = "ext_k_off"
		const val EXTRA_IMAGE_URI = "ext_im_uri"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_create_offer)
		setTitle(R.string.title_activity_create_offer)
		nextButton = findViewById(R.id.next_button)
		progressBar = findViewById(R.id.progress)
		fragmentContainer = findViewById(R.id.fragment_container)
		fragment = CreateOfferFirstFragment()
		nextButton?.setOnClickListener(this)
		viewModel = ViewModelProvider(this).get(OfferCreationFirebaseViewModel::class.java)
		viewModel?.init(intent.getParcelableExtra(KEY_OFFER))
		setTitle(if (viewModel?.isEditMode == true) R.string.title_edit_offer else R.string.title_activity_create_offer)
		openFragment(false)
	}

	override fun onStart() {
		super.onStart()

	}

	private fun openFragment(addToBackStack: Boolean) {
		val transaction = supportFragmentManager
				.beginTransaction()
				.replace(R.id.fragment_container, fragment!!)
		if (addToBackStack) {
			transaction.addToBackStack("create offer")
		}
		transaction.commit()
		onFragmentChange()
	}

	override fun onClick(v: View) {
		when (v.id) {
			R.id.next_button -> {
				if (fragment?.validFilled() != true) return
				val offer = fragment?.fillOffer(viewModel!!.offer)
				if (fragment?.nextFragment() == null) {
					val fileUri = intent.getParcelableExtra<Uri>(EXTRA_IMAGE_URI)
					loadingDialog.show(supportFragmentManager, null)
					viewModel!!.createOffer(this, fileUri, object : ServiceCallback<Offer> {
						override fun onSuccess(response: Offer) {
							loadingDialog.dismiss()
							if (viewModel?.isEditMode != true) {
								EventBus.getDefault().post(OfferCreateEvent(response))
							}
							val intent = Intent(this@CreateOfferActivity, OfferActivity::class.java)
							intent.putExtra(OfferActivity.EXTRA_OFFER, response)
							startActivity(intent)

							finish()
						}

						override fun onFailure() {
							loadingDialog.dismiss()
							showNoConnectionToast(this@CreateOfferActivity)
						}
					})
				} else {
					offer?.let {
						viewModel?.offer = it
						fragment = fragment?.nextFragment()
						openFragment(true)
					}
				}
			}
		}
	}

	override fun onBackPressed() {
		super.onBackPressed()
		fragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as CreateOfferBaseFragment?
		if (fragment != null) {
			onFragmentChange()
		}
	}

	private fun onFragmentChange() {
		progressBar!!.progress = fragment!!.progress
		nextButton!!.setText(fragment!!.nextButtonTextResource)
	}
}