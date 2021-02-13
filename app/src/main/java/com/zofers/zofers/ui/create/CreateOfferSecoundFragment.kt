package com.zofers.zofers.ui.create


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
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
import coil.request.CachePolicy
import coil.transform.RoundedCornersTransformation
import com.google.android.material.snackbar.Snackbar
import com.zofers.zofers.R
import com.zofers.zofers.databinding.FragmentCreateOfferSecoundBinding
import com.zofers.zofers.model.Offer


/**
 * A simple [Fragment] subclass.
 */
class CreateOfferSecoundFragment : CreateOfferBaseFragment(), View.OnClickListener {
	private lateinit var binding: FragmentCreateOfferSecoundBinding
	private lateinit var root: View
	private var imageUri: Uri? = null
	private var imageDrawable: Drawable? = null

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
//		binding.image.load(R.drawable.ic_image)
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
//		binding.randomImageMagic.setColorFilter(R.drawable.magic_gradient)
		binding.randomImageMagic.setOnClickListener {
			loadImageDrawable()
		}
		return root
	}

	override fun onResume() {
		super.onResume()
		if (imageUri != null) {
			loadImage(imageUri)
		} else if (imageDrawable != null) {
			binding.image.setImageDrawable(imageDrawable)
		}
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
		if (viewModel?.isEditMode != true && !hasImage()) {
			Snackbar.make(root, "Please select image", Snackbar.LENGTH_SHORT).show()
			validFilled = false
		}

		if (validFilled) {
			if (imageUri == null) {
				binding.image.drawable?.let {
					imageDrawable = it // to show on back
					activity!!.intent.putExtra(CreateOfferActivity.EXTRA_IMAGE_BYTES, it.toByteArray())
				}
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

		offer.imageURL?.let { loadImage(it) }
	}

	private fun loadImage(imageUri: Uri?) {
		binding.image.scaleType = ImageView.ScaleType.CENTER_CROP
		binding.image.load(imageUri) {
			transformations(RoundedCornersTransformation(4f))
		}
	}

	private fun loadImage(url: String) {
		binding.image.scaleType = ImageView.ScaleType.CENTER_CROP
		binding.image.load(url) {
			transformations(RoundedCornersTransformation(4f))
		}
	}

	private fun hasImage(): Boolean {
		return binding.image.drawable != null || imageUri != null
	}

	private fun loadImageDrawable() {
		binding.image.scaleType = ImageView.ScaleType.CENTER_CROP
		binding.image.load("https://source.unsplash.com/1200x900/?${binding.titleEditText.text}") {
			transformations(RoundedCornersTransformation(4f))
			memoryCachePolicy(CachePolicy.DISABLED)
			placeholder(R.drawable.ic_banner_foreground)
		}
		imageUri = null
	}

	override fun fillOffer(offer: Offer): Offer {
		offer.name = binding.titleEditText.text.toString().trim()
		offer.description = binding.descriptionEditText.text.toString().trim()
		if (hasImage()) {
			offer.imageURL = null
		}

		return offer
	}

	override fun onClick(v: View) {
		when (v.id) {
			R.id.image -> openGallery(root, REQUEST_SELECT_PICTURE)
		}
	}
}

