package com.example.skath.model;

public class Product {

    private int ID;
    private int ID_STORE;
    private int ID_FAMILY;
    private String description;
    private double price;
    private int amount;

    public Product(int ID, int ID_STORE, int ID_FAMILY, String description, double price, int amount) {
        this.ID = ID;
        this.ID_STORE = ID_STORE;
        this.ID_FAMILY = ID_FAMILY;
        this.description = description;
        this.price = price;
        this.amount = amount;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID_STORE() {
        return ID_STORE;
    }

    public void setID_STORE(int ID_STORE) {
        this.ID_STORE = ID_STORE;
    }

    public int getID_FAMILY() {
        return ID_FAMILY;
    }

    public void setID_FAMILY(int ID_FAMILY) {
        this.ID_FAMILY = ID_FAMILY;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

}
