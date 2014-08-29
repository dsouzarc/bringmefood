package com.ryan.bringmefood;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class OrdersFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 2;
    final Context theC;

    public OrdersFragmentPagerAdapter(final FragmentManager fragmentManager, final Context theC) {
        super(fragmentManager);
        this.theC = theC;
    }

    public class MyOrders extends Fragment {

        private final ArrayList<Order> allOrders = new ArrayList<Order>();

        private LinearLayout theLL;
        private SQLiteOrdersDatabase theDB;

        @Override
        public View onCreateView(LayoutInflater theLI, ViewGroup container, Bundle savedinstance) {
            final View rootInflater = theLI.inflate(R.layout.myorders_layout, container, false);

            theDB = new SQLiteOrdersDatabase(theC);

            theLL = (LinearLayout) rootInflater.findViewById(R.id.theLinearLayout);

            theDB = new SQLiteOrdersDatabase(theC);
            allOrders.addAll(theDB.getAllOrders()); //removeAllDuplicates(theDB.getAllOrders());

            addOrdersToLayout();
            return rootInflater;
        }

        public void addOrdersToLayout() {
            for(Order theOrder : allOrders) {
                theLL.addView(getView(theOrder));
            }
        }
        public TextView getView(final Order theOrder) {
            final TextView theView = new TextView(theC);
            theView.setText(theOrder.getDateForm() + " " + theOrder.getRestaurantName() + theOrder.getStatus());
            theView.setTextColor(Color.BLACK);
            theView.setTextSize(20);
            theView.setPadding(20, 20, 0, 0);

            theView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AlertDialog.Builder deleteItem = new AlertDialog.Builder(getActivity());
                    deleteItem.setTitle("Delete " + theOrder.getRestaurantName() + " order");
                    deleteItem.setMessage("Delete your order for " + theOrder.getRestaurantName() +
                    " on " + theOrder.getDateForm());

                    deleteItem.setPositiveButton("Delete order", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            theDB.deleteOrder(theOrder.getIdNumber());
                            allOrders.remove(theOrder);
                            theLL.removeAllViews();
                            addOrdersToLayout();

                        }
                    });

                    deleteItem.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    deleteItem.show();
                    return false;
                }
            });
            return theView;
        }
    };

    public class NewOrder extends Fragment {

        private final LinkedList<String> theItems = new LinkedList<String>();
        private View rootInflater;
        @Override
        public View onCreateView(LayoutInflater theLI, ViewGroup container, Bundle savedInstance) {
            final View rootInflater = theLI.inflate(R.layout.neworder_layout, container, false);

            this.rootInflater = rootInflater;
            final LinearLayout itemsLayout = (LinearLayout) rootInflater.findViewById(R.id.itemsLinearLayout);
            final TextView addItem = (TextView) rootInflater.findViewById(com.ryan.bringmefood.R.id.addItemTV);

            addItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder addItem = new AlertDialog.Builder(getActivity());
                    final EditText item = new EditText(theC);

                    addItem.setTitle("Add Item");
                    addItem.setMessage("Add new item to your order");
                    addItem.setView(item);

                    addItem.setPositiveButton("Add item", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            theItems.add(item.getText().toString());
                            itemsLayout.addView(getView(item.getText().toString()), 0);
                        }
                    });

                    addItem.show();

                }
            });

            final Button submit = (Button) rootInflater.findViewById(R.id.submitButton);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(theItems.size() == 0) {
                        theItems.add("ITEM");
                    }

                    final String myName = getEditText(com.ryan.bringmefood.R.id.myName);
                    final String myAddress = getEditText(com.ryan.bringmefood.R.id.myAddress);
                    final String myPhone = getEditText(com.ryan.bringmefood.R.id.myPhoneNumber);
                    final String restaurantName = getEditText(com.ryan.bringmefood.R.id.restaurantName);
                    final String myCost = getEditText(com.ryan.bringmefood.R.id.cost);
                    final String Order_ID = String.valueOf(String.valueOf(System.currentTimeMillis()).hashCode());
                    final String time = String.valueOf(System.currentTimeMillis());
                    final String[] order = theItems.toArray(new String[theItems.size()]);
                    final String UID = Secure.getString(theC.getContentResolver(), Secure.ANDROID_ID);

                    //Name, phone number, my address, restaurant address, UID, myOrder[], order ID, orderCost, time in millis, status

                    final Order theOrder = new Order(myName, myPhone, myAddress, restaurantName,
                            UID, order, Order_ID, myCost, time, "0");
                    SQLiteOrdersDatabase theDB = new SQLiteOrdersDatabase(theC);
                    theDB.addOrder(theOrder);
                    theDB.close();
                }
            });


            return rootInflater;
        }

        public String getEditText(final int resID) {
            try {
                final EditText theText = (EditText) rootInflater.findViewById(resID);
                return theText.getText().toString();
            }
            catch (Exception e) {
                return e.toString();
            }
        }

        public TextView getView(final String text) {
            final TextView theView = new TextView(theC);
            theView.setText(text);
            theView.setTextColor(Color.BLACK);
            theView.setTextSize(20);
            theView.setPadding(20, 20, 0, 0);
            return theView;
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
