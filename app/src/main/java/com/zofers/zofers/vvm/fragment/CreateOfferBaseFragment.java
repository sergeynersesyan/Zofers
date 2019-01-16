package com.zofers.zofers.vvm.fragment;


import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.zofers.zofers.R;
import com.zofers.zofers.model.Offer;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class CreateOfferBaseFragment extends BaseFragment {
    public abstract int getProgress();
    public abstract boolean validFilled();
    public abstract CreateOfferBaseFragment nextFragment();
    public abstract Offer fillOffer(@NonNull Offer offer);
    public int getNextButtonTextResource() {
        return R.string.action_next;
    }
}
