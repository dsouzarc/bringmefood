package com.ryan.bringmefood;

public class MenuItem {
    private String name, cost, description;

    public MenuItem(String name, String cost, String description) {
        this.name = name;
        this.cost = cost;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getCost() {
        return cost;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ITEM: " + this.name + " " +
                "\tCOST: " + this.cost + " " +
                "\tDESCRIPTION: " + this.description;
    }
}
