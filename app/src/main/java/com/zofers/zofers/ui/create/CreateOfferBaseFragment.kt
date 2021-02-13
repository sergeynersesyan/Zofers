package com.zofers.zofers.ui.create

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.zofers.zofers.BaseFragment
import com.zofers.zofers.R
import com.zofers.zofers.model.Offer

abstract class CreateOfferBaseFragment : BaseFragment() {
	protected var viewModel: OfferCreationFirebaseViewModel? = null
	abstract val progress: Int
	abstract fun validFilled(): Boolean
	abstract fun nextFragment(): CreateOfferBaseFragment?
	abstract fun fillOffer(offer: Offer): Offer?
	abstract fun fillFields(offer: Offer)
	open val nextButtonTextResource: Int
		get() = R.string.action_next

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewModel = ViewModelProvider(activity!!).get(OfferCreationFirebaseViewModel::class.java)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewModel?.let { vm ->
			if (vm.isEditMode) {
				fillFields(vm.offer)
			}
		}
	}

}