package com.zofers.zofers.ui.home

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.zofers.zofers.AppViewModel
import com.zofers.zofers.event.OfferDeleteEvent
import com.zofers.zofers.model.Offer
import com.zofers.zofers.staff.AnalyticsEventLogger
import com.zofers.zofers.staff.States
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

class FeedViewModel : AppViewModel() {

	companion object {
		private const val LIMIT = 20
	}

	private var countryCode: String? = null
	val offersList = MutableLiveData<MutableList<Offer>>()
	private var currentCountryLastVisibleItem: DocumentSnapshot? = null
	private var otherCountriesLastVisibleItem: DocumentSnapshot? = null
	private var filteredLastVisibleItem: DocumentSnapshot? = null
	private var lastQuery: String? = null
	private var currentCountryReachedToEnd = false
	private var otherCountriesReachedToEnd = false
	private var filteredReachedToEnd = false
	private var isCreated = false

	fun init(countryCode: String?) {
		if (isCreated) return
		isCreated = true
		this.countryCode = countryCode
		loadFeed()
		EventBus.getDefault().register(this)
	}

	fun loadFeed() {
		currentCountryReachedToEnd = false
		otherCountriesReachedToEnd = false
		currentCountryLastVisibleItem = null
		otherCountriesLastVisibleItem = null
		lastQuery = null
		state.value = States.LOADING
		getAllOffers()
	}

	private fun loadFiltered() {
		state.value = States.LOADING
		getFilteredOffers(
				query = lastQuery ?: ""
		)
	}

	fun search(query: String?) {
		lastQuery = query
		filteredReachedToEnd = false
		filteredLastVisibleItem = null
		if (query.isNullOrEmpty()) {
			loadFeed()
		} else {
			loadFiltered()
			AnalyticsEventLogger.logSearchEvent(query)
		}

	}

	fun loadMore() {
		if (lastQuery == null) {
			getAllOffers()
		} else {
			getFilteredOffers(lastQuery!!)
		}
	}

	@Subscribe
	fun onOfferDelete (offerDeleteEvent: OfferDeleteEvent) {
		val offers = offersList.value
		offers?.remove(offerDeleteEvent.offer)
		offersList.value = offers
	}

	private fun getFilteredOffers(query: String) {
		if (filteredReachedToEnd) return
		state.value = States.LOADING
		FirebaseFirestore.getInstance().collection("offer")
				.whereArrayContains("keyWords", query.toLowerCase(Locale.getDefault()))
//				.orderBy("viewCount")
				.limit(LIMIT + 1L)
//				.whereEqualTo("name", query)
				.get()
				.addOnCompleteListener { task ->
					if (task.isSuccessful) {
						state.value = States.NONE
						task.result?.let { querySnapshot ->
							val offers =
									if (filteredLastVisibleItem == null) {
										querySnapshot.toOfferList()
									} else {
										val items = offersList.value
										items?.addAll(querySnapshot.toOfferList())
										items
									}
							filteredReachedToEnd = if (offers?.size ?: 0 > LIMIT) {
								offers?.removeAt(LIMIT)
								false
							} else {
								true
							}
							val notReportedOffers = currentUser?.let {user ->
								offers?.filter { !user.reportedOfferIDs.contains(it.id) }
							} ?: offers

							filteredLastVisibleItem = querySnapshot.documents.lastOrNull()
							offersList.value = notReportedOffers?.toMutableList()
						}

					} else {
						state.value = States.FAIL
					}
				}
	}

	private fun getAllOffers() {
		if (!currentCountryReachedToEnd) {
			loadCurrentCountryOffers()
		} else if (!otherCountriesReachedToEnd) {
			loadOtherCountriesOffers()
		}
	}

	private fun loadCurrentCountryOffers() {
		state.value = States.LOADING
		var query = FirebaseFirestore.getInstance()
				.collection("offer")
				.whereIn(
						"countryCode",
						listOf(
								countryCode?.toLowerCase(Locale.ROOT),
								countryCode?.toUpperCase(Locale.ROOT)
						)
				)
				.orderBy("creationDate", Query.Direction.DESCENDING)
				.limit(LIMIT + 1L)
		currentCountryLastVisibleItem?.let {
			query = query.startAt(it)
		}
		query.get()
				.addOnCompleteListener { task ->
					if (task.isSuccessful) {
						state.value = States.NONE
						task.result?.let { querySnapshot ->
							val newOffers = querySnapshot.toOfferList()
							currentCountryReachedToEnd = if (newOffers.size > LIMIT) {
								newOffers.removeAt(LIMIT)
								false
							} else {
								if (newOffers.isNullOrEmpty()) {
									loadOtherCountriesOffers()
								}
								true
							}

							val notReportedOffers = currentUser?.let {user ->
								newOffers.filter { !user.reportedOfferIDs.contains(it.id) }
							} ?: newOffers
							offersList.value = if (currentCountryLastVisibleItem == null) {
								notReportedOffers.toMutableList()
							} else {
								val items = offersList.value ?: arrayListOf()
								items.addAll(notReportedOffers)
								items
							}
							currentCountryLastVisibleItem = querySnapshot.documents.lastOrNull()
						}

					} else {
						state.value = States.FAIL
					}
				}
	}

	private fun loadOtherCountriesOffers() {
		if (state.value != States.LOADING) {
			state.value = States.LOADING
		}
		var query = FirebaseFirestore.getInstance()
				.collection("offer")
				.orderBy("creationDate", Query.Direction.DESCENDING)
				.limit(LIMIT + 1L)
		otherCountriesLastVisibleItem?.let {
			query = query.startAt(it)
		}
		query.get()
				.addOnCompleteListener { task ->
					if (task.isSuccessful) {
						state.value = States.NONE
						task.result?.let { querySnapshot ->
							val newOffers = querySnapshot.toOfferList()

							otherCountriesReachedToEnd = if (newOffers.size > LIMIT) {
								newOffers.removeAt(LIMIT)
								false
							} else {
								true
							}
							otherCountriesLastVisibleItem = querySnapshot.documents.lastOrNull()
							val notReportedOffers = currentUser?.let {user ->
								newOffers.filter { !user.reportedOfferIDs.contains(it.id) }
							} ?: newOffers

							val offers = offersList.value ?: arrayListOf()
							offers.addAll(notReportedOffers.filter { offersList.value?.contains(it) != true })
							offersList.value = offers
						}
					} else {
						state.value = States.FAIL
					}
				}
	}

	fun destroy() {
		EventBus.getDefault().unregister(this)
	}
}

private fun QuerySnapshot.toOfferList(): ArrayList<Offer> {
	val offers = ArrayList<Offer>()
	for (docSnapshot in this) {
		val offer = docSnapshot.toObject(Offer::class.java)
		offer.id = docSnapshot.id
		offers.add(offer)
	}
	return offers
}
