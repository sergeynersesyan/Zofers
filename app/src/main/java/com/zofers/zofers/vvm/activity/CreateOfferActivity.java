package com.zofers.zofers.vvm.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zofers.zofers.App;
import com.zofers.zofers.R;
import com.zofers.zofers.model.Offer;
import com.zofers.zofers.staff.FileUtils;
import com.zofers.zofers.staff.MessageHelper;
import com.zofers.zofers.view.LoadingDialog;
import com.zofers.zofers.vvm.fragment.CreateOfferBaseFragment;
import com.zofers.zofers.vvm.fragment.CreateOfferFirstFragment;
import com.zofers.zofers.vvm.viewmodel.ItemCreationViewModel;

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

    private ItemCreationViewModel viewModel;

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

        viewModel = ViewModelProviders.of(this).get(ItemCreationViewModel.class);

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
                if (fragment.validFilled()) {
                    Offer offer = fragment.fillOffer(viewModel.getOffer());
                    if (fragment.nextFragment() == null) {
                        Uri fileUri = getIntent().getParcelableExtra(EXTRA_IMAGE_URI);
                        loadingDialog.show(getSupportFragmentManager(), null);
                        viewModel.createOffer(FileUtils.getFile(this, fileUri), new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    finish();
                                } else {
                                    MessageHelper.showErrorToast(CreateOfferActivity.this, response.code() + "");
                                }
                                loadingDialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                loadingDialog.dismiss();
                                MessageHelper.showNoConnectionToast(CreateOfferActivity.this);
                            }
                        });
                    } else {
                        viewModel.setOffer(offer);
                        fragment = fragment.nextFragment();
                        openFragment(true);
                    }
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
