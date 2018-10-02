package com.zofers.zofers.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zofers.zofers.R;
import com.zofers.zofers.model.Offer;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class CreateOfferBaseFragment extends BaseFragment {
    public abstract int getProgress();
    public abstract boolean validFilled();
    public abstract CreateOfferBaseFragment nextFragment();
    public abstract Offer fillOffer(Offer offer);
    public int getNextButtonTextResource() {
        return R.string.action_next;
    }
}
