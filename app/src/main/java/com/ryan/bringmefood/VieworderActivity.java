package com.ryan.bringmefood;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VieworderActivity extends Activity {

    private TextView restaurantName;
    private TextView orderStatus;
    private TextView driverDetails;
    private TextView myName;
    private TextView myPhone;
    private TextView myAddress;
    private TextView myCost;

    private LinearLayout itemsLinearLayout;

    private Order theOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vieworder);

        initializeVariables();

        restaurantName.setText(theOrder.getRestaurantName());


    }

    private void initializeVariables() {
        this.theOrder = Order.getOrder(getIntent().getExtras().getString("order"));
        this.itemsLinearLayout = (LinearLayout) findViewById(R.id.itemsLinearLayout);
        this.restaurantName = (TextView) findViewById(R.id.restaurantNameTV);
        this.orderStatus = (TextView) findViewById(R.id.orderStatusTV);
        this.driverDetails = (TextView) findViewById(R.id.driverDetailsTV);
        this.myName = (TextView) findViewById(R.id.myNameTV);
        this.myPhone = (TextView) findViewById(R.id.myPhoneTV);
        this.myAddress = (TextView) findViewById(R.id.myAddressTV);
        this.myCost = (TextView) findViewById(R.id.orderCostTV);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vieworder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
