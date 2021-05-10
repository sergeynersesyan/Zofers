package com.zofers.zofers.ui.offer


import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.zofers.zofers.AppViewModel
import com.zofers.zofers.event.OfferDeleteEvent
import com.zofers.zofers.model.Offer
import com.zofers.zofers.model.Profile
import com.zofers.zofers.staff.AnalyticsEventLogger
import com.zofers.zofers.staff.States
import org.greenrobot.eventbus.EventBus


class OfferViewModel : AppViewModel() {

	val offer = MutableLiveData<Offer>()
	val user = MutableLiveData<Profile>()
	val showDialogEvent = MutableLiveData<Boolean>(false)
	val showCanceledMessageEvent = MutableLiveData<Boolean>(false)
	val showReportedMessageEvent = MutableLiveData<Boolean>(false)
	val startLoginActivityEvent = MutableLiveData<Boolean>(false)
	val updateViewEvent = MutableLiveData<Boolean>(false)

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
		state.value = States.LOADING
		offer.value?.imageURL?.let { url ->
			deleteImage(url) {
				deleteOfferDocument()
			}
		} ?: deleteOfferDocument()
		AnalyticsEventLogger.logDeleteOfferEvent(offer.value?.name.orEmpty())
	}

	fun report() {
		if (isLoggedOut()) {
			startLoginActivityEvent.value = true
			return
		}

		offer.value?.let {
			val reportedOfferIDs = currentUser?.reportedOfferIDs ?: mutableListOf()
			reportedOfferIDs.add(it.id.orEmpty())
			currentUser?.let { user ->
				firebaseService.updateDocument(Profile.DOC_NAME, user.id, "reportedOfferIDs", reportedOfferIDs) { task ->
					if (task.isSuccessful) {
						user.reportedOfferIDs = reportedOfferIDs
						currentUser = user
						EventBus.getDefault().post(OfferDeleteEvent(offer.value))
						showReportedMessageEvent.value = true
					}
				}
			}
		}
	}

	fun isCurrentUserOffer(): Boolean {
		return offer.value?.userID == currentUser?.id
	}

	fun getOfferState(): OfferState {
		val offer = offer.value!!
		return when {
			isCurrentUserOffer() -> OfferState.MY
			offer.interestedUsers.isNullOrEmpty() || !offer.interestedUsers.contains(currentUser?.id) -> OfferState.DEFAULT
//			offer.approvedUsers.isNullOrEmpty() || !offer.approvedUsers.contains(currentUser?.id) -> OfferState.PENDING
			else -> OfferState.PENDING
		}
	}

	fun onInterestedClicked() {
		offer.value?.let { ofo ->
			AnalyticsEventLogger.logInterestedButtonClickEvent(
					offerName = ofo.name,
					isInterested = getOfferState() == OfferState.DEFAULT
			)
		}

		if (isLoggedOut()) {
			startLoginActivityEvent.value = true
			return
		}

		when (getOfferState()) {
			OfferState.DEFAULT -> {
				onInterested()
			}
			OfferState.PENDING -> {
				onCancelled()
			}
			OfferState.MY,
			OfferState.APPROVED -> {
				//ignore
			}
		}


	}

	private fun onInterested() {
		changeConnection(true)
		changeInterestedUser(true)
	}

	private fun onCancelled() {
		changeInterestedUser(false)
//		changeConnection(false) // do not remove because they may have conversation, or applied for other offer
		currentUser?.let { profile ->
			showCanceledMessageEvent.value = true
			firebaseService.deleteConversationIfOneMessage(profile.id, offer.value?.userID.orEmpty())
		}
	}

	private fun deleteOfferDocument() {
		firebaseService.deleteDocument("offer", offer.value!!.id) { task ->
			state.value = if (task.isSuccessful) {
				EventBus.getDefault().post(OfferDeleteEvent(offer.value))
				States.FINISH
			} else {
				States.ERROR
			}
		}
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
			if (add) {
				profile.connections.add(offer.userID)
			} else {
				profile.connections.remove(offer.userID)
			}

			firebaseService.updateDocument(Profile.DOC_NAME, currentUser!!.id, "connections", profile.connections) { task ->
				if (task.isSuccessful) {
					currentUser = profile
				}
			}
			if (add) {
				firebaseService.sendInvitationMessage(
						fromId = profile.id,
						toId = offer.userID,
						offerID = offer.id
				) { task ->
					if (task.isSuccessful) {
						showDialogEvent.value = userManager.showInterestedRequestAlert
					} else {
						state.value = States.ERROR
					}
				}
			} else {
				showCanceledMessageEvent.value = true
				firebaseService.deleteConversationIfOneMessage(profile.id, offer.userID)
			}

		}
	}

	fun onAlertOkClicked(doNotShowChecked: Boolean) {
		userManager.showInterestedRequestAlert = !doNotShowChecked
	}

	fun onLoggedIn() {
		updateViewEvent.value = true
	}
}
