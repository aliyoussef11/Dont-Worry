package com.example.dontworry;

public class ParentHelper {
    String username;
    String password;
    String ChildUsername;

    public ParentHelper(String username, String password, String childUsername) {
        this.username = username;
        this.password = password;
        ChildUsername = childUsername;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getChildUsername() {
        return ChildUsername;
    }

    public void setChildUsername(String childUsername) {
        ChildUsername = childUsername;
    }
}
