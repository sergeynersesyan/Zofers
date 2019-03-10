package com.zofers.zofers.vvm.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.zofers.zofers.R;
import com.zofers.zofers.adapter.OffersAdapter;
import com.zofers.zofers.model.Offer;
import com.zofers.zofers.staff.MessageHelper;
import com.zofers.zofers.vvm.viewmodel.FeedViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private OffersAdapter adapter;
    private FeedViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CreateOfferActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.offers_recycler_view);
        adapter = new OffersAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        recyclerView.setAdapter(adapter);
        adapter.setListener(offer -> {
            Intent intent = new Intent(this, OfferActivity.class);
            intent.putExtra(OfferActivity.EXTRA_OFFER, offer);
            startActivity(intent);
        });

        viewModel = ViewModelProviders.of(this).get(FeedViewModel.class);
        viewModel.getFeed(new Callback<List<Offer>>() {
            @Override
            public void onResponse(Call<List<Offer>> call, Response<List<Offer>> response) {
                if (!response.isSuccessful()) {
                    MessageHelper.showErrorToast(HomeActivity.this, response.code() + "");
                    return;
                }
                adapter.setItems(response.body());
            }

            @Override
            public void onFailure(Call<List<Offer>> call, Throwable t) {
                MessageHelper.showNoConnectionToast(HomeActivity.this);
            }
        });
    }

}
