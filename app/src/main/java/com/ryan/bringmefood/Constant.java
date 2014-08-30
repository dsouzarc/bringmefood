package com.ryan.bringmefood;
import android.util.Log;
import android.content.Context;
import android.widget.Toast;
/**
 * Created by Ryan on 8/30/14.
 */
public class Constant {

    public static void log(final String message) {
        Log.e("com.ryan.bringmefood", message);
    }

    public static void makeToast(final Context theC, final String message) {
        Toast.makeText(theC, message, Toast.LENGTH_LONG).show();
    }
}
