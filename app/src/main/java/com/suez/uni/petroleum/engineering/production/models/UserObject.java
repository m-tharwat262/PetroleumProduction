package com.suez.uni.petroleum.engineering.production.models;

public class UserObject {

    private String name = "";
    private String emailAddress = "";
    private String password = "";


    public UserObject() {

    }

    public UserObject(String name, String emailAddress) {
        this.name = name;
        this.emailAddress = emailAddress;
    }


    public UserObject(String name, String emailAddress, String password) {
        this.name = name;
        this.emailAddress = emailAddress;
        this.password = password;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
