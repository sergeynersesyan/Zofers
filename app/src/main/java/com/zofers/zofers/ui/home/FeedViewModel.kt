package com.zofers.zofers.ui.home

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.zofers.zofers.AppViewModel
import com.zofers.zofers.model.Offer
import com.zofers.zofers.staff.States
import java.util.*

class FeedViewModel : AppViewModel() {

	companion object {
		private const val LIMIT = 20
	}

	val offersList = MutableLiveData<MutableList<Offer>>()
	private var lastVisibleItem: DocumentSnapshot? = null
	private var lastQuery: String? = null
	private var reachedToEnd = false
	private val listener = OnCompleteListener<QuerySnapshot> { task ->
		if (task.isSuccessful) {
			state.value = States.NONE
			task.result?.let { querySnapshot ->
				offersList.value = handleResponse(querySnapshot)
			}
		} else {
			state.value = States.FAIL
		}

	}

	private val listenerLoadMore = OnCompleteListener<QuerySnapshot> { task ->
		if (task.isSuccessful) {
			state.value = States.NONE
			task.result?.let { querySnapshot ->
				val items = offersList.value
				items?.addAll(handleResponse(querySnapshot))
				offersList.value = items
			}

		} else {
			state.value = States.FAIL
		}

	}

	private fun handleResponse(querySnapshot: QuerySnapshot): ArrayList<Offer> {
		val offers = ArrayList<Offer>()
		for (docSnapshot in querySnapshot) {
			val offer = docSnapshot.toObject(Offer::class.java)
			offer.id = docSnapshot.id
			offers.add(offer)
		}
		reachedToEnd = if (offers.size > LIMIT) {
			offers.removeAt(LIMIT)
			false
		} else {
			true
		}
		lastVisibleItem = querySnapshot.documents.lastOrNull()
		return offers
	}

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
		reachedToEnd = false
		state.value = States.LOADING
		if (query == null) {
			lastQuery = null
			getAllOffers(listener)
		} else {
			lastQuery = query
			getFilteredOffers(
					query = query,
					listener = listener
			)
		}
	}

	fun loadMore() {
		if (reachedToEnd) return
		state.value = States.LOADING
		if (lastQuery == null) {
			getAllOffers(listenerLoadMore, true)
		} else {
			getFilteredOffers(lastQuery!!, true, listenerLoadMore)
		}
	}

	private fun getFilteredOffers(query: String, loadMore: Boolean = false, listener: OnCompleteListener<QuerySnapshot>) {
		reachedToEnd = false
		FirebaseFirestore.getInstance().collection("offer")
				.whereEqualTo("city", query.toLowerCase(Locale.getDefault()))
//				.orderBy("viewCount")
//				.limit(5)
//				.whereEqualTo("name", query)
				.get()
				.addOnCompleteListener(listener)
	}

	private fun getAllOffers(listener: OnCompleteListener<QuerySnapshot>, loadMore: Boolean = false) {
		var query = FirebaseFirestore.getInstance()
				.collection("offer")
				.limit(LIMIT + 1L)
		if (loadMore) {
			lastVisibleItem?.let {
				query = query.startAt(it)
			}
		}
		query.get()
				.addOnCompleteListener(listener)

	}


}
