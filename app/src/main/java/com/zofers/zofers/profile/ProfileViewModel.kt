package com.zofers.zofers.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.zofers.zofers.AppViewModel
import com.zofers.zofers.model.Offer
import com.zofers.zofers.model.Profile
import com.zofers.zofers.staff.States
import java.util.*

class ProfileViewModel : AppViewModel() {
	val offersList = MutableLiveData<List<Offer>>()
	val profile = MutableLiveData<Profile>()
	lateinit var profileID: String

	fun logout() {
		currentUser = null
		auth.signOut()
	}

	fun init(userID: String) {
		profileID = userID
		FirebaseFirestore.getInstance()
				.collection("offer")
				.whereEqualTo("userID", currentUser!!.id)
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

		if (profileID == currentUser?.id) {
			profile.value = currentUser
		} else {
			FirebaseFirestore.getInstance().collection("profile").document(profileID)
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
			uploadImage(context, uri, "images/user${user.id}/profile") { url ->
				updateDocument("profile", user.id, "avatarUrl", url.toString()) { task ->
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
			uploadImage(context, uri, "images/user/${user.id}/private/$uri") { url ->
				user.privateImages.add(url.toString())
				updateDocument("profile", user.id, "privateImages", user.privateImages) { task ->
					state.value = if (task.isSuccessful) {
						currentUser = user
						States.NONE
					} else {
						user.privateImages.remove(url.toString())
						States.ERROR
					}
				}
			}
		}
	}


	private fun updateProfilePrivateImages(documentID: String, privateImages: MutableList<String>) {
		val db = FirebaseFirestore.getInstance()
		val profileDocRef = db.collection("profile").document(documentID)

		db.runTransaction { transaction ->
			val snapshot = transaction.get(profileDocRef)

			transaction.update(profileDocRef, "privateImages", privateImages)

			// Success
			null
		}
	}


}
