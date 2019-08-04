package com.zainco.realtimeloction2.model;

import java.util.HashMap;

public class User {
    private String email;
    private String uid;
    private HashMap<String, User> acceptList;
    public String getEmail() {
        return this.email;
    }

    public User(String email, String uid) {
        this.email = email;
        this.uid = uid;
        acceptList = new HashMap<>();
    }

    public User() {
    }

    public HashMap<String, User> getAcceptList() {
        return acceptList;
    }

    public void setAcceptList(HashMap<String, User> acceptList) {
        this.acceptList = acceptList;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}