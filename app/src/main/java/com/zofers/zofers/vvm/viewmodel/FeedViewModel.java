package com.zofers.zofers.vvm.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.zofers.zofers.model.Offer;
import com.zofers.zofers.service.RetrofitProvider;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedViewModel extends AppViewModel {

	private MutableLiveData<List<Offer>> offersList = new MutableLiveData<>();

	public MutableLiveData<List<Offer>> getOffersList() {
		return offersList;
	}

	public void load() {
		load(null);
	}

	public void load(@Nullable String query) {
		Call<List<Offer>> call = RetrofitProvider.getInstance().getOfferApi().getFeed(query);
		call.enqueue(new Callback<List<Offer>>() {
			@Override
			public void onResponse(Call<List<Offer>> call, Response<List<Offer>> response) {
				if (!response.isSuccessful()) {
					state.setValue(States.ERROR);
					return;
				}
				offersList.setValue(response.body());
			}

			@Override
			public void onFailure(Call<List<Offer>> call, Throwable t) {
				state.setValue(States.FAIL);
			}
		});
	}

	public void loadFirebase() {
		FirebaseFirestore.getInstance().collection("offer")
				.get()
				.addOnCompleteListener(task -> {
					if (task.isSuccessful()) {
						List<Offer> offers = new ArrayList<>();
						for (QueryDocumentSnapshot document : task.getResult()) {
							offers.add(document.toObject(Offer.class));
						}
						offersList.setValue(offers);
					} else {
							state.setValue(States.FAIL);
					}
				});
	}
}
