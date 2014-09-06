package com.ryan.bringmefood;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import android.os.AsyncTask;
import android.content.Context;

public class VieworderActivity extends Activity {

    private final Context theC = this;

    private TextView restaurantName;
    private TextView orderStatus;
    private TextView myName;
    private TextView myPhone;
    private TextView myAddress;
    private TextView myCost;

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
        myCost.setText("Estimated Cost: $" + theOrder.getOrderCost());

        final String rawStatus = theOrder.getRawStatus();
        if(rawStatus.contains("1") || rawStatus.contains("2")) {
            orderStatusLinearLayout.addView(getTextView("ETA From Claim: " +
                    theOrder.getDeliveryTime() + " minutes"));
            orderStatusLinearLayout.addView(getTextView("Driver details"));
        }

        final String[] items = theOrder.getMyOrder();
        for(String item : items) {
            itemsLinearLayout.addView(getItemTV(item));
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        switch (id) {
            case R.id.refreshItem :

                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
