package com.ryan.bringmefood;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class VieworderActivity extends Activity {

    private final Context theC = this;

    private TextView restaurantName;
    private TextView orderStatus;
    private TextView myName;
    private TextView myPhone;
    private TextView myAddress;
    private TextView myCost;

    private TextView driverDetails;
    private TextView eta;

    private LinearLayout itemsLinearLayout;
    private LinearLayout orderStatusLinearLayout;

    private Order theOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vieworder);

        initializeVariables();

        restaurantName.setText("Restaurant: " + theOrder.getRestaurantName());
        orderStatus.setText("Status: " + theOrder.getStatus());
        myName.setText("My name: " + theOrder.getMyName());
        myPhone.setText("My phone: " + theOrder.getMyNumber());
        myAddress.setText("Delivery address: " + theOrder.getMyAddress());
        myCost.setText("Estimated Cost: " + theOrder.getOrderCost());

        addEtaDetails();

        final String[] items = theOrder.getMyOrder();
        for(String item : items) {
            itemsLinearLayout.addView(getItemTV(item));
        }
    }

    private void addEtaDetails() {
        if(theOrder.getStatus() == Order.STATUS.CLAIMED ||
                theOrder.getStatus() == Order.STATUS.EN_ROUTE ||
                theOrder.getStatus() == Order.STATUS.FOOD_ORDERED) {
            if(eta != null && driverDetails != null) {
                orderStatusLinearLayout.removeView(eta);
                orderStatusLinearLayout.removeView(driverDetails);
            }

            final String deliveryTime = theOrder.getDeliveryTime().contains("N/A") ?
                    "N/A" : theOrder.getDeliveryTime() + " minutes";

            eta = getTextView("ETA From Claim: " + deliveryTime);
            driverDetails = getTextView("Driver details");

            try {
                orderStatusLinearLayout.addView(eta, 1);
                orderStatusLinearLayout.addView(driverDetails, 2);
            }
            catch (Exception e) {
                orderStatusLinearLayout.addView(eta);
                orderStatusLinearLayout.addView(driverDetails);
            }
        }
    }

    private TextView getTextView(final String text) {
        final TextView theView = new TextView(theC);
        theView.setText(text);
        theView.setTextAppearance(theC, R.style.Order_Items_TextView);
        return theView;
    }

    private TextView getItemTV(final String item) {
        final TextView theView = new TextView(theC);
        theView.setText(item);
        theView.setTextAppearance(theC, R.style.Order_Items_TextView);
        theView.setPadding(16, 24, 0, 0);
        theView.setTextColor(getResources().getColor(R.color.primary500));
        return theView;
    }

    private void initializeVariables() {
        this.theOrder = Order.getOrder(getIntent().getExtras().getString("order"));
        this.itemsLinearLayout = (LinearLayout) findViewById(R.id.itemsLinearLayout);
        this.orderStatusLinearLayout = (LinearLayout) findViewById(R.id.orderDetailsLL);
        this.restaurantName = (TextView) findViewById(R.id.restaurantNameTV);
        this.orderStatus = (TextView) findViewById(R.id.orderStatusTV);
        this.myName = (TextView) findViewById(R.id.myNameTV);
        this.myPhone = (TextView) findViewById(R.id.myPhoneTV);
        this.myAddress = (TextView) findViewById(R.id.myAddressTV);
        this.myCost = (TextView) findViewById(R.id.orderCostTV);
    }

    private void makeToast(final String message) {
        Toast.makeText(theC, message, Toast.LENGTH_LONG);
    }

    private class UpdateOrderDetails extends AsyncTask<Void, Void, Void> {

        @Override
        public Void doInBackground(Void... params) {
            final HttpClient theClient = new DefaultHttpClient();
            final HttpPost post = new HttpPost(theOrder.getUpdateOrderHttpPost());

            try {
                final HttpResponse response = theClient.execute(post);
                final String theResponse = EntityUtils.toString(response.getEntity());
                log("Order Detail Response: " + theResponse);
                final String status = theResponse.substring(0, theResponse.indexOf("|"));
                final String deliveryTime = theResponse.substring(theResponse.indexOf("||") + 2);
                theOrder.setEstimatedDeliveryTime(deliveryTime);
                theOrder.setOrderStatus(Order.getStatus(status));
            }
            catch (Exception e) {
                e.printStackTrace();
                makeToast("Sorry, something went wrong");
            }
            return null;
        }

        @Override
        public void onPostExecute(Void param) {
            orderStatus.setText("Status: " + theOrder.getStatus());
            addEtaDetails();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(VieworderActivity.this, MainOrdersActivity.class));
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vieworder, menu);
        return true;
    }

    private void log(final String message) {
        Log.e("com.ryan.bringmefood", message);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        switch (id) {
            case R.id.refreshItem :
                orderStatus.setText("Updating...");
                new UpdateOrderDetails().execute();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
