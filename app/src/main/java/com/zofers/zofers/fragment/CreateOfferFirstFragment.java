package com.zofers.zofers.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.zofers.zofers.R;
import com.zofers.zofers.model.Offer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateOfferFirstFragment extends CreateOfferBaseFragment {

    private Spinner countrySpinner;
    private EditText cityEdittext;
    private RadioGroup radioGroup;
    //    private RadioButton expensesMeRB;
    private EditText costEdittext;
    private EditText currencyEdittext;
    View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_create_offer_first, container, false);

        countrySpinner = view.findViewById(R.id.country_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, countryList());
        countrySpinner.setAdapter(adapter);

        cityEdittext = view.findViewById(R.id.city_editText);
        costEdittext = view.findViewById(R.id.cost_editText);
        currencyEdittext = view.findViewById(R.id.currency_editText);

        radioGroup = view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                boolean freeForGuest = checkedId == R.id.expensesMe;
                costEdittext.setEnabled(!freeForGuest);
                currencyEdittext.setEnabled(!freeForGuest);
            }
        });
        return view;
    }

    @Override
    public int getProgress() {
        return 33;
    }

    @Override
    public boolean validFilled() {
        boolean validFilled = true;
        if (countrySpinner.getSelectedItemPosition() == 0) {
            Snackbar.make(view,"Please select country", Snackbar.LENGTH_SHORT).show();
            validFilled = false;
        }
        if (cityEdittext.getText().toString().trim().length() == 0) {
            cityEdittext.setError("Please enter city");
            validFilled = false;
        }
        if (radioGroup.getCheckedRadioButtonId() == -1 && validFilled) {
            Snackbar.make(view,"Please select who will pay Expenses", Snackbar.LENGTH_SHORT).show();
            validFilled = false;
        }
        if (radioGroup.getCheckedRadioButtonId() != R.id.expensesMe && costEdittext.getText().length() == 0) {
            costEdittext.setError("PLease enter cost");
            validFilled = false;
        }
        if (currencyEdittext.getText().toString().trim().length() == 0) {
            costEdittext.setError("PLease enter currency");
            validFilled = false;
        }
        return validFilled;
    }

    @Override
    public CreateOfferBaseFragment nextFragment() {
        return new CreateOfferSecoundFragment();
    }

    @Override
    public Offer fillOffer(Offer offer) {
        offer.setCountry(countrySpinner.getSelectedItem().toString());
        offer.setCity(cityEdittext.getText().toString().trim());
        offer.setCostMode(costMode());
        if (radioGroup.getCheckedRadioButtonId() != R.id.expensesMe) {
            String cost = costEdittext.getText().toString();
            offer.setCost(Integer.parseInt(cost));
            String currency = currencyEdittext.getText().toString().trim();
            offer.setCurrency(currency);
        }
        return offer;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private ArrayList<String> countryList() {
        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        String country;
        for (Locale loc : locale) {
            country = loc.getDisplayCountry();
            if (country.length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);
        countries.add(0, "Select Country");
        return countries;
    }

    private int costMode() {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.expensesMe:
                return Offer.COST_MODE_CREATOR;
            case R.id.expensesBoth:
                return Offer.COST_MODE_BOTH;
            case R.id.expensesGuest:
                return Offer.COST_MODE_GUEST;
            default:
                return Offer.COST_MODE_CREATOR;
        }
    }
}