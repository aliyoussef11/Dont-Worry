package com.example.dontworry;

public class RatingHelper{
    float rating;
    String Username, User;

    public RatingHelper(float rating, String username, String user) {
        this.rating = rating;
        Username = username;
        User = user;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }
}
