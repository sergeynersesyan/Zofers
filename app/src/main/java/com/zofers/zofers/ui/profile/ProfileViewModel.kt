package com.zofers.zofers.ui.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.zofers.zofers.AppViewModel
import com.zofers.zofers.event.OfferCreateEvent
import com.zofers.zofers.event.OfferDeleteEvent
import com.zofers.zofers.firebase.FirebaseService
import com.zofers.zofers.model.Offer
import com.zofers.zofers.model.Profile
import com.zofers.zofers.staff.States
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

class ProfileViewModel : AppViewModel() {
	val offersList = MutableLiveData<List<Offer>>()
	val profile = MutableLiveData<Profile>()
	var isCurrentUser = false
	val isConnected
		get() = profile.value?.connections?.contains(currentUser?.id) == true
	var isOffersLoaded = false
	val showBlockedMessageEvent = MutableLiveData<Boolean>(false)

	fun logout() {
		currentUser = null
		firebaseService.deleteDeviceToken()
		auth.signOut()
	}

	fun init(userID: String) {
		FirebaseFirestore.getInstance()
				.collection("offer")
				.whereEqualTo("userID", userID)
				.get()
				.addOnCompleteListener { task ->
					isOffersLoaded = true
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

		isCurrentUser = userID == currentUser?.id
		if (isCurrentUser) {
			profile.value = currentUser
		} else {
			FirebaseFirestore.getInstance().collection("profile").document(userID)
					.get()
					.addOnCompleteListener { task ->
						if (task.isSuccessful) {
							val prof = task.result?.toObject(Profile::class.java)
							prof?.let {
								profile.value = prof
							}
						} else {
							state.postValue(States.FAIL)
						}
					}
		}
		EventBus.getDefault().register(this)
	}

	fun refreshProfile() {
		profile.postValue(currentUser)
	}

	@Subscribe
	fun onOfferDelete(offerDeleteEvent: OfferDeleteEvent) {
		offersList.value = offersList.value?.filter { it.id != offerDeleteEvent.offer?.id }
	}

	@Subscribe
	fun onOfferCreate(offerCreateEvent: OfferCreateEvent) {
		offerCreateEvent.offer?.let {
			val offers: MutableList<Offer> = offersList.value?.toMutableList() ?: mutableListOf()
			offers.add(it)
			offersList.value = offers
		}
	}

	fun onNewProfileImage(context: Context, uri: Uri) {
		profile.value?.let { user ->
			state.value = States.LOADING
			uploadImage(context, uri, "images/user/${user.id}/profile") { url ->
				firebaseService.updateDocument("profile", user.id, "avatarURL", url.toString()) { task ->
					state.value = if (task.isSuccessful) {
						user.avatarURL = url.toString()
						currentUser = user
						profile.value = user
						firebaseService.updateAvatarInConversations(url.toString(), currentUser!!.id)
						firebaseService.updateAvatarInOffers(url.toString(), currentUser!!.id)
						States.NONE
					} else {
						States.ERROR
					}
				}
			}
		}
	}

	fun onNewPrivateImage(context: Context, uri: Uri) {
		profile.value?.let { user ->
			state.value = States.LOADING
			uploadImage(context, uri, "images/user/${user.id}/private/${uri.lastPathSegment + getRandomString(8)}") { url ->
				user.privateImages.add(url.toString())
				firebaseService.updateDocument("profile", user.id, "privateImages", user.privateImages) { task ->
					state.value = if (task.isSuccessful) {
						currentUser = user
						profile.value = user
						States.NONE
					} else {
						user.privateImages.remove(url.toString())
						States.ERROR
					}
				}
			}
		}
	}

	fun deleteImage(url: String?) {
		url?.let {
			deleteImage(it) {
				profile.value?.let { user ->
					user.privateImages.remove(url)
					firebaseService.updateDocument("profile", user.id, "privateImages", user.privateImages) { task ->
						state.value = if (task.isSuccessful) {
							currentUser = user
							profile.value = user
							States.DONE
						} else {
							user.privateImages.add(url)
							States.ERROR
						}
					}
				}
				Log.e("firebasestorage", "onSuccess: deleted file")
			}
		}

	}

	private fun getRandomString(length: Int): String {
		val allowedChars = ('A'..'Z') + ('a'..'z')
		return (1..length)
				.map { allowedChars.random() }
				.joinToString("")
	}

	fun destroy() {
		EventBus.getDefault().unregister(this)
	}

	fun blockUser() {
		profile.value?.let {
			currentUser?.let { curUser ->

				val editFieldsMap = mutableMapOf<String, Any>()

				val blockedUserIDs = currentUser?.blockedUserIDs ?: mutableListOf()
				blockedUserIDs.add(it.id)
				editFieldsMap["blockedUserIDs"] = blockedUserIDs
				val connections = curUser.connections
				if (connections.contains(it.id)) {
					connections.remove(it.id)
					editFieldsMap["connections"] = connections
				}

				firebaseService.updateDocument(Profile.DOC_NAME, curUser.id, editFieldsMap) { task ->
					if (task.isSuccessful) {
						curUser.blockedUserIDs = blockedUserIDs
						curUser.connections = connections
						currentUser = curUser
						showBlockedMessageEvent.value = true
					}
				}
			}

		}
	}

}
