package com.zofers.zofers.offer


import androidx.lifecycle.ViewModel

import com.google.firebase.firestore.FirebaseFirestore
import com.zofers.zofers.AppViewModel
import com.zofers.zofers.model.Offer

class OfferViewModel : AppViewModel() {

	lateinit var offer: Offer

	fun delete() {
		FirebaseFirestore.getInstance()
				.collection("offer")
				.document(offer.id)
				.delete()
		//				.addOnSuccessListener();
	}

	fun isCurrentUserOffer(): Boolean {
		return offer.userID == currentUser?.id
	}

	fun onInterestedClicked() {
//		currentUser
//		offer.
	}
}
