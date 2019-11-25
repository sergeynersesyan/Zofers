package com.zofers.zofers

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.zofers.zofers.callback.PermissionRequestCallback
import com.zofers.zofers.service.RetrofitProvider
import com.zofers.zofers.staff.States
import java.io.ByteArrayOutputStream
import java.io.File
import android.graphics.BitmapFactory
import com.zofers.zofers.staff.FileHelper


open class AppViewModel : ViewModel() {
	var state: MutableLiveData<Int>
	protected var retrofitProvider: RetrofitProvider
	val auth = FirebaseAuth.getInstance()
	val currentUser
		get() = auth.currentUser

	init {
		state = MutableLiveData()
		state.value = States.NONE
		retrofitProvider = RetrofitProvider.getInstance()
	}

	fun uploadImage(context: Context, imageUri: Uri, pathString: String, onSuccess: ((Uri) -> Unit)) {
		// Create a storage reference from our app
		val storageRef = FirebaseStorage.getInstance().reference
		// Create a reference to "mountains.jpg"
		val imagesRef = storageRef.child(pathString)

		val uploadTask =  imagesRef.putBytes(FileHelper.getImageBinary(context, imageUri))
//		val uploadTask = imagesRef.putFile(imageUri)
		uploadTask
				.addOnFailureListener {
					state.value = States.FAIL
				}
				.addOnSuccessListener {
					imagesRef.downloadUrl.addOnSuccessListener(onSuccess)
				}

	}

}
