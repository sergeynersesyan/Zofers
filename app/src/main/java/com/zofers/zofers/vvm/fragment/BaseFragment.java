package com.zofers.zofers.vvm.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.zofers.zofers.callback.PermissionRequestCallback;

/**
 * Created by Mr Nersesyan on 19/09/2018.
 */

public class BaseFragment extends Fragment {

    private static final int WRITE_STORAGE_PERMISSION_REQUEST_CODE = 1234;
    PermissionRequestCallback permissionCallback;

    public void promptExternalStoragePermissions(PermissionRequestCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionCallback = callback;
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_PERMISSION_REQUEST_CODE);
            } else {
                callback.onResponse(true);
            }
        } else {
            if (callback != null) {
                callback.onResponse(true);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_STORAGE_PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        permissionCallback.onResponse(true);
                    } else {
                        permissionCallback.onResponse(false);
                    }
                }
            }
        }
    }
}
