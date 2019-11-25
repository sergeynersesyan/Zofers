package com.zofers.zofers.home

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.zofers.zofers.model.Offer
import com.zofers.zofers.service.RetrofitProvider
import com.zofers.zofers.AppViewModel
import com.zofers.zofers.staff.States
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class FeedViewModel : AppViewModel() {

	val offersList = MutableLiveData<List<Offer>>()

	@JvmOverloads
	fun load(query: String? = null) {
		val call = RetrofitProvider.getInstance().offerApi.getFeed(query)
		call.enqueue(object : Callback<List<Offer>> {
			override fun onResponse(call: Call<List<Offer>>, response: Response<List<Offer>>) {
				if (!response.isSuccessful) {
					state.value = States.ERROR
					return
				}
				offersList.value = response.body()
			}

			override fun onFailure(call: Call<List<Offer>>, t: Throwable) {
				state.value = States.FAIL
			}
		})
	}

	fun loadFirebase() {
		FirebaseFirestore.getInstance().collection("offer")
				.get()
				.addOnCompleteListener { task ->
					if (task.isSuccessful) {
						val offers = ArrayList<Offer>()
						for (document in task.result!!) {
							val offer = document.toObject(Offer::class.java)
							offer.id = document.id
							offers.add(offer)
						}
						offersList.setValue(offers)
					} else {
						state.setValue(States.FAIL)
					}
				}
	}
}
