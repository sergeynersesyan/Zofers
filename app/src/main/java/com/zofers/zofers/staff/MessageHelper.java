package com.zofers.zofers.staff;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class MessageHelper {

    public static void showErrorToast(Context context, String text) {
        Toast.makeText(context, "something went wrong " + text, Toast.LENGTH_SHORT).show();
    }

    public static void showNoConnectionToast (Context context) {
        showErrorToast(context, "no connection");
    }
}
