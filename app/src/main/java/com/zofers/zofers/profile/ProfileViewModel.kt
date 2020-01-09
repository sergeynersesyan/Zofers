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

	fun logout() {
		currentUser = null
		auth.signOut()
	}

	fun init() {
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
		profile.value = currentUser
	}

	fun onNewProfileImage(context: Context, uri: Uri) {
		profile.value?.let { user ->
			state.value = States.LOADING
			uploadImage(context, uri, "images/user${user.id}/profile") { url ->
				val db = FirebaseFirestore.getInstance()
				db.collection("profile")
						.document(user.id)
						.update("avatarUrl", url.toString())
						.addOnCompleteListener { task ->
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
				updateProfilePrivateImages(user.id, user.privateImages)
				state.value = States.NONE
				currentUser = user
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
