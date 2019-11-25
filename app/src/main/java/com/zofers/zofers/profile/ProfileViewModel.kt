package com.zofers.zofers.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.UserProfileChangeRequest
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
		auth.signOut()
	}

	fun init() {
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

		FirebaseFirestore.getInstance().collection("profile").document(currentUser!!.uid)
				.get()
				.addOnCompleteListener { task ->
					if (task.isSuccessful) {
						val prof =  task.result?.toObject(Profile::class.java)
						prof?.let {
							profile.value = prof
						}
					} else {
						state.setValue(States.FAIL)
					}
				}
	}

	fun onNewProfileImage(context: Context, uri: Uri) {
		currentUser?.let { user ->
			state.value = States.LOADING
			uploadImage(context, uri, "images/user${user.uid}/profile") { url ->
				val profileUpdates = UserProfileChangeRequest.Builder()
						.setPhotoUri(url)
						.build()

				user.updateProfile(profileUpdates)
						.addOnCompleteListener { task ->
							if (task.isSuccessful) {
								state.value = States.NONE
							}
						}
			}
		}
	}

	fun onNewPrivateImage(context: Context, uri: Uri) {
		currentUser?.let { user ->
			state.value = States.LOADING
			uploadImage(context, uri, "images/user/${user.uid}/private/$uri") { url ->
				//					profile.
				profile.value?.let {
					it.privateImages.add(url.toString())
					updateProfilePrivateImages(user.uid, it.privateImages)
					state.value = States.NONE
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
