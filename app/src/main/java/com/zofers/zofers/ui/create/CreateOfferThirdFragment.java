package com.zofers.zofers.ui.create;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import com.zofers.zofers.R;
import com.zofers.zofers.model.Offer;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateOfferThirdFragment extends CreateOfferBaseFragment {
    private EditText peopleCountEdittext;
    private AppCompatSpinner genderSpinner;
    private EditText reqEdittext;
    private EditText availEdittext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_create_offer_third, container, false);
        genderSpinner = root.findViewById(R.id.genderSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.gender_list, android.R.layout.simple_list_item_1);
        genderSpinner.setAdapter(adapter);

        peopleCountEdittext = root.findViewById(R.id.people_count_edittext);
        reqEdittext = root.findViewById(R.id.requirements_edittext);
        availEdittext = root.findViewById(R.id.availability_editText);
        return root;
    }

    @Override
    public int getProgress() {
        return 100;
    }

    @Override
    public boolean validFilled() {
        return true;
    }

    @Override
    public CreateOfferBaseFragment nextFragment() {
        return null;
    }

    @Override
    public Offer fillOffer(@NonNull Offer offer) {
        if (peopleCountEdittext.getText().length() > 0) {
            offer.setPeopleCount(Integer.parseInt(peopleCountEdittext.getText().toString()));
        }
        if (reqEdittext.getText().toString().trim().length() > 0) {
            offer.setRequirements(reqEdittext.getText().toString().trim());
        }
        if (availEdittext.getText().toString().trim().length() > 0) {
            offer.setAvailability(availEdittext.getText().toString().trim());
        }
        offer.setGender(genderSpinner.getSelectedItemPosition());
        return offer;
    }

    @Override
    public int getNextButtonTextResource() {
        return R.string.action_finish;
    }
}
