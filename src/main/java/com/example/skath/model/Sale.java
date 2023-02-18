package com.example.skath.model;

import java.sql.Time;
import java.util.Date;

public class Sale {

    private int ID;
    private String client;
    private String user;
    private double received;
    private double returned;
    private double total;
    private Date date;

    private Time time;

    public Sale(int ID, String client, String user, double received, double returned, double total, Date date, Time time) {
        this.ID = ID;
        this.client = client;
        this.user = user;
        this.received = received;
        this.returned = returned;
        this.total = total;
        this.date = date;
        this.time = time;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getReceived() {
        return received;
    }

    public void setReceived(double received) {
        this.received = received;
    }

    public double getReturned() {
        return returned;
    }

    public void setReturned(double returned) {
        this.returned = returned;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}
