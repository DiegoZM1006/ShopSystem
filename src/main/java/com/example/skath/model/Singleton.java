package com.example.skath.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Singleton {

    private static Singleton instance;
    private Connection cn;
    private User user;
    private boolean isSideBarCollapsed;

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

}
