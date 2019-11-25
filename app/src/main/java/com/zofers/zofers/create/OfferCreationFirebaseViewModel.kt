package com.zofers.zofers.create

import android.content.Context
import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.zofers.zofers.AppViewModel
import com.zofers.zofers.model.Offer
import com.zofers.zofers.service.ServiceCallback

class OfferCreationFirebaseViewModel : AppViewModel() {

	var offer: Offer = Offer()

	fun createOffer(context: Context, image: Uri, callback: ServiceCallback<Offer>) {
		offer.userID = currentUser?.uid

		uploadImage(context, image, "images/offers/" + image.lastPathSegment!!) { uri ->
			offer.imageUrl = uri.toString()
			FirebaseFirestore.getInstance().collection("offer")
					.add(offer)
					.addOnSuccessListener {
						callback.onSuccess(null) // todo change null
					}
					.addOnFailureListener {
						callback.onFailure()
					}
		}
	}
}
