package com.zofers.zofers.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.widget.AppCompatSpinner
import com.zofers.zofers.R
import com.zofers.zofers.model.Offer

/**
 * A simple [Fragment] subclass.
 */
class CreateOfferThirdFragment : CreateOfferBaseFragment() {
	private var peopleCountEdittext: EditText? = null
	private var genderSpinner: AppCompatSpinner? = null
	private var reqEdittext: EditText? = null
	private var availEdittext: EditText? = null
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val root = inflater.inflate(R.layout.fragment_create_offer_third, container, false)
		genderSpinner = root.findViewById(R.id.genderSpinner)
		val adapter = ArrayAdapter.createFromResource(context!!, R.array.gender_list, android.R.layout.simple_list_item_1)
		genderSpinner?.adapter = adapter
		peopleCountEdittext = root.findViewById(R.id.people_count_edittext)
		reqEdittext = root.findViewById(R.id.requirements_edittext)
		availEdittext = root.findViewById(R.id.availability_editText)
		return root
	}

	override val progress: Int
		get() = 100

	override fun validFilled(): Boolean {
		return true
	}

	override fun nextFragment(): CreateOfferBaseFragment? {
		return null
	}

	override fun fillFields(offer: Offer) {
		peopleCountEdittext?.setText(offer.peopleCount.toString())
		reqEdittext?.setText(offer.requirements.orEmpty())
		availEdittext?.setText(offer.availability.orEmpty())
		genderSpinner?.setSelection(offer.gender)
	}

	override fun fillOffer(offer: Offer): Offer? {
		if (!peopleCountEdittext?.text.isNullOrEmpty()) {
			offer.peopleCount = peopleCountEdittext?.text.toString().toInt()
		}
		if (reqEdittext?.text.toString().trim().isNotEmpty()) {
			offer.requirements = reqEdittext!!.text.toString().trim()
		}
		if (availEdittext!!.text.toString().trim().isNotEmpty()) {
			offer.availability = availEdittext!!.text.toString().trim()
		}
		offer.gender = genderSpinner!!.selectedItemPosition
		return offer
	}

	override val nextButtonTextResource: Int
		get() = R.string.action_finish
}