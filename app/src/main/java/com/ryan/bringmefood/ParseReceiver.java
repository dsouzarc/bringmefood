package com.ryan.bringmefood;


import android.app.Application;
import android.content.Context;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class ParseReceiver extends Application {

    private static ParseReceiver instance = new ParseReceiver();

    public ParseReceiver() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "AdzOc2Rwa3OHorbxpc8mv698qtl8e7dg2XSjscqO",
                "JY1hNJ9EgxcZxFUp1VlLhqV4ZYd7Azf1H4tQTBQX");
        PushService.setDefaultPushCallback(this, MainOrdersActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
