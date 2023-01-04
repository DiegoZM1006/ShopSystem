package com.example.skath.model;

public class Family {

    // Error problable los atributos tienen que ser publicos

    public int id;
    public String name;

    public Family(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
