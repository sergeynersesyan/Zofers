package com.zofers.zofers.vvm.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.zofers.zofers.model.Offer
import java.util.ArrayList

class ProfileViewModel : AppViewModel() {

	val offersList = MutableLiveData<List<Offer>>()

	fun logout() {
		auth.signOut()
	}

	fun loadUserOffers() {
		FirebaseFirestore.getInstance().collection("offer").whereEqualTo("userID", currentUser!!.uid)
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
