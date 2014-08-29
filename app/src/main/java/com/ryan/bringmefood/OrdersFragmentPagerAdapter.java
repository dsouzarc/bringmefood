package com.ryan.bringmefood;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OrdersFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 2;

    public OrdersFragmentPagerAdapter(final FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public class MyOrders extends Fragment {
        @Override
        public View onCreateView(LayoutInflater theLI, ViewGroup container, Bundle savedinstance) {
            final View rootInflater = theLI.inflate(R.layout.myorders_layout, container, false);
            return rootInflater;
        }
    };

    public class NewOrder extends Fragment {
        @Override
        public View onCreateView(LayoutInflater theLI, ViewGroup container, Bundle savedInstance) {
            final View rootInflater = theLI.inflate(R.layout.neworder_layout, container, false);
            return rootInflater;
        }
    }



    @Override
    public Fragment getItem(int position) {
        final Bundle data = new Bundle();
        data.putInt("current_page", position + 1);

        switch (position) {
            case 0:
                MyOrders myOrders = new MyOrders();
                myOrders.setArguments(data);
                return myOrders;
            case 1:
                NewOrder newOrder = new NewOrder();
                newOrder.setArguments(data);
                return newOrder;
            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
