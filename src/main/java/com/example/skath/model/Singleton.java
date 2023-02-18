package com.example.skath.model;

import javafx.beans.Observable;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Singleton {

    private static Singleton instance;
    private Connection cn;
    private User user;
    private boolean isSideBarCollapsed;
    private int idToScreenPermits;
    private Client clientToFastClient;
    private Product productToFastProduct;
    private ObservableList<Product> obsProducts;
    private double totalPayment;
    private boolean theSaleWasMade = false;
    private Integer idSalesDetail;
    private Integer idCreditsDetail;


    private Singleton() {
        isSideBarCollapsed = false;
    }

    public static Singleton getInstance() {
        if(instance == null) {
            instance = new Singleton();
            // instance.connectionToDB();
        }
        return instance;
    }

    private void connectionToDB() {
        try {
            String urlDb = "jdbc:mysql://localhost/db_mysystem";
            String username = "root";
            String password = "";
            cn = DriverManager.getConnection(urlDb, username, password);
        } catch(Exception e) {
            Alerts.error("Error de conexion", "Hubo un error de conexion, por favor conectate");
            System.out.println(e.getMessage());
        }
    }

    public Connection getCn() {
        connectionToDB();
        return cn;
    }

    public void closeCn() throws SQLException {
        cn.close();
    }

    public int getIdToScreenPermits() {
        return idToScreenPermits;
    }

    public void setIdToScreenPermits(int idToScreenPermits) {
        this.idToScreenPermits = idToScreenPermits;
    }

    public void setCn(Connection cn) {
        this.cn = cn;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static void setInstance(Singleton instance) {
        Singleton.instance = instance;
    }

    public boolean isSideBarCollapsed() {
        return isSideBarCollapsed;
    }

    public void setSideBarCollapsed(boolean sideBarCollapsed) {
        isSideBarCollapsed = sideBarCollapsed;
    }

    public Client getClientToFastClient() {
        return clientToFastClient;
    }

    public void setClientToFastClient(Client clientToFastClient) {
        this.clientToFastClient = clientToFastClient;
    }

    public Product getProductToFastProduct() {
        return productToFastProduct;
    }

    public void setProductToFastProduct(Product productToFastProduct) {
        this.productToFastProduct = productToFastProduct;
    }

    public ObservableList<Product> getObsProducts() {
        return obsProducts;
    }

    public void setObsProducts(ObservableList<Product> obsProducts) {
        this.obsProducts = obsProducts;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(double totalPayment) {
        this.totalPayment = totalPayment;
    }

    public boolean isTheSaleWasMade() {
        return theSaleWasMade;
    }

    public void setTheSaleWasMade(boolean theSaleWasMade) {
        this.theSaleWasMade = theSaleWasMade;
    }

    public Integer getIdSalesDetail() {
        return idSalesDetail;
    }

    public void setIdSalesDetail(Integer idSalesDetail) {
        this.idSalesDetail = idSalesDetail;
    }

    public Integer getIdCreditsDetail() {
        return idCreditsDetail;
    }

    public void setIdCreditsDetail(Integer idCreditsDetail) {
        this.idCreditsDetail = idCreditsDetail;
    }
}
