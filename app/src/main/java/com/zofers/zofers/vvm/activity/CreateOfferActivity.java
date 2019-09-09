package com.zofers.zofers.vvm.activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.zofers.zofers.R;
import com.zofers.zofers.model.Offer;
import com.zofers.zofers.service.ServiceCallback;
import com.zofers.zofers.staff.FileUtils;
import com.zofers.zofers.staff.MessageHelper;
import com.zofers.zofers.view.LoadingDialog;
import com.zofers.zofers.vvm.fragment.CreateOfferBaseFragment;
import com.zofers.zofers.vvm.fragment.CreateOfferFirstFragment;
import com.zofers.zofers.vvm.viewmodel.ItemCreationViewModel;
import com.zofers.zofers.vvm.viewmodel.OfferCreationFirebaseViewModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateOfferActivity extends BaseActivity implements View.OnClickListener {
	public static final String KEY_OFFER = "ext_k_off";
	public static final String EXTRA_IMAGE_BYTES = "ext_im_bs";
	public static final String EXTRA_IMAGE_URI = "ext_im_uri";
	private Button nextButton;
	private ProgressBar progressBar;
	private FrameLayout fragmentContainer;
	private LoadingDialog loadingDialog = new LoadingDialog();

	private CreateOfferBaseFragment fragment;

	private OfferCreationFirebaseViewModel viewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_offer);
		setTitle(R.string.title_activity_create_offer);
		nextButton = findViewById(R.id.next_button);
		progressBar = findViewById(R.id.progress);
		fragmentContainer = findViewById(R.id.fragment_container);
		fragment = new CreateOfferFirstFragment();
		nextButton.setOnClickListener(this);

		viewModel = ViewModelProviders.of(this).get(OfferCreationFirebaseViewModel.class);

//        offer = getIntent().getParcelableExtra(EXTRA_OFFER);
//        if (offer == null) {
//            offer = new Offer();
//        }
		openFragment(false);
	}

	private void openFragment(boolean addToBackStack) {
		FragmentTransaction transaction =
				getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.fragment_container, fragment);
		if (addToBackStack) {
			transaction.addToBackStack("create offer");
		}
		transaction.commit();
		onFragmentChange();

	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.next_button:
				if (!fragment.validFilled()) break;

				Offer offer = fragment.fillOffer(viewModel.getOffer());
				if (fragment.nextFragment() == null) {
					Uri fileUri = getIntent().getParcelableExtra(EXTRA_IMAGE_URI);
					loadingDialog.show(getSupportFragmentManager(), null);
					viewModel.createOffer(fileUri, new ServiceCallback<Offer>() {
						@Override
						public void onSuccess(Offer response) {
							finish();
							loadingDialog.dismiss();
						}

						@Override
						public void onFailure() {
							loadingDialog.dismiss();
							MessageHelper.showNoConnectionToast(CreateOfferActivity.this);
						}
					});
				} else {
					viewModel.setOffer(offer);
					fragment = fragment.nextFragment();
					openFragment(true);
				}

				break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		fragment = (CreateOfferBaseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		if (fragment != null) {
			onFragmentChange();
		}
	}

	private void onFragmentChange() {
		progressBar.setProgress(fragment.getProgress());
		nextButton.setText(fragment.getNextButtonTextResource());
	}
}
