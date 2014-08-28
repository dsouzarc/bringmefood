package com.ryan.bringmefood;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainOrdersActivity extends FragmentActivity {

    private ActionBar theActionBar;
    private ViewPager theViewPager;
    private Context theC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_orders);

        theC = getApplicationContext();

        theActionBar = getActionBar();
        theActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        theViewPager = (ViewPager) findViewById(R.id.theViewPager);
        final FragmentManager theManager = getSupportFragmentManager();

        final ViewPager.SimpleOnPageChangeListener thePageListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                theActionBar.setSelectedNavigationItem(position);
            }

            int positionCurrent;
            boolean dontLoadList;

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

        final OrdersFragmentPagerAdapter ofPA = new OrdersFragmentPagerAdapter(theManager);
        theViewPager.setAdapter(ofPA);
        theActionBar.setDisplayShowTitleEnabled(true);

        Tab theTab = theActionBar.newTab().setText("My Orders").setTabListener(tabListener);
        //theTab.setCustomView()
        theActionBar.addTab(theTab, 0);

        theTab = theActionBar.newTab().setText("New order").setTabListener(tabListener);
        theActionBar.addTab(theTab, 1);

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
        public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
        }
    };

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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
