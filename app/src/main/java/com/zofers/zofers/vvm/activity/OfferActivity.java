package com.zofers.zofers.vvm.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        offer = getIntent().getParcelableExtra(EXTRA_OFFER);
        setTitle(offer.getName());

        image = findViewById(R.id.cover_image);
        description = findViewById(R.id.description_textView);

        image.setImageURI(offer.getImageUrl());
        description.setText(offer.getDescription());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

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
                viewModel.delete(offer.getId(), new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (isFinishing()) return;
                        if (response.isSuccessful()) finish();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
