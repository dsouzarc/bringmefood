package com.ryan.bringmefood;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
/**
 * Created by Ryan on 8/30/14.
 */
public class Constant {

    public static void log(final String message) {
        Log.e("com.ryan.bringmefood", message);
    }

    public static void makeToast(final Activity theActivity, final String message) {
        Toast.makeText(theActivity, message, Toast.LENGTH_LONG).show();
    }
}
