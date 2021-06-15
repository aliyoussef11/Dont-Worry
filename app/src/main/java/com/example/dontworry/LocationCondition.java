package com.example.dontworry;

public class LocationCondition {
    String Condition;
    String username;

    public LocationCondition(String username, String condition) {
        this.username = username;
        Condition = condition;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCondition() {
        return Condition;
    }

    public void setCondition(String condition) {
        Condition = condition;
    }
}
