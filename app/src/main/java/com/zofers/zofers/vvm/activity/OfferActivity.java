package com.zofers.zofers.vvm.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.zofers.zofers.R;
import com.zofers.zofers.model.Offer;
import com.zofers.zofers.vvm.viewmodel.OfferViewModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfferActivity extends BaseActivity {

    public static final String EXTRA_OFFER = "key_offer";

    private SimpleDraweeView image;
    private TextView description;

    private Offer offer;
    private OfferViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        offer = getIntent().getParcelableExtra(EXTRA_OFFER);
        setTitle(offer.getName());

        image = findViewById(R.id.cover_image);
        description = findViewById(R.id.description_textView);

        image.setImageURI(offer.getImageUrl());
        description.setText(offer.getDescription());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show());

        viewModel = ViewModelProviders.of(this).get(OfferViewModel.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_offer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                viewModel.delete(offer);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
