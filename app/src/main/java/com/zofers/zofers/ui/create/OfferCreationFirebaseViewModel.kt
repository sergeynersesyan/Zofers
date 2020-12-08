package com.zofers.zofers.ui.create

import android.content.Context
import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.zofers.zofers.AppViewModel
import com.zofers.zofers.model.Offer
import com.zofers.zofers.model.Owner
import com.zofers.zofers.service.ServiceCallback
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

	fun createOffer(context: Context, image: Uri?, callback: ServiceCallback<Offer>) {
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

		image?.let {
			uploadImage(context, image, "images/offers/" + image.lastPathSegment!!) { uri ->
				offer.imageURL = uri.toString()
				writeOffer(callback)
			}
		} ?: writeOffer(callback)

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
}
