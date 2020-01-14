package com.zofers.zofers.offer


import com.google.firebase.firestore.FirebaseFirestore
import com.zofers.zofers.AppViewModel
import com.zofers.zofers.model.Offer
import com.zofers.zofers.model.Profile

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

	fun getState(): OfferState {
		return when {
			isCurrentUserOffer() -> OfferState.MY
			offer.interestedUsers.isNullOrEmpty() || !offer.interestedUsers.contains(currentUser?.id) -> OfferState.DEFAULT
			offer.approvedUsers.isNullOrEmpty() || !offer.approvedUsers.contains(currentUser?.id) -> OfferState.PENDING
			else -> OfferState.APPROVED
		}
	}

	fun onInterestedClicked() {
		addConnection()
		addInterestedUser()
	}

	private fun addInterestedUser() {
		val interestedUsers = offer.interestedUsers ?: mutableListOf()
		interestedUsers.add(currentUser!!.id)
		offer.interestedUsers = interestedUsers
		updateDocument(Offer.DOC_NAME, offer.id, "interestedUsers", interestedUsers) { task ->
			if (!task.isSuccessful) {
				offer.interestedUsers.remove(currentUser?.id)
			}
		}
	}

	private fun addConnection() {
		currentUser?.let {
			it.connections.add(offer.userID)
			updateDocument(Profile.DOC_NAME, currentUser!!.id, "connections", it.connections) { task ->
				if (!task.isSuccessful) {
					it.connections.remove(offer.userID)
				}
			}
		}

	}


}
