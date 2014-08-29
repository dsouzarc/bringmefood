package com.ryan.bringmefood;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
/**
 * Created by Ryan on 8/28/14.
 */
public class Order {

    private final String myName;
    private final String myNumber;
    private final String myAddress;
    private final String restaurantName;
    private final String uniqueDeviceIdentifier;
    private final String[] myOrder;
    private String orderCost;
    private final String idNumber;
    private final String calendarTimeMillis;
    private final Calendar theDate;

    private String status;

    //Name, phone number, my address, restaurant address, UID, myOrder[], order ID, orderCost, time in millis, status

    public Order(String myName, String myNumber, String myAddress, String restaurantName,
                 String uniqueDeviceIdentifier, String[] myOrder, String idNumber, String orderCost,
                 final String calendarTimeMillis, final String status) {
        this.myName = myName;
        this.myNumber = myNumber;
        this.myAddress = myAddress;
        this.restaurantName = restaurantName;
        this.uniqueDeviceIdentifier = uniqueDeviceIdentifier;
        this.myOrder = myOrder;
        this.idNumber = idNumber;
        this.orderCost = orderCost;
        this.calendarTimeMillis = calendarTimeMillis;
        this.theDate = new GregorianCalendar();
        this.theDate.setTimeInMillis(Long.parseLong(calendarTimeMillis));
        this.status = status;
    }

    public JSONObject toJSONObject() {
        final JSONObject theObject = new JSONObject();

        try {
            theObject.put("myName", myName);
            theObject.put("myNumber", myNumber);
            theObject.put("myAddress", myAddress);
            theObject.put("restaurantName", restaurantName);
            theObject.put("uniqueDeviceIdentifier", uniqueDeviceIdentifier);
            theObject.put("myOrder", new JSONArray(myOrder));
            theObject.put("id", idNumber);
            theObject.put("orderCost", orderCost);
            theObject.put("time", calendarTimeMillis);
            theObject.put("status", status);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return theObject;
    }

    public String getStatus() {
        return getStatusAsString(getStatus(Integer.parseInt(status)));
    }

    public static Order getOrder(final JSONObject theJSON) {

        try {
            final JSONArray theOrder = theJSON.getJSONArray("myOrder");

            final String[] theItems = new String[theOrder.length()];
            for(int i = 0; i < theOrder.length(); i++) {
                theItems[i] = theOrder.get(i).toString();
            }

            return new Order(theJSON.getString("myName"), theJSON.getString("myNumber"),
                    theJSON.getString("myAddress"), theJSON.getString("restaurantName"),
                    theJSON.getString("uniqueDeviceIdentifier"), theItems,
                    theJSON.getString("id"), theJSON.getString("orderCost"),
                    theJSON.getString("time"), theJSON.getString("status"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public enum STATUS {
        UNCLAIMED, CLAIMED, IN_PROCESS_OF_ORDERING, FOOD_ORDERED,
        FOOD_PICKED_UP, ON_WAY_TO_HOUSE, DELIVERED;
    }

    public String getCalendarTimeMillis() {
        return this.calendarTimeMillis;
    }

    public Calendar getCalendar() {
        return this.theDate;
    }

    public static String getStatusAsString(final STATUS theStatus) {
        switch (theStatus) {
            case UNCLAIMED:
                return "Waiting to be claimed";
            case CLAIMED:
                return "Claimed by driver";
            case IN_PROCESS_OF_ORDERING:
                return "In process of being ordered";
            case FOOD_ORDERED:
                return "Food ordered";
            case FOOD_PICKED_UP:
                return "Food picked up";
            case ON_WAY_TO_HOUSE:
                return "On way to house";
            case DELIVERED:
                return "Delivered";
            default:
                return "";
        }
    }

    public static int getStatusNumber(final String statusInString) {
        if(statusInString.contains("Waiting to be claimed")) {
            return 0;
        }
        if(statusInString.contains("Claimed by driver")) {
            return 1;
        }
        if(statusInString.contains("In process of being ordered")) {
            return 2;
        }
        if(statusInString.contains("Food ordered")) {
            return 3;
        }
        if(statusInString.contains("Food picked up")) {
            return 4;
        }
        if(statusInString.contains("On way to house")) {
            return 5;
        }
        if(statusInString.contains("Delivered")) {
            return 6;
        }
        return 0;
    }


    public static STATUS getStatus(final int statusNumber) {
        return STATUS.values()[statusNumber];
    }


    public String getMyName() {
        return myName;
    }

    public String getMyNumber() {
        return myNumber;
    }

    public String getMyAddress() {
        return myAddress;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getUniqueDeviceIdentifier() {
        return uniqueDeviceIdentifier;
    }

    public String[] getMyOrder() {
        return myOrder;
    }

    public String getOrderCost() {
        return orderCost;
    }

    public void setOrderCost(String orderCost) {
        this.orderCost = orderCost;
    }

    public String getIdNumber() {
        return idNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof com.ryan.bringmefood.Order))
            return false;

        Order order = (Order) o;

        if (!calendarTimeMillis.equals(order.calendarTimeMillis))
            return false;
        if (!idNumber.equals(order.idNumber))
            return false;
        if (!myAddress.equals(order.myAddress))
            return false;
        if (!myName.equals(order.myName))
            return false;
        if (!myNumber.equals(order.myNumber))
            return false;
        if (!Arrays.equals(myOrder, order.myOrder))
            return false;
        if (!orderCost.equals(order.orderCost))
            return false;
        if (!restaurantName.equals(order.restaurantName))
            return false;
        if (!status.equals(order.status))
            return false;
        if (!theDate.equals(order.theDate))
            return false;
        if (!uniqueDeviceIdentifier.equals(order.uniqueDeviceIdentifier))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = myName.hashCode();
        result = 31 * result + myNumber.hashCode();
        result = 31 * result + myAddress.hashCode();
        result = 31 * result + restaurantName.hashCode();
        result = 31 * result + uniqueDeviceIdentifier.hashCode();
        result = 31 * result + Arrays.hashCode(myOrder);
        result = 31 * result + orderCost.hashCode();
        result = 31 * result + idNumber.hashCode();
        result = 31 * result + calendarTimeMillis.hashCode();
        result = 31 * result + theDate.hashCode();
        result = 31 * result + status.hashCode();
        return result;
    }

    public void log(final String message) {
        Log.e("com.ryan.bringmefood", message);
    }
}
