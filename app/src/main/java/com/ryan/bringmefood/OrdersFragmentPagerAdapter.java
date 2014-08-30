package com.ryan.bringmefood;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.app.ProgressDialog;
import android.widget.Toast;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.util.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import android.util.Log;
import org.apache.http.impl.*;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.net.Uri;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class OrdersFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 2;
    private final Context theC;
    private final SharedPreferences thePrefs;
    private final SharedPreferences.Editor theEd;

    public OrdersFragmentPagerAdapter(final FragmentManager fragmentManager, final Context theC) {
        super(fragmentManager);
        this.theC = theC;
        this.thePrefs = theC.getSharedPreferences("com.ryan.bringmefood", Context.MODE_PRIVATE);
        this.theEd = thePrefs.edit();
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
        private LinearLayout itemsLayout;
        private TextView addItem;
        private EditText myNameET, myPhoneET, myAddressET, restaurantNameET, orderCostET;
        private Button submit;

        private void initializeVariables() {
            itemsLayout = (LinearLayout) rootInflater.findViewById(R.id.itemsLinearLayout);
            addItem = (TextView) rootInflater.findViewById(R.id.addItemTV);
            submit = (Button) rootInflater.findViewById(R.id.submitButton);

            myNameET = (EditText) rootInflater.findViewById(R.id.myName);
            myPhoneET = (EditText) rootInflater.findViewById(R.id.myPhoneNumber);
            myAddressET = (EditText) rootInflater.findViewById(R.id.myAddress);
            restaurantNameET = (EditText) rootInflater.findViewById(R.id.restaurantName);
            orderCostET = (EditText) rootInflater.findViewById(R.id.cost);

            String data = getPreferences("myName");
            if(data.length() > 2) {
                myNameET.setText(data);
            }

            data = getPreferences("myPhone");
            myPhoneET.setText((data.length() > 2) ? data : getPhoneNumber(theC));

            data = getPreferences("myAddress");
            if(data.length() > 2) {
                myAddressET.setText(data);
            }
        }

        @Override
        public View onCreateView(LayoutInflater theLI, ViewGroup container, Bundle savedInstance) {
            this.rootInflater = theLI.inflate(R.layout.neworder_layout, container, false);

            initializeVariables();

            addItem.setOnClickListener(AddItemListener);
            submit.setOnClickListener(SubmitOrderListener);
            return rootInflater;
        }

        public TextView getItemView(final String text) {
            final TextView theView = new TextView(theC);
            theView.setText(text);
            theView.setTextColor(Color.BLACK);
            theView.setTextSize(20);
            theView.setPadding(20, 20, 0, 0);

            theView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final AlertDialog.Builder editAD = new AlertDialog.Builder(getActivity());
                    editAD.setTitle("Edit");
                    editAD.setMessage("Edit item");

                    final EditText theItem = new EditText(theC);
                    theItem.setText(text);

                    editAD.setView(theItem);

                    editAD.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final TextView theV = (TextView) v;
                            final String newItem = theItem.getText().toString();
                            final String oldItem = theV.getText().toString();
                            for(int i = 0; i < theItems.size(); i++) {
                                if (theItems.get(i).equals(oldItem)) {
                                    theItems.set(i, newItem);
                                    i = Integer.MAX_VALUE;
                                }
                            }

                            theV.setText(newItem);
                            theView.setText(newItem);
                        }
                    });
                    editAD.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    editAD.show();
                }
            });

            theView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final TextView theV = (TextView) v;
                    final String toBeDeleted = theV.getText().toString();

                    final AlertDialog.Builder deleteAD = new AlertDialog.Builder(getActivity());
                    deleteAD.setTitle("Delete item");
                    deleteAD.setMessage("Delete " + toBeDeleted + "?");

                    deleteAD.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Iterator<String> theIT = theItems.iterator();

                            while(theIT.hasNext() && theIT != null) {
                                if(theIT.next().equals(toBeDeleted)) {
                                    theIT.remove();
                                    theIT = null;
                                }
                            }

                            itemsLayout.removeAllViews();

                            theIT = theItems.iterator();

                            while(theIT.hasNext()) {
                                itemsLayout.addView(getItemView(theIT.next()));
                            }
                        }
                    });
                    deleteAD.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    deleteAD.show();

                    return false;
                }
            });

            return theView;
        }

        private final View.OnClickListener SubmitOrderListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(theItems.size() == 0) {
                    makeToast("Must have at least 1 item");
                    return;
                }

                final AlertDialog.Builder confirmSubmit = new AlertDialog.Builder(getActivity());

                confirmSubmit.setTitle("Confirm Order");
                confirmSubmit.setMessage("Are you sure you want to submit this order for $" +
                                            orderCostET.getText().toString() + "?");
                confirmSubmit.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String myName = myNameET.getText().toString();
                        final String myAddress = myAddressET.getText().toString();
                        final String myPhone = myPhoneET.getText().toString();
                        final String restaurantName = restaurantNameET.getText().toString();
                        final String myCost = orderCostET.getText().toString();
                        final String Order_ID = String.valueOf(String.valueOf(System.currentTimeMillis()).hashCode());
                        final String time = String.valueOf(System.currentTimeMillis());
                        final String[] order = theItems.toArray(new String[theItems.size()]);
                        final String UID = Secure.getString(theC.getContentResolver(), Secure.ANDROID_ID);

                        setPreference("myName", myName);
                        setPreference("myPhone", myPhone);
                        setPreference("myAddress", myAddress);
                        //Name, phone number, my address, restaurant address, UID, myOrder[], order ID, orderCost, time in millis, status

                        final Order theOrder = new Order(myName, myPhone, myAddress, restaurantName,
                                UID, order, Order_ID, myCost, time, "0");

                        final ProgressDialog submitOrderProgress = ProgressDialog.show(getActivity(),
                                "Please wait", "Submitting Order to " + restaurantName, true);
                        submitOrderProgress.setCancelable(true);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    final HttpClient httpclient = new DefaultHttpClient();
                                    final HttpPost httppost = new HttpPost(theOrder.getOrderHttpPost());
                                    final HttpResponse response = httpclient.execute(httppost);
                                    final String response1 = EntityUtils.toString(response.getEntity());

                                    if(response1.equals("ACK")) {
                                        makeToast("Order submitted");
                                        final SQLiteOrdersDatabase theDB = new SQLiteOrdersDatabase(theC);
                                        theDB.addOrder(theOrder);
                                        theDB.close();
                                    }

                                } catch (Exception e) {
                                    makeToast("Something went wrong. Please try resubmitting your order");
                                }
                                submitOrderProgress.dismiss();
                            }
                        }).start();
                    }
                });

                confirmSubmit.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                confirmSubmit.show();
            }
        };

        private final View.OnClickListener AddItemListener = new View.OnClickListener() {
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
                        final String itemText = item.getText().toString();

                        if(itemText.replace(" ", "").length() < 2) {
                            makeToast("Please enter a valid item");
                        }
                        else {
                            theItems.add(item.getText().toString());
                            itemsLayout.addView(getItemView(item.getText().toString()), 0);
                        }
                    }
                });
                addItem.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                addItem.show();

            }
        };

        private void makeToast(final String message) {
            Toast.makeText(theC, message, Toast.LENGTH_LONG);
        }
    }

    private String getPhoneNumber(final Context theC) {
        TelephonyManager tMgr = (TelephonyManager)theC.getSystemService(Context.TELEPHONY_SERVICE);
        return tMgr.getLine1Number();
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

    private String getPreferences(final String key) {
        return thePrefs.getString(key, "");
    }

    private void setPreference(final String key, final String value) {
        theEd.putString(key, value);
        theEd.apply();
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
