package com.zofers.zofers.offer;


import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.zofers.zofers.model.Offer;

public class OfferViewModel extends ViewModel {

	public void delete(Offer offer) {
		FirebaseFirestore.getInstance()
				.collection("offer")
				.document(offer.getId())
				.delete();
//				.addOnSuccessListener();
	}
}
