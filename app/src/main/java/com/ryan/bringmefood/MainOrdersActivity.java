package com.ryan.bringmefood;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.Context;
import android.provider.Settings;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.app.AlertDialog;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;
import android.content.DialogInterface;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;

public class MainOrdersActivity extends FragmentActivity {

    private ActionBar theActionBar;
    private ViewPager theViewPager;
    private Context theC;
    private SharedPreferences thePrefs;
    private SharedPreferences.Editor theEd;
    private OrdersFragmentPagerAdapter ofPA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_orders);

        if(!haveNetworkConnection()) {
            AlertDialog.Builder theBuilder = new AlertDialog.Builder(MainOrdersActivity.this);
            theBuilder.setTitle("No Internet Connection Detected");
            theBuilder.setMessage("Sorry, you must have a working Internet Connection to use " +
                        "this app");
            theBuilder.setPositiveButton("Take me to Wifi Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    finish();
                }
            });
        }

        try {
            Parse.initialize(this, "AdzOc2Rwa3OHorbxpc8mv698qtl8e7dg2XSjscqO",
                    "JY1hNJ9EgxcZxFUp1VlLhqV4ZYd7Azf1H4tQTBQX");
            PushService.setDefaultPushCallback(this, MainOrdersActivity.class);
            ParseInstallation.getCurrentInstallation().saveInBackground();
        }
        catch (Exception e) {
        }

        theC = getApplicationContext();
        thePrefs = this.getSharedPreferences("com.ryan.bringmefood", Context.MODE_PRIVATE);
        theEd = thePrefs.edit();

        if(isFirstUse()) {
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put("UDID", getUDID());
            installation.saveInBackground();
        }

        theActionBar = getActionBar();
        getActionBar().setIcon(
                new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        theActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        theViewPager = (ViewPager) findViewById(R.id.theViewPager);
        final FragmentManager theManager = getSupportFragmentManager();

        final ViewPager.SimpleOnPageChangeListener thePageListener = new ViewPager.SimpleOnPageChangeListener() {

            int positionCurrent;
            boolean dontLoadList;

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                theActionBar.setSelectedNavigationItem(position);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 0) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            if (!dontLoadList) {
                            }
                        }
                    }, 06);
                }
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                positionCurrent = position;
                if (positionOffset == 0 && positionOffsetPixels == 0) {
                    dontLoadList = false;
                } else {
                    dontLoadList = true;
                }
            }
        };

        theViewPager.setOnPageChangeListener(thePageListener);

        ofPA = new OrdersFragmentPagerAdapter(theManager, getApplicationContext());
        theViewPager.setAdapter(ofPA);
        theActionBar.setDisplayShowTitleEnabled(true);

        Tab theTab = theActionBar.newTab().setText("My Orders").setTabListener(tabListener);
        theActionBar.addTab(theTab, 0);
        theTab = theActionBar.newTab().setText("New order").setTabListener(tabListener);
        theActionBar.addTab(theTab, 1);

    }

    private String getUDID() {
        return Secure.getString(getApplication().getApplicationContext().getContentResolver(),
                Secure.ANDROID_ID);
    }

    private String getPreferences(final String key) {
        return thePrefs.getString(key, "");
    }

    private boolean isFirstUse() {
        boolean isFirst = thePrefs.getBoolean("isFirst", true);
        theEd.putBoolean("isFirst", false);
        return isFirst;
    }

    private void setPreference(final String key, final String value) {
        theEd.putString(key, value);
        theEd.apply();
    }

    private String getPhoneNumber() {
        TelephonyManager tMgr = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        return tMgr.getLine1Number();
    }

    //Tab listener
    private final ActionBar.TabListener tabListener = new ActionBar.TabListener() {
        @Override
        public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            theViewPager.setCurrentItem(tab.getPosition());
            switch (tab.getPosition()) {
                case 0:
                    theActionBar.setTitle("My Orders");
                    break;
                case 1:
                    theActionBar.setTitle("New Order");
                    break;
                default:
                    theActionBar.setTitle("Bring Me Food");
                    break;
            }
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        }
    };

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public void log(final String message) {
        Log.e("com.ryan.bringmefood", message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_orders, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.refreshItem) {
            try {
                ofPA.myOrdersFragment.addOrdersToLayout();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        if(id == R.id.addOrderItem) {
            theViewPager.setCurrentItem(1, true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
