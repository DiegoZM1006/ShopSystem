package com.example.skath.model;

public class Product {

    private int ID;
    private Storage storage;
    private Family family;
    private String description;
    private double price;
    private int amount;

    public Product(int ID, Storage storage, Family family, String description, double price, int amount) {
        this.ID = ID;
        this.storage = storage;
        this.family = family;
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

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
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
