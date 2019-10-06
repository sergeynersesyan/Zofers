package com.zofers.zofers.vvm.viewmodel;

import android.net.Uri;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zofers.zofers.model.Offer;
import com.zofers.zofers.service.ServiceCallback;

public class OfferCreationFirebaseViewModel extends AppViewModel {

	private Offer offer;

	public void createOffer(Uri image, ServiceCallback<Offer> callback) {
//


		offer.setUserID(getCurrentUser().getUid());
		// Create a storage reference from our app
		StorageReference storageRef = FirebaseStorage.getInstance().getReference();

		// Create a reference to "mountains.jpg"
		StorageReference imagesRef = storageRef.child("images/" + image.getLastPathSegment());


		UploadTask uploadTask = imagesRef.putFile(image);


		uploadTask.addOnFailureListener(exception -> {
		}).addOnSuccessListener(taskSnapshot -> {
			imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
				offer.setImageUrl(uri.toString());
				FirebaseFirestore.getInstance().collection("offer")
						.add(offer)
						.addOnSuccessListener(documentReference -> {
							callback.onSuccess(null); // todo change null
						})
						.addOnFailureListener(e -> callback.onFailure());


			});


		});


	}

	public Offer getOffer() {
		if (offer == null) {
			offer = new Offer();
		}
		return offer;
	}

	public void setOffer(Offer offer) {
		this.offer = offer;
	}
}
