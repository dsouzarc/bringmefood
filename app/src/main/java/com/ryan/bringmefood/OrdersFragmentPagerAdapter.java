package com.ryan.bringmefood;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.ScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;

public class OrdersFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 2;
    final Context theC;

    public OrdersFragmentPagerAdapter(final FragmentManager fragmentManager, final Context theC) {
        super(fragmentManager);
        this.theC = theC;
    }

    public class MyOrders extends Fragment {
        @Override
        public View onCreateView(LayoutInflater theLI, ViewGroup container, Bundle savedinstance) {
            final View rootInflater = theLI.inflate(R.layout.myorders_layout, container, false);

            final ScrollView theScroll = (ScrollView) rootInflater.findViewById(R.id.scrollViewAllOrders);

            final SQLiteOrdersDatabase theDB = new SQLiteOrdersDatabase(theC);



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
