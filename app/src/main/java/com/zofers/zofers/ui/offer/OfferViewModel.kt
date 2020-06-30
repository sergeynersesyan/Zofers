package com.zofers.zofers.ui.offer


import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.zofers.zofers.AppViewModel
import com.zofers.zofers.model.Offer
import com.zofers.zofers.model.Profile
import com.zofers.zofers.staff.States

class OfferViewModel : AppViewModel() {

	val offer = MutableLiveData<Offer>()
	val user = MutableLiveData<Profile>()

	fun init(offer: Offer) {
		this.offer.value = offer
		val docRef = FirebaseFirestore.getInstance()
				.collection(Offer.DOC_NAME).document(offer.id)
		docRef.addSnapshotListener { snapshot, e ->
			if (e != null) {
				return@addSnapshotListener // failed
			}

			if (snapshot != null && snapshot.exists()) {
				this.offer.value = snapshot.toObject(Offer::class.java)
			}
		}

		val userDocRef = FirebaseFirestore.getInstance()
				.collection(Profile.DOC_NAME).document(offer.userID)
		userDocRef.addSnapshotListener { snapshot, e ->
			if (e != null) {
				return@addSnapshotListener // failed
			}

			if (snapshot != null && snapshot.exists()) {
				this.user.value = snapshot.toObject(Profile::class.java)
			}
		}

	}

	fun delete() {
		firebaseService.deleteDocument("offer", offer.value!!.id) { task ->
			if (task.isSuccessful) {
				state.value = States.FINISH
			} else {
				state.value = States.ERROR
			}
		}
	}

	fun isCurrentUserOffer(): Boolean {
		return offer.value?.userID == currentUser?.id
	}

	fun getState(): OfferState {
		val offer = offer.value!!
		return when {
			isCurrentUserOffer() -> OfferState.MY
			offer.interestedUsers.isNullOrEmpty() || !offer.interestedUsers.contains(currentUser?.id) -> OfferState.DEFAULT
			offer.approvedUsers.isNullOrEmpty() || !offer.approvedUsers.contains(currentUser?.id) -> OfferState.PENDING
			else -> OfferState.APPROVED
		}
	}

	fun onInterestedClicked() {
		changeConnection(true)
		changeInterestedUser(true)
	}

	fun onInterestedCancelled() {
		changeInterestedUser(false)
		changeConnection(false)
	}


	private fun changeInterestedUser(add: Boolean) {
		val offer = offer.value!!
		val interestedUsers = offer.interestedUsers ?: mutableListOf()
		if (add) interestedUsers.add(currentUser!!.id) else interestedUsers.remove(currentUser!!.id)

		firebaseService.updateDocument(Offer.DOC_NAME, offer.id, "interestedUsers", interestedUsers) { task ->
			if (task.isSuccessful) {
				offer.interestedUsers = interestedUsers
			}
		}
	}

	private fun changeConnection(add: Boolean) {
		val offer = offer.value!!
		currentUser?.let { profile ->
            val connections = profile.connections
			if (add) {
                connections.add(offer.userID)
			} else {
                connections.remove(offer.userID)
			}

			firebaseService.updateDocument(Profile.DOC_NAME, currentUser!!.id, "connections", profile.connections) { task ->
				if (task.isSuccessful) {
					profile.connections = connections
				}
			}
			if (add) {
				firebaseService.sendInvitationMessage(profile.id, offer.userID) { task ->
					if (!task.isSuccessful) {
						state.value = States.ERROR
					}
				}
			} else {
				firebaseService.deleteConversation(profile.id, offer.userID)
			}

		}
	}
}
