package com.zed_one.zed_one;

import java.util.HashMap;

public class User {
    private String username, userID,email;
    private HashMap<String,Device> Devices;

    public User(String username,String userID,String email){
        this.userID=userID;
        this.username=username;
        this.Devices= new HashMap<>();
        this.email =email;
    }
    public User(){}
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public HashMap<String, Device> getDevices() {
        return Devices;
    }

    public void setDevices(HashMap<String, Device> devices) {
        Devices = devices;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
