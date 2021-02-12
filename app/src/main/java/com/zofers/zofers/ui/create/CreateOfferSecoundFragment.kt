package com.zofers.zofers.ui.create


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import coil.api.load
import coil.transform.RoundedCornersTransformation
import com.google.android.material.snackbar.Snackbar
import com.zofers.zofers.R
import com.zofers.zofers.databinding.FragmentCreateOfferSecoundBinding
import com.zofers.zofers.model.Offer
import com.zofers.zofers.staff.FileHelper

/**
 * A simple [Fragment] subclass.
 */
class CreateOfferSecoundFragment : CreateOfferBaseFragment(), View.OnClickListener {
	private lateinit var binding: FragmentCreateOfferSecoundBinding
	private lateinit var root: View
	private var imageUri: Uri? = null

	override val progress: Int
		get() = 66

	companion object {
		const val REQUEST_SELECT_PICTURE = 1000
	}

	@SuppressLint("ClickableViewAccessibility")
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		root = inflater.inflate(R.layout.fragment_create_offer_secound, container, false)
		binding = DataBindingUtil.bind(root)!!
		binding.image.setOnClickListener(this)
		binding.image.load(R.drawable.ic_image)
		binding.titleEditText.doOnTextChanged { _, _, _, _ ->
			binding.titleTIL.error = null
		}
		binding.descriptionEditText.doOnTextChanged { _, _, _, _ ->
			binding.descriptionTIL.error = null
		}
		binding.descriptionEditText.setOnTouchListener { view, event ->
			view.parent.requestDisallowInterceptTouchEvent(true)
			if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
				view.parent.requestDisallowInterceptTouchEvent(false)
			}
			return@setOnTouchListener false
		}
		return root
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_SELECT_PICTURE) {
				if (data != null) {
					imageUri = data.data
					loadImage(imageUri)
				}
			}
		}
	}

	override fun onResume() {
		super.onResume()
		if (imageUri != null) {
			loadImage(imageUri)
		}
	}

	override fun validFilled(): Boolean {
		var validFilled = true
		if (binding.titleEditText.text.toString().trim().isEmpty()) {
			binding.titleTIL.error = " "
			validFilled = false
		}
		if (binding.descriptionEditText.text.toString().trim().isEmpty()) {
			binding.descriptionTIL.error = " "
			validFilled = false
		}
		if (viewModel?.isEditMode == false && imageUri == null) {
			Snackbar.make(root, "Please select image", Snackbar.LENGTH_SHORT).show()
			validFilled = false
		}

		if (validFilled && imageUri != null) {
			val binaryImage = FileHelper.getImageBinary(context, imageUri)
			if (binaryImage == null) {
				validFilled = false
			} else {
				activity!!.intent.putExtra(CreateOfferActivity.EXTRA_IMAGE_URI, imageUri)
			}
		}
		return validFilled
	}

	override fun nextFragment(): CreateOfferBaseFragment {
		return CreateOfferThirdFragment()
	}

	override fun fillFields(offer: Offer) {
		binding.titleEditText.setText(offer.name)
		binding.descriptionEditText.setText(offer.description)

		binding.image.scaleType = ImageView.ScaleType.CENTER_CROP
		binding.image.load(offer.imageURL) {
			transformations(RoundedCornersTransformation(4f))
		}
	}

	private fun loadImage(imageUri: Uri?) {
		binding.image.scaleType = ImageView.ScaleType.CENTER_CROP
		binding.image.load(imageUri) {
			transformations(RoundedCornersTransformation(4f))
		}
	}

	override fun fillOffer(offer: Offer): Offer {
		offer.name = binding.titleEditText.text.toString().trim()
		offer.description = binding.descriptionEditText.text.toString().trim()

		return offer
	}

	override fun onClick(v: View) {
		when (v.id) {
			R.id.image -> openGallery(root, REQUEST_SELECT_PICTURE)
		}
	}
}
