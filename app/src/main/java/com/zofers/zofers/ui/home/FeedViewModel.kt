package com.zofers.zofers.ui.home

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.zofers.zofers.AppViewModel
import com.zofers.zofers.model.Offer
import com.zofers.zofers.staff.States
import java.util.*

class FeedViewModel : AppViewModel() {

	val offersList = MutableLiveData<List<Offer>>()

//	@JvmOverloads
//	fun load(query: String? = null) {
//		val call = RetrofitProvider.getInstance().offerApi.getFeed(query)
//		call.enqueue(object : Callback<List<Offer>> {
//			override fun onResponse(call: Call<List<Offer>>, response: Response<List<Offer>>) {
//				if (!response.isSuccessful) {
//					state.value = States.ERROR
//					return
//				}
//				offersList.value = response.body()
//			}
//
//			override fun onFailure(call: Call<List<Offer>>, t: Throwable) {
//				state.value = States.FAIL
//			}
//		})
//	}

	fun loadFirebase(query: String? = null) {
		state.value = States.LOADING
		val listener = OnCompleteListener<QuerySnapshot> { task ->
			if (task.isSuccessful) {

				state.value = States.NONE
				val offers = ArrayList<Offer>()
				for (document in task.result!!) {
					val offer = document.toObject(Offer::class.java)
					offer.id = document.id
					offers.add(offer)
				}
				offersList.setValue(offers)
			} else {
				state.value = States.FAIL
			}

		}
		if (query == null) {
			getAllOffers(listener)
		} else {
			getFilteredOffers(query, listener)
		}

	}

	private fun getFilteredOffers(query: String, listener: OnCompleteListener<QuerySnapshot>) {
		FirebaseFirestore.getInstance().collection("offer").whereEqualTo("city", query)
//				.orderBy("viewCount")
//				.limit(5)
//				.whereEqualTo("name", query)
				.get()
				.addOnCompleteListener(listener)
	}

	private fun getAllOffers(listener: OnCompleteListener<QuerySnapshot>) {
		FirebaseFirestore.getInstance().collection("offer")
				.get()
				.addOnCompleteListener(listener)
	}


}
