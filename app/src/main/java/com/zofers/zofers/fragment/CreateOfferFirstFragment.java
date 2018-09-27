package com.zofers.zofers.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.zofers.zofers.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateOfferFirstFragment extends CreateOfferBaseFragment {

    private Spinner countrySpinner;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_offer_first, container, false);
        countrySpinner = view.findViewById(R.id.country_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, countryList());
        countrySpinner.setAdapter(adapter);
        return view;
    }

    @Override
    public int getProgress() {
        return 33;
    }

    @Override
    public boolean validFilled() {
        return true;
    }

    @Override
    public CreateOfferBaseFragment nextFragment() {
        return new CreateOfferSecoundFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private ArrayList<String> countryList () {
        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        String country;
        for( Locale loc : locale ){
            country = loc.getDisplayCountry();
            if( country.length() > 0 && !countries.contains(country) ){
                countries.add( country );
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);
        return countries;
    }
}
