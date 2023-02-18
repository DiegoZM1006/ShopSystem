package com.example.skath.model;

public class SaleDetail {

    private int ID;
    private int idProduct;
    private String nameProduct;
    private int idSales;
    private int amount;
    private double price;
    private double totalPrice;

    public SaleDetail(int ID, int idProduct, String nameProduct, int idSales, int amount, double price, double totalPrice) {
        this.ID = ID;
        this.idProduct = idProduct;
        this.nameProduct = nameProduct;
        this.idSales = idSales;
        this.amount = amount;
        this.price = price;
        this.totalPrice = totalPrice;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public int getIdSales() {
        return idSales;
    }

    public void setIdSales(int idSales) {
        this.idSales = idSales;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
