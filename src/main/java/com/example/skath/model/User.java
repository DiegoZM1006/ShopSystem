package com.example.skath.model;

public class User {

    private int ID;
    private String username;
    private String name;
    private String lastName;
    private String password;

    public User(int ID, String username, String name, String lastName) {
        this.ID = ID;
        this.username = username;
        this.name = name;
        this.lastName = lastName;
    }

    public User(int ID, String username, String name, String lastName, String password) {
        this.ID = ID;
        this.username = username;
        this.name = name;
        this.lastName = lastName;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
