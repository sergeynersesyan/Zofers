package com.zofers.zofers.create;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.zofers.zofers.BaseActivity;
import com.zofers.zofers.R;
import com.zofers.zofers.callback.PermissionRequestCallback;
import com.zofers.zofers.model.Offer;
import com.zofers.zofers.staff.FileHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateOfferSecoundFragment extends CreateOfferBaseFragment implements View.OnClickListener {

    private static final int REQUEST_SELECT_PICTURE = 1000;
    private SimpleDraweeView image;
    private EditText nameEdittext;
    private TextInputLayout nameTIL;
    private EditText descriptionEdittext;
    private TextInputLayout descriptionTIL;
    private View root;
    private Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_create_offer_secound, container, false);
        image = root.findViewById(R.id.image);
        nameEdittext = root.findViewById(R.id.title_editText);
        descriptionEdittext = root.findViewById(R.id.description_textView);
        nameTIL = root.findViewById(R.id.title_TIL);
        descriptionTIL = root.findViewById(R.id.description_TIL);
        image.setOnClickListener(this);
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_SELECT_PICTURE) {
                if (data != null) {
                    image.setImageURI(data.getData());
                    imageUri = data.getData();
                }
            }
        }
    }

    @Override
    public int getProgress() {
        return 66;
    }

    @Override
    public boolean validFilled() {
        boolean validFilled = true;
        if (nameEdittext.getText().toString().trim().length() == 0) {
            nameTIL.setError(" ");
            validFilled = false;
        }
        if (descriptionEdittext.getText().toString().trim().length() == 0) {
            descriptionTIL.setError(" ");
            validFilled = false;
        }
        if (imageUri == null) {
            Snackbar.make(root,"Please select image", Snackbar.LENGTH_SHORT).show();
            validFilled = false;
        }

        if (validFilled) {
            byte[] binaryImage = FileHelper.getImageBinary(getContext(), imageUri);
            if (binaryImage == null) {
                validFilled = false;
            } else {
                getActivity().getIntent().putExtra(CreateOfferActivity.EXTRA_IMAGE_URI, imageUri);
            }
        }
        return validFilled;
    }

    @Override
    public CreateOfferBaseFragment nextFragment() {
        return new CreateOfferThirdFragment();
    }

    @Override
    public Offer fillOffer(@NonNull Offer offer) {
        offer.setName(nameEdittext.getText().toString().trim());
        offer.setDescription(descriptionEdittext.getText().toString().trim());
        return offer;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image:
                openGallery();
                break;
        }
    }

    private void openGallery() {
        ((BaseActivity)getActivity()).openGallery(root, REQUEST_SELECT_PICTURE, this);
    }
}
