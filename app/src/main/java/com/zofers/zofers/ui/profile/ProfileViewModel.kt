package com.zofers.zofers.ui.profile

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.zofers.zofers.AppViewModel
import com.zofers.zofers.model.Offer
import com.zofers.zofers.model.Profile
import com.zofers.zofers.staff.States
import java.net.URI
import java.util.*

class ProfileViewModel : AppViewModel() {
	val offersList = MutableLiveData<List<Offer>>()
	val profile = MutableLiveData<Profile>()
	var isCurrentUser = false
	val isConnected
		get() = profile.value?.connections?.contains(currentUser?.id) == true

	fun logout() {
		currentUser = null
		auth.signOut()
	}

	fun init(userID: String) {
		FirebaseFirestore.getInstance()
				.collection("offer")
				.whereEqualTo("userID", userID)
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
	}

	fun onNewProfileImage(context: Context, uri: Uri) {
		profile.value?.let { user ->
			state.value = States.LOADING
			uploadImage(context, uri, "images/user/${user.id}/profile") { url ->
				firebaseService.updateDocument("profile", user.id, "avatarUrl", url.toString()) { task ->
					state.value = if (task.isSuccessful) {
						user.avatarUrl = url.toString()
						currentUser = user
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
			//			var documentPath = url
//			val query = url.toUri().query
//			if (!query.isNullOrEmpty()) {
//				documentPath = url.substring(0, url.indexOf(query) - 1) + ".jpeg"
//			}

//			deleteImage(documentPath) { task ->
//				if (task.isSuccessful) {
			profile.value?.let { user ->
				user.privateImages.remove(url)
				firebaseService.updateDocument("profile", user.id, "privateImages", user.privateImages) { task ->
					state.value = if (task.isSuccessful) {
						currentUser = user
						profile.value = user
						States.DONE
					} else {
						user.privateImages.remove(url)
						States.ERROR
					}
				}
			}
//				} else {
//					state.value = States.ERROR
//				}
//		}
		}

	}

	private fun getRandomString(length: Int) : String {
		val allowedChars = ('A'..'Z') + ('a'..'z')
		return (1..length)
				.map { allowedChars.random() }
				.joinToString("")
	}

}
