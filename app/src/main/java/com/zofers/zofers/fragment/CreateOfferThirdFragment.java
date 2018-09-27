package com.zofers.zofers.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zofers.zofers.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateOfferThirdFragment extends CreateOfferBaseFragment {


    public CreateOfferThirdFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_offer_third, container, false);
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
    public int getNextButtonTextResource() {
        return R.string.action_finish;
    }
}
