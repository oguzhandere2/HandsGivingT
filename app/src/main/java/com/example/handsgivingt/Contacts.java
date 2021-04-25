package com.example.handsgivingt;

public class Contacts {

    String name, surname, uid, userType;

    public Contacts() {
    }

    public Contacts(String name, String surname, String uid, String userType) {
        this.name = name;
        this.surname = surname;
        this.uid = uid;
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getUid() {
        return uid;
    }

    public String getUserType() {
        return userType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
