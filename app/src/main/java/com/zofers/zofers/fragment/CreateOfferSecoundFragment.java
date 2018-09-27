package com.zofers.zofers.fragment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zofers.zofers.R;
import com.zofers.zofers.callback.PermissionRequestCallback;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateOfferSecoundFragment extends CreateOfferBaseFragment implements View.OnClickListener {

    private static final int REQUEST_SELECT_PICTURE = 1015;
    private SimpleDraweeView image;
    private EditText nameEdittext;
    private EditText descriptionEdittext;
    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_create_offer_secound, container, false);
        image = root.findViewById(R.id.image);
        nameEdittext = root.findViewById(R.id.title_editText);
        descriptionEdittext = root.findViewById(R.id.description_editText);
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
        return true;
    }

    @Override
    public CreateOfferBaseFragment nextFragment() {
        return new CreateOfferThirdFragment();
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
        promptExternalStoragePermissions(new PermissionRequestCallback() {
            @Override
            public void onResponse(boolean granted) {
                if (getActivity() == null) return;
                if (granted) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(Intent.createChooser(intent, "Select picture"), REQUEST_SELECT_PICTURE);
                } else {
                    Snackbar snackbar = Snackbar.make(root, R.string.image_permission_denied, Snackbar.LENGTH_LONG);

                    snackbar.setAction("Open settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    });
                    snackbar.show();
                }
            }
        });
    }
}
