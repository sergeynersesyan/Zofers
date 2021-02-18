package com.zofers.zofers

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.zofers.zofers.firebase.FirebaseService
import com.zofers.zofers.model.Profile
import com.zofers.zofers.staff.FileHelper
import com.zofers.zofers.staff.States
import com.zofers.zofers.staff.UserManager


open class AppViewModel : ViewModel() {
	var state: MutableLiveData<Int> = MutableLiveData()
	val auth = FirebaseAuth.getInstance()
	var userManager: UserManager
	var currentUser: Profile?
		get() {
			return userManager.userProfile
		}
		set(value) {
			userManager.userProfile = value
		}
	var firebaseService = FirebaseService() //todo integrate dagger

	init {
		state.value = States.NONE
		userManager = App.instance.userManager
	}

	fun uploadImage(context: Context, imageUri: Uri, pathString: String, onSuccess: ((Uri) -> Unit)) = uploadImage(
			context = context,
			imageBytes = FileHelper.getImageBinary(context, imageUri),
			pathString = pathString,
			onSuccess = onSuccess
	)


	fun uploadImage(context: Context, imageBytes: ByteArray, pathString: String, onSuccess: ((Uri) -> Unit)) {
		// Create a storage reference from our app
		val storageRef = FirebaseStorage.getInstance().reference
		// Create a reference to "mountains.jpg"
		val imagesRef = storageRef.child(pathString)

		val uploadTask = imagesRef.putBytes(imageBytes)
//		val uploadTask = imagesRef.putFile(imageUri)
		uploadTask
				.addOnFailureListener {
					state.value = States.FAIL
				}
				.addOnSuccessListener {
					imagesRef.downloadUrl.addOnSuccessListener(onSuccess)
				}
	}

	fun deleteImage(url: String, onComplete: (Task<Void>) -> Unit) {
		val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url)
		storageReference.delete().addOnCompleteListener(onComplete)
	}

	fun isLoggedOut(): Boolean {
		return currentUser == null || FirebaseAuth.getInstance().currentUser == null
	}
}
