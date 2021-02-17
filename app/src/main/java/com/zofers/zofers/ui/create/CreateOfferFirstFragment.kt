package com.zofers.zofers.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doOnTextChanged
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.zofers.zofers.R
import com.zofers.zofers.model.Offer
import java.util.*

class CreateOfferFirstFragment : CreateOfferBaseFragment() {
	private var countrySpinner: Spinner? = null
	private var countryText: TextView? = null
	private var cityEdittext: EditText? = null
	private var radioGroup: RadioGroup? = null
	//    private RadioButton expensesMeRB;
	private var costEdittext: EditText? = null
	private var costLayout: ViewGroup? = null
	private var currencyEdittext: EditText? = null
	private var cityTIL: TextInputLayout? = null
	private var costTIL: TextInputLayout? = null
	private var currencyTIL: TextInputLayout? = null
	private var countryUnderline: View? = null
	private lateinit var root: View

	override val progress: Int
		get() = 33

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		root = inflater.inflate(R.layout.fragment_create_offer_first, container, false)
		countrySpinner = root.findViewById(R.id.country_spinner)
		countryText = root.findViewById(R.id.country_text)
		val adapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, countryList())
		countrySpinner?.adapter = adapter
		cityEdittext = root.findViewById(R.id.city_editText)
		costEdittext = root.findViewById(R.id.cost_editText)
		costLayout = root.findViewById(R.id.cost_layout)
		currencyEdittext = root.findViewById(R.id.currency_editText)
		cityTIL = root.findViewById(R.id.city_TIL)
		costTIL = root.findViewById(R.id.cost_TIL)
		currencyTIL = root.findViewById(R.id.currency_TIL)
		countryUnderline = root.findViewById(R.id.country_underline_view)
		radioGroup = root.findViewById(R.id.radioGroup)
		radioGroup?.setOnCheckedChangeListener { _, checkedId ->
			val freeForGuest = checkedId == R.id.expensesMe
			costEdittext?.isEnabled = !freeForGuest
			currencyEdittext?.isEnabled = !freeForGuest
			costLayout?.visibility = if (freeForGuest) View.GONE else View.VISIBLE
			if (freeForGuest) {
				costEdittext?.setText("")
			}
		}
		cityEdittext?.doOnTextChanged { _, _, _, _ ->
			cityTIL?.error = null
		}
		costEdittext?.doOnTextChanged { _, _, _, _ ->
			 costTIL?.error = null
		}
		currencyEdittext?.doOnTextChanged { _, _, _, _ ->
			currencyTIL?.error = null
		}
		countrySpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
			override fun onNothingSelected(parent: AdapterView<*>?) {}
			override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
				countryUnderline?.setBackgroundColor(context!!.resources.getColor(R.color.gray_transparent))
				countryText?.setTextColor(context!!.resources.getColor(R.color.gray_transparent))
			}

		}
		return root
	}

	override fun validFilled(): Boolean {
		var validFilled = true
		if (countrySpinner?.selectedItemPosition == 0) { //            Snackbar.make(view,"Please select country", Snackbar.LENGTH_SHORT).show();
			countryUnderline?.setBackgroundColor(context!!.resources.getColor(R.color.error_color))
			countryText?.setTextColor(context!!.resources.getColor(R.color.error_color))
			validFilled = false
		}
		if (cityEdittext?.text.toString().trim { it <= ' ' }.isEmpty()) {
			cityTIL?.error = " "
			validFilled = false
		}
		if (radioGroup?.checkedRadioButtonId == -1 && validFilled) {
			Snackbar.make(root, "Please select who will pay Expenses", Snackbar.LENGTH_SHORT).show()
			validFilled = false
		}
		if (radioGroup?.checkedRadioButtonId != R.id.expensesMe && costEdittext!!.text.length == 0) {
			costTIL?.error = " "
			validFilled = false
		}
		if (currencyEdittext!!.text.toString().trim { it <= ' ' }.isEmpty()) {
			currencyTIL?.error = " "
			validFilled = false
		}
		return validFilled
	}

	override fun nextFragment(): CreateOfferBaseFragment {
		return CreateOfferSecoundFragment()
	}

	override fun fillFields(offer: Offer) {
		countrySpinner?.setSelection(countryList().indexOf(offer.country))
		cityEdittext?.setText(offer.city)
		radioGroup?.check(
				when (offer.costMode) {
					Offer.COST_MODE_BOTH -> R.id.expensesBoth
					Offer.COST_MODE_GUEST -> R.id.expensesGuest
					else -> R.id.expensesMe
				}
		)
		costEdittext?.setText(offer.cost.toString())
		currencyEdittext?.setText(offer.currency)
	}

	override fun fillOffer(offer: Offer): Offer {
		offer.country = countrySpinner?.selectedItem.toString()
		offer.countryCode = getCountryCode(offer.country)
		offer.city = cityEdittext?.text.toString().trim { it <= ' ' }.toLowerCase(Locale.ROOT)
		offer.costMode = costMode()
		if (radioGroup?.checkedRadioButtonId != R.id.expensesMe) {
			val cost = costEdittext?.text.toString()
			offer.cost = cost.toInt()
			offer.currency = currencyEdittext?.text.toString().trim { it <= ' ' }
		}
		return offer
	}

	private fun countryList(): ArrayList<String> {
		val locale = Locale.getAvailableLocales()
		val countries = ArrayList<String>()
		var country: String
		for (loc in locale) {
			country = loc.displayCountry
			if (country.isNotEmpty() && !countries.contains(country)) {
				countries.add(country)
			}
		}
		Collections.sort(countries, java.lang.String.CASE_INSENSITIVE_ORDER)
		countries.add(0, getString(R.string.select_country))
		return countries
	}

	private fun getCountryCode(country: String): String {
		Locale.getAvailableLocales().forEach { loc ->
			if (loc.displayCountry == country) {
				return loc.country
			}
		}
		return ""
	}

	private fun costMode(): Int {
		return when (radioGroup!!.checkedRadioButtonId) {
			R.id.expensesMe -> Offer.COST_MODE_CREATOR
			R.id.expensesBoth -> Offer.COST_MODE_BOTH
			R.id.expensesGuest -> Offer.COST_MODE_GUEST
			else -> Offer.COST_MODE_CREATOR
		}
	}
}