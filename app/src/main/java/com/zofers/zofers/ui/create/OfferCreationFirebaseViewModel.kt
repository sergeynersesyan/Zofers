package com.zofers.zofers.ui.create

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.zofers.zofers.AppViewModel
import com.zofers.zofers.model.Offer
import com.zofers.zofers.model.Owner
import com.zofers.zofers.service.ServiceCallback
import java.io.ByteArrayOutputStream
import java.util.*


class OfferCreationFirebaseViewModel : AppViewModel() {

	var offer: Offer = Offer()
	var isEditMode: Boolean = false

	fun init(offer: Offer?) {
		if (offer != null) {
			isEditMode = true
			this.offer = offer
		}
	}

	fun createOffer(context: Context, imageUri: Uri?, imageBytes: ByteArray?, callback: ServiceCallback<Offer>) {
		offer.userID = currentUser?.id
		offer.owner = Owner().apply {
			id = currentUser?.id
			avatarURL = currentUser?.avatarURL
			name = currentUser?.name
		}
		offer.creationDate = Date()

		//create keywords
		val regex = Regex("\\W+")
		val nameParts = offer.name.toLowerCase(Locale.ROOT).split(regex)
		val keyWords = offer.city.toLowerCase(Locale.ROOT).split(regex).toMutableList()
		if (keyWords.size > 1) {
			keyWords.add(offer.city)
		}
		keyWords.addAll(nameParts)
		if (nameParts.size > 1) {
			keyWords.add(offer.name)
		}
		keyWords.addAll(offer.country.toLowerCase(Locale.ROOT).split(regex))
		keyWords.addAll(offer.description.toLowerCase(Locale.ROOT).split(regex))
		offer.keyWords = keyWords

		when {
			imageBytes != null -> {
				uploadImage(context, imageBytes, "images/offers/" + getRandomString(15)) { uri ->
					offer.imageURL = uri.toString()
					writeOffer(callback)
				}
			}
			imageUri != null -> {
				uploadImage(context, imageUri, "images/offers/" + getRandomString(15)) { uri ->
					offer.imageURL = uri.toString()
					writeOffer(callback)
				}
			}
			else -> {
				writeOffer(callback)
			}
		}

	}

	private fun writeOffer(callback: ServiceCallback<Offer>) {
		FirebaseFirestore.getInstance().collection("offer")
				.document(offer.id)
				.set(offer)
				.addOnSuccessListener {
					callback.onSuccess(offer)
				}
				.addOnFailureListener {
					callback.onFailure()
				}
	}

	private fun getRandomString(length: Int) : String {
		val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
		return (1..length)
				.map { allowedChars.random() }
				.joinToString("")
	}
}

fun Drawable.toByteArray(): ByteArray {
	val bitmap = (this as BitmapDrawable).bitmap
	val stream = ByteArrayOutputStream()
	bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
	return stream.toByteArray()
}