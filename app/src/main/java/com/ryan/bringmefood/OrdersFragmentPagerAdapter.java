package com.ryan.bringmefood;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class OrdersFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 2;

    private static LinearLayout.LayoutParams matchWrap = new
            LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    private static final DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");

    private final Context theC;
    private final SharedPreferences thePrefs;
    private final SharedPreferences.Editor theEd;
    private final DisplayMetrics theMetrics;
    private final int DP_72, DP_16, DP_8;
    private final int SP_16, SP_14;

    public final MyOrders myOrdersFragment;
    public final NewOrder newOrderFragment;

    public OrdersFragmentPagerAdapter(final FragmentManager fragmentManager, final Context theC) {
        super(fragmentManager);
        this.theC = theC;
        this.thePrefs = theC.getSharedPreferences("com.ryan.bringmefood", Context.MODE_PRIVATE);
        this.theEd = thePrefs.edit();

        this.theMetrics = theC.getResources().getDisplayMetrics();
        this.DP_72 = getDP(72);
        this.DP_16 = getDP(16);
        this.DP_8 = getDP(8);
        this.SP_16 = getSP(16);
        this.SP_14 = getSP(14);

        this.myOrdersFragment = new MyOrders();
        this.newOrderFragment = new NewOrder();
    }

    private int getSP(final int theNum) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, theNum, theMetrics);
    }
    private int getDP(final int theNum) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, theNum, theMetrics);
    }

    public class MyOrders extends Fragment {

        private final HttpClient httpclient = new DefaultHttpClient();
        private final LinearLayout.LayoutParams theLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

        private final ArrayList<Order> allOrders = new ArrayList<Order>();

        private LinearLayout theLL;
        private SQLiteOrdersDatabase theDB;
        private UpdateOrderDB updateOrderDBRunnable;

        @Override
        public View onCreateView(LayoutInflater theLI, ViewGroup container, Bundle savedinstance) {
            final View rootInflater = theLI.inflate(R.layout.myorders_layout, container, false);

            theLL = (LinearLayout) rootInflater.findViewById(R.id.theLinearLayout);
            theDB = new SQLiteOrdersDatabase(theC);
            allOrders.addAll(theDB.getAllOrders());

            addOrdersToLayout();
            return rootInflater;
        }

        public void addOrdersToLayout() {

            if (updateOrderDBRunnable != null) {
                updateOrderDBRunnable.stop();
            }

            theLL.removeAllViews();
            for(Order order : allOrders) {
                theLL.addView(getView(order, true));
            }
            updateOrderDBRunnable = new UpdateOrderDB();
            new Thread(updateOrderDBRunnable).start();
        }

        private class UpdateOrderDB implements Runnable {

            private boolean toContinue = true;

            public void stop() {
                toContinue = false;
            }

            @Override
            public void run() {
                int counter = 0;
                final Iterator<Order> theIterator = allOrders.iterator();

                HttpPost httpPost;
                HttpResponse httpResponse;

                while (theIterator.hasNext() && toContinue) {
                    final Order theOrder = theIterator.next();
                    try {
                        httpPost = new HttpPost(theOrder.getUpdateOrderHttpPost());
                        httpResponse = httpclient.execute(httpPost);
                        final String theResponse = EntityUtils.toString(httpResponse.getEntity());
                        log("ORDER RESPONSE: " + theResponse);

                        final String status = theResponse.substring(0, theResponse.indexOf("|"));
                        final String deliveryTime = theResponse.substring(theResponse.indexOf("||") + 2);
                        theOrder.setEstimatedDeliveryTime(deliveryTime);
                        theOrder.setOrderStatus(status);

                    } catch (Exception e) {
                        log(e.toString());
                    }
                    new UpdateViewAndDatabaseTask(counter, theOrder).execute();
                    counter++;
                }
            }
        }

        private class UpdateViewAndDatabaseTask extends AsyncTask<Void, Void, View> {
            private final int counter;
            private final Order theOrder;

            public UpdateViewAndDatabaseTask(final int counter, final Order theOrder) {
                this.counter = counter;
                this.theOrder = theOrder;
            }

            @Override
            public View doInBackground(Void... params) {
                return getView(theOrder, false);
            }

            @Override
            public void onPostExecute(final View theView) {
                try {
                    theLL.removeViewAt(counter);
                } catch (Exception e) {
                    log(e.toString());
                }

                try {
                    theLL.addView(theView, counter);
                } catch (Exception e) {
                    log(e.toString());
                }
                theDB.updateOrder(theOrder);
            }
        }

        public View getView(final Order theOrder, final boolean isBeingUpdated) {

            final LinearLayout encompassingLV = new LinearLayout(theC);
            final TextView restaurantTV = new TextView(theC);
            final TextView statusTV = new TextView(theC);

            final MyOrderLongClickListener theLong = new MyOrderLongClickListener(theOrder);
            final MyOrderShortClickListener theShort = new MyOrderShortClickListener(theOrder);

            encompassingLV.setLayoutParams(theLayoutParams);
            encompassingLV.setOrientation(LinearLayout.VERTICAL);
            encompassingLV.setMinimumHeight(DP_72);
            encompassingLV.setPadding(DP_16, DP_8, 0, DP_8);

            restaurantTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            statusTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

            restaurantTV.setLayoutParams(matchWrap);
            statusTV.setLayoutParams(matchWrap);

            restaurantTV.setText(theOrder.getRestaurantName());

            if (isBeingUpdated) {
                statusTV.setText("Updating...");
            } else {
                final String rawStatus = theOrder.getRawStatus();
                if(rawStatus.contains("0")) {
                    statusTV.setText("Unclaimed");
                }
                else if(rawStatus.contains("1")) {
                    statusTV.setText("Claimed: " + theOrder.getDeliveryTime() + " minutes");
                }
                else if(rawStatus.contains("2")) {
                    statusTV.setText("En route to address: " + theOrder.getDeliveryTime() + " minutes");
                }
                else if(rawStatus.contains("3")) {
                    statusTV.setText("Delivered");
                }
                else {
                    statusTV.setText("Unclaimed: " + theOrder.getDeliveryTime() + " minutes");
                }
            }

            restaurantTV.setTextColor(Color.BLACK);
            //restaurantTV.setTextColor(getResources().getColor(R.color.primary300));
            //statusTV.setTextColor(getResources().getColor(R.color.primary500));

            restaurantTV.setOnLongClickListener(theLong);
            statusTV.setOnLongClickListener(theLong);

            restaurantTV.setOnClickListener(theShort);
            statusTV.setOnClickListener(theShort);

            encompassingLV.addView(restaurantTV);
            encompassingLV.addView(statusTV);

            return encompassingLV;
        }

        private class MyOrderShortClickListener implements View.OnClickListener {
            private final Order theOrder;

            public MyOrderShortClickListener(final Order theOrder) {
                this.theOrder = theOrder;
            }

            @Override
            public void onClick(View v) {
                final Intent toViewOrder = new Intent(getActivity(), VieworderActivity.class);
                toViewOrder.putExtra("order", theOrder.toJSONObject().toString());
                startActivity(toViewOrder);
            }
        }

        private class MyOrderLongClickListener implements View.OnLongClickListener {
            private final Order theOrder;

            public MyOrderLongClickListener(final Order theOrder) {
                this.theOrder = theOrder;
            }

            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder deleteItem = new AlertDialog.Builder(getActivity());
                deleteItem.setTitle("Delete " + theOrder.getRestaurantName() + " order");
                deleteItem.setMessage("Delete your order for " + theOrder.getRestaurantName() +
                        " on " + theOrder.getDateForm());

                deleteItem.setPositiveButton("Delete order", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(theOrder.getStatus().contains("Unclaimed") || theOrder.getStatus().contains("Delivered") ||
                                theOrder.getRawStatus().equals("0")) {
                            new Thread(new SendToServer(theOrder.getOrderDeleteHttpPost())).start();
                            theDB.deleteOrder(theOrder.getIdNumber());
                            allOrders.clear();
                            allOrders.addAll(theDB.getAllOrders());
                            theLL.removeAllViews();
                            addOrdersToLayout();
                        }
                        else {
                            makeToast("Sorry, you can't delete a live order");
                        }

                    }
                });

                deleteItem.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                final AlertDialog theDialog = deleteItem.create();
                theDialog.getWindow().getAttributes().windowAnimations = com.ryan.bringmefood.R.style.DialogSlideAnim;
                theDialog.show();
                return false;
            }
        }

        private void makeToast(final String message) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
    }


    /**
     *
     *
     *
     * NEW ORDER FRAGMENT
     *
     *
     *
     */


    public class NewOrder extends Fragment {

        private final String[] allRestaurants = {"Cheeburger Cheeburger",
                            "Chucks", "Contes", "Georges", "Hoagie Haven",
                            "Olives", "Slice Between", "Soup Co.", "Subway",
                            "Taste of Mexico", "Teresas", "Tortugas"};

        private final LinkedList<MenuItem> theItems = new LinkedList<MenuItem>();

        private View rootInflater;
        private LinearLayout itemsLayout;
        private TextView addItem, addItemFromMenu;
        private EditText myNameET, myPhoneET, myAddressET, orderCostET;
        private AutoCompleteTextView restaurantNameET;
        private Button submit;
        private Activity theActivity;

        private void initializeVariables() {
            this.theActivity = getActivity();
            itemsLayout = (LinearLayout) rootInflater.findViewById(R.id.itemsLinearLayout);
            addItem = (TextView) rootInflater.findViewById(R.id.addItemTV);
            addItemFromMenu = (TextView) rootInflater.findViewById(R.id.addItemMenu);
            submit = (Button) rootInflater.findViewById(R.id.submitButton);
            myNameET = (EditText) rootInflater.findViewById(R.id.myName);
            myPhoneET = (EditText) rootInflater.findViewById(R.id.myPhoneNumber);
            myPhoneET.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
            myAddressET = (EditText) rootInflater.findViewById(R.id.myAddress);
            restaurantNameET = (AutoCompleteTextView) rootInflater.findViewById(R.id.restaurantName);
            orderCostET = (EditText) rootInflater.findViewById(R.id.cost);

            //Set up restaurant autocomplete
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (getActivity().getApplicationContext(), R.layout.restaurant_items_textview,
                            Arrays.asList(allRestaurants));
            restaurantNameET.setAdapter(adapter);
            restaurantNameET.setThreshold(-1);
            restaurantNameET.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        restaurantNameET.showDropDown();
                    }
                }
            });
            restaurantNameET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if(s.length() == 0) {
                        restaurantNameET.showDropDown();
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.length() == 0) {
                        restaurantNameET.showDropDown();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(s.length() == 0) {
                        restaurantNameET.showDropDown();
                    }
                }
            });
            orderCostET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    final String theString = s.toString();
                    try {
                        if (theString.charAt(0) != '$') {
                            orderCostET.setText("$" + theString.replace("$", ""));
                        }
                        orderCostET.setSelection(orderCostET.getText().length());
                    } catch (Exception e) {
                    }
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {
                }
            });


            //Personal variables
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

            //Add item from menu
            addItemFromMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String restaurant = restaurantNameET.getText().toString();
                    boolean isFromList = false;

                    for(int i = 0; i < allRestaurants.length; i++) {
                        if (allRestaurants[i].equals(restaurant)) {
                            isFromList = true;
                            i = Integer.MAX_VALUE;
                            break;
                        }
                    }

                    if(restaurant.length() <= 3 || !isFromList) {
                        makeToast("Must select a restaurant from list for this feature");
                        return;
                    }

                    final AlertDialog.Builder theAlert = new AlertDialog.Builder(getActivity());
                    final MenuItem[] theMenu = getMenu(restaurant);

                    final MenuListViewAdapter theAdapter = new
                            MenuListViewAdapter(theC, R.layout.menu_listview_item, theMenu, theItems);

                    final ListView listView = new ListView(theC);
                    listView.setAdapter(theAdapter);

                    //AutoComplete for adding items from menu
                    final AutoCompleteTextView autoMenuNames = (AutoCompleteTextView)
                            (((LayoutInflater) getActivity()
                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                                    .inflate(R.layout.autocomplete, null))
                                    .findViewById(R.id.itemNameACTV);
                    ((ViewGroup) autoMenuNames.getParent()).removeView(autoMenuNames);

                    final String[] menuItems = new String[theMenu.length];
                    for(int i = 0; i < theMenu.length; i++) {
                        menuItems[i] = theMenu[i].getName();
                    }

                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                            (getActivity().getApplicationContext(),
                                    R.layout.restaurant_items_textview, menuItems);

                    autoMenuNames.setAdapter(adapter);
                    autoMenuNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            final String text = ((TextView) view).getText().toString();

                            for(int i = 0; i < theMenu.length; i++) {
                                if (theMenu[i].getName().contains(text)) {
                                    position = i;
                                }
                            }

                            final MenuItem item = theMenu[position];
                            final EditText toEdit = new EditText(theC);
                            final AlertDialog.Builder itemDescription = new AlertDialog.Builder(theActivity);

                            if(item.getDescription().length() <= 2) {
                                toEdit.setHint("No description available");
                            }
                            else {
                                toEdit.setText(item.getDescription());
                            }
                            toEdit.setBackgroundColor(Color.WHITE);
                            toEdit.setTextColor(Color.BLACK);

                            itemDescription.setView(toEdit);
                            itemDescription.setTitle("Item description");
                            itemDescription.setMessage(item.getName());

                            itemDescription.setPositiveButton("Add to order", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final MenuItem newItem = MenuItem.deepClone(item);
                                    newItem.setDescription(toEdit.getText().toString());
                                    theItems.add(newItem);
                                    itemsLayout.addView(getItemView(newItem), 0);

                                    try {
                                        try {
                                            final double currentPrice =
                                                    Double.parseDouble(orderCostET.getText().toString().replace("$", ""));
                                            orderCostET.setText((String.valueOf(currentPrice +
                                                    Double.parseDouble(item.getCost()))));
                                        }
                                        catch (Exception e) {
                                            orderCostET.setText(item.getCost());
                                        }
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            itemDescription.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            itemDescription.show();
                        }
                    });

                    autoMenuNames.setThreshold(-1);
                    autoMenuNames.setOnFocusChangeListener(new OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
                                restaurantNameET.showDropDown();
                            }
                        }
                    });

                    final LinearLayout items = new LinearLayout(theC);
                    items.setOrientation(LinearLayout.VERTICAL);
                    items.addView(autoMenuNames);
                    items.addView(listView);

                    theAlert.setView(items);

                    theAlert.setPositiveButton("Finished choosing from menu",
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    theAlert.show();
                }
            });
        }

        @Override
        public View onCreateView(LayoutInflater theLI, ViewGroup container, Bundle savedInstance) {
            this.rootInflater = theLI.inflate(R.layout.neworder_layout, container, false);

            initializeVariables();

            addItem.setOnClickListener(AddItemListener);
            submit.setOnClickListener(SubmitOrderListener);
            return rootInflater;
        }

        public TextView getItemView(final MenuItem anItem) {
            final TextView theView = new TextView(theC);
            theView.setText(anItem.getName() + " - " + anItem.getDescription());
            theView.setTextColor(getResources().getColor(R.color.primary700));
            theView.setTextSize(20);
            theView.setPadding(20, 20, 0, 0);

            theView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final AlertDialog.Builder editAD = new AlertDialog.Builder(getActivity());
                    editAD.setTitle("Edit");
                    editAD.setMessage("Edit item");

                    final EditText theItem = new EditText(theC);
                    theItem.setBackgroundColor(Color.WHITE);
                    theItem.setTextColor(Color.BLACK);
                    theItem.setText(anItem.getDescription());

                    editAD.setView(theItem);

                    editAD.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final TextView theV = (TextView) v;
                            anItem.setDescription(theItem.getText().toString());

                            theV.setText(anItem.getName() + " - " + anItem.getDescription());
                            theView.setText(anItem.getName() + " - " + anItem.getDescription());
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
                            theItems.remove(anItem);
                            updateChosenItems();
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

                final String myCost = "$" + decimalFormat.format(
                        Double.parseDouble(orderCostET.getText().toString().replace("$", "")));

                confirmSubmit.setTitle("Confirm Order");
                confirmSubmit.setMessage("Are you sure you want to submit this order for " +
                                            myCost + "?");
                confirmSubmit.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String myName = myNameET.getText().toString();
                        final String myAddress = myAddressET.getText().toString();
                        final String myPhone = myPhoneET.getText().toString();
                        final String restaurantName = restaurantNameET.getText().toString();

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

                        new SubmitOrderTask(theOrder).execute();
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

        private MenuItem[] getMenu(final String restaurantName) {
            try {
                final BufferedReader theReader = new BufferedReader(
                        new InputStreamReader(theC.getResources().getAssets().open(restaurantName + ".txt")));

                final StringBuilder menuString = new StringBuilder("");

                while(theReader.ready()) {
                    menuString.append(theReader.readLine());
                }

                final JSONObject wholeMenu = new JSONObject(menuString.toString());

                final JSONArray wholeArray = wholeMenu.getJSONArray("menu");
                final MenuItem[] allItems = new MenuItem[wholeArray.length()];

                for(int i = 0; i < wholeArray.length(); i++) {
                    final JSONObject theObj = wholeArray.getJSONObject(i);
                    allItems[i] = new MenuItem(theObj.getString("name"),
                            theObj.getString("price"),
                            theObj.getString("description"));
                }
                theReader.close();
                return allItems;
            }
            catch (Exception e) {
                log("Error here: " + restaurantName);
                return new MenuItem[]{new MenuItem("Error", "", e.toString())};
            }
        }

        private void updateChosenItems() {
            itemsLayout.removeAllViews();
            for(MenuItem item : theItems) {
                itemsLayout.addView(getItemView(item));
            }
        }

        private class SubmitOrderTask extends AsyncTask<Void, Void, Boolean> {
            private final Order theOrder;
            private final ProgressDialog theDialog;

            public SubmitOrderTask(final Order theOrder) {
                this.theOrder = theOrder;
                theDialog = ProgressDialog.show(getActivity(), "Please wait", "Submitting order to " +
                theOrder.getRestaurantName(), true);
                theDialog.setCancelable(false);
            }

            @Override
            public Boolean doInBackground(Void... params) {
                try {
                    final HttpClient httpclient = new DefaultHttpClient();
                    final HttpPost httppost = new HttpPost(theOrder.getOrderHttpPost());
                    final HttpResponse response = httpclient.execute(httppost);
                    final String response1 = EntityUtils.toString(response.getEntity());

                    if (response1.contains("ACK")) {
                        final SQLiteOrdersDatabase theDB = new SQLiteOrdersDatabase(theC);
                        theDB.addOrder(theOrder);
                        theDB.close();
                        log("Successfully ordered " + response1);
                        return true;
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public void onPostExecute(final Boolean status) {
                if(status) {
                    theDialog.setMessage("Order submitted");
                    theDialog.setTitle("Order submitted");
                    makeToast("Order successfully submitted");

                    final Intent viewOrder = new Intent(getActivity(), VieworderActivity.class);
                    viewOrder.putExtra("order", theOrder.toJSONObject().toString());
                    viewOrder.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(viewOrder);
                    getActivity().finish();
                }
                else {
                    makeToast("Sorry, something went wrong. Please submit again");
                }
                theDialog.dismiss();
            }
        }

        private void log(final String message) {
            Log.e("com.ryan.bringmefood", message);
        }

        private final View.OnClickListener AddItemListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder addItem = new AlertDialog.Builder(getActivity());
                final EditText item = new EditText(theC);
                item.setBackgroundColor(Color.WHITE);
                item.setTextColor(Color.BLACK);

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
                            final MenuItem newItem = new MenuItem("Not on Menu",
                                    "0.00", item.getText().toString());
                            theItems.add(newItem);
                            updateChosenItems();
                        }
                    }
                });
                addItem.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                final AlertDialog theDialog = addItem.create();
                theDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                theDialog.show();
            }
        };

        private void makeToast(final String message) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }

        private class MenuListViewAdapter extends ArrayAdapter<MenuItem> {
            private final LayoutInflater inflater;
            private final MenuItem[] theMenu;
            private final LinkedList<MenuItem> chosenItems;
            private final SparseBooleanArray mSelectedItemsIds;

            public MenuListViewAdapter(Context context, int resource,
                                       MenuItem[] objects, LinkedList<MenuItem> chosenItems) {
                super(context, resource, objects);
                this.inflater = LayoutInflater.from(context);
                this.theMenu = objects;
                this.chosenItems = chosenItems;
                this.mSelectedItemsIds = new SparseBooleanArray();
            }

            private class ViewHolder {
                TextView itemName;
                ImageView itemDescription;
                TextView itemCost;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                final ViewHolder holder;
                if(view == null) {
                    holder = new ViewHolder();
                    view = inflater.inflate(R.layout.menu_items_view, null);

                    holder.itemName = (TextView) view.findViewById(R.id.itemNameTextView);
                    holder.itemDescription = (ImageView) view.findViewById(R.id.informationButton);
                    holder.itemCost = (TextView) view.findViewById(R.id.costTextView);

                    final MenuItem item = theMenu[position];
                    try {
                        final double cost = Double.parseDouble(item.getCost().replace(" ", "").replace("$", ""));
                        holder.itemCost.setText("$" + decimalFormat.format(cost).replace(" ", ""));
                        holder.itemCost.setGravity(Gravity.RIGHT);
                    }
                    catch (Exception e) {
                        holder.itemCost.setText("$: " + item.getCost());
                    }
                    holder.itemName.setText(item.getName());
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final EditText toEdit = new EditText(theC);

                            if(item.getDescription().length() <= 2) {
                                toEdit.setHint("No description available");
                            }
                            else {
                                toEdit.setText(item.getDescription());
                            }
                            toEdit.setBackgroundColor(Color.WHITE);
                            toEdit.setTextColor(Color.BLACK);

                            final AlertDialog.Builder itemDescription = new AlertDialog.Builder(theActivity);
                            itemDescription.setView(toEdit);
                            itemDescription.setTitle("Item description");
                            itemDescription.setMessage(item.getName());

                            itemDescription.setPositiveButton("Add to order", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final MenuItem newItem = MenuItem.deepClone(item);
                                    newItem.setDescription(toEdit.getText().toString());
                                    chosenItems.add(newItem);
                                    updateChosenItems();

                                    try {
                                        try {
                                            final double currentPrice =
                                                    Double.parseDouble(orderCostET.getText().toString().replace("$", ""));
                                            orderCostET.setText((String.valueOf(currentPrice +
                                                    Double.parseDouble(item.getCost()))));
                                        }
                                        catch (Exception e) {
                                            orderCostET.setText(item.getCost());
                                        }
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            itemDescription.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            itemDescription.show();

                        }
                    });
                }
                else {
                    holder = (ViewHolder) view.getTag();
                }
                return view;
            }

            public void toggleSelection(int position) {
                selectView(position, !mSelectedItemsIds.get(position));
            }

            public void selectView(int position, boolean value) {
                if (value)
                    mSelectedItemsIds.put(position, value);
                else
                    mSelectedItemsIds.delete(position);
                notifyDataSetChanged();
            }

            public int getSelectedCount() {
                return mSelectedItemsIds.size();
            }

            public SparseBooleanArray getSelectedIds() {
                return mSelectedItemsIds;
            }
        }
    }

    private class SendToServer implements Runnable {
        private final String sendMessage;

        public SendToServer(final String message) {
            this.sendMessage = message;
        }

        @Override
        public void run() {
            try {
                final HttpClient httpclient = new DefaultHttpClient();
                final HttpPost httppost = new HttpPost(sendMessage);
                final HttpResponse response = httpclient.execute(httppost);
                final String response1 = EntityUtils.toString(response.getEntity());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
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

    private void log(final String message) {
        Log.e("com.ryan.bringmefood", message);
    }

    @Override
    public Fragment getItem(int position) {
        final Bundle data = new Bundle();
        data.putInt("current_page", position + 1);

        switch (position) {
            case 0:
                myOrdersFragment.setArguments(data);
                return myOrdersFragment;
            case 1:
                newOrderFragment.setArguments(data);
                return newOrderFragment;
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