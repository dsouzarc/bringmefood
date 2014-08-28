package com.ryan.bringmefood;

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

    public Order(String myName, String myNumber, String myAddress, String restaurantName,
                 String uniqueDeviceIdentifier, String[] myOrder, String idNumber, String orderCost) {
        this.myName = myName;
        this.myNumber = myNumber;
        this.myAddress = myAddress;
        this.restaurantName = restaurantName;
        this.uniqueDeviceIdentifier = uniqueDeviceIdentifier;
        this.myOrder = myOrder;
        this.idNumber = idNumber;
        this.orderCost = orderCost;
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
}
