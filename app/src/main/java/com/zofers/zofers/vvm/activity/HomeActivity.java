package com.zofers.zofers.vvm.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.zofers.zofers.R;
import com.zofers.zofers.adapter.OffersAdapter;
import com.zofers.zofers.model.Offer;
import com.zofers.zofers.staff.MessageHelper;
import com.zofers.zofers.vvm.viewmodel.FeedViewModel;
import com.zofers.zofers.vvm.viewmodel.States;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private OffersAdapter adapter;
    private FeedViewModel viewModel;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle(null);
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
        viewModel.load();
        viewModel.getOffersList().observe(this, offers -> {
            adapter.setItems(offers);
        });
        viewModel.getState().observe(this, state -> {
            switch (state) {
                case States.ERROR:
                    MessageHelper.showErrorToast(HomeActivity.this, "");
                    break;
                case States.FAIL:
                    MessageHelper.showNoConnectionToast(HomeActivity.this);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        searchView.setMaxWidth(Integer.MAX_VALUE);
//        EditText searchEditText = searchView.findViewById(R.id.search_src_text);
//        searchItem.expandActionView();

        searchView.setOnQueryTextListener(this);
        searchView.setIconified(false);
        searchView.setGravity(Gravity.LEFT);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                onBackPressed();
                return true;
            }
        });
        searchView.clearFocus();

        View closeButton = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> {
                searchView.setQuery("", false);
                searchView.clearFocus();
            });
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        viewModel.load(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (TextUtils.isEmpty(s)) {
            onQueryTextSubmit(null);
            return true;
        }
        return false;
    }
}
