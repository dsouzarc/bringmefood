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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

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
            final ArrayList<Order> allOrders = removeAllDuplicates(theDB.getAllOrders());

            for(Order theOrder : )

            return rootInflater;
        }
    };

    public TextView getView(final String text) {
        final TextView theView = new TextView(theC);
        theView.setText(text);
        theView.setTextColor(Color.BLACK);
        theView.setTextSize(20);
        theView.setPadding(20, 20, 0, 0);
        return theView;
    }

    public class NewOrder extends Fragment {
        @Override
        public View onCreateView(LayoutInflater theLI, ViewGroup container, Bundle savedInstance) {
            final View rootInflater = theLI.inflate(R.layout.neworder_layout, container, false);
            return rootInflater;
        }
    }

    public ArrayList<Order> removeAllDuplicates(final List<Order> theOrders) {
        final SortedSet<Order> theSet = new TreeSet<Order>(new Comparator<Order>() {
            @Override
            public int compare(Order lhs, Order rhs) {
                return lhs.hashCode() - rhs.hashCode();
            }
        });
        return new ArrayList<Order>(theSet);
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
