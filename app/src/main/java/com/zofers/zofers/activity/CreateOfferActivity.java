package com.zofers.zofers.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.zofers.zofers.R;
import com.zofers.zofers.fragment.CreateOfferBaseFragment;
import com.zofers.zofers.fragment.CreateOfferFirstFragment;
import com.zofers.zofers.model.Offer;

public class CreateOfferActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String KEY_OFFER = "ext_k_off";
    private Button nextButton;
    private ProgressBar progressBar;
    private FrameLayout fragmentContainer;

    private CreateOfferBaseFragment fragment;

    private Offer offer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_offer);
        nextButton = findViewById(R.id.next_button);
        progressBar = findViewById(R.id.progress);
        fragmentContainer = findViewById(R.id.fragment_container);
        fragment = new CreateOfferFirstFragment();
        nextButton.setOnClickListener(this);

        offer = getIntent().getParcelableExtra(KEY_OFFER);
        if (offer == null) {
            offer = new Offer();
        }
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
                    offer = fragment.fillOffer(offer);
                    if (fragment.nextFragment() == null) {
                        createOffer();
                    } else {
                        getIntent().putExtra(KEY_OFFER, offer);
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

    private void createOffer() {

    }
}
