package com.example.skath.model;

import java.util.Date;

public class CreditDetail {

    private Integer idTemp;
    private Integer id;
    private String userName;
    private double pay;
    private double payable;
    private double total;
    private Date date;

    public CreditDetail(Integer idTemp, Integer id, String userName, double pay, double payable, double total, Date date) {
        this.idTemp = idTemp;
        this.id = id;
        this.userName = userName;
        this.pay = pay;
        this.payable = payable;
        this.total = total;
        this.date = date;
    }

    public Integer getIdTemp() {
        return idTemp;
    }

    public void setIdTemp(Integer idTemp) {
        this.idTemp = idTemp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getPay() {
        return pay;
    }

    public void setPay(double pay) {
        this.pay = pay;
    }

    public double getPayable() {
        return payable;
    }

    public void setPayable(double payable) {
        this.payable = payable;
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

}
