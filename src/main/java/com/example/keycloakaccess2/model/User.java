package com.example.keycloakaccess2.model;

public class User {

    String ID;
    String Name;
    String Last_name;

    public User() {

    }

    public User(String iD, String name, String last_name) {
        super();
        ID = iD;
        Name = name;
        Last_name = last_name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String iD) {
        ID = iD;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLast_name() {
        return Last_name;
    }

    public void setLast_name(String last_name) {
        Last_name = last_name;
    }

}
