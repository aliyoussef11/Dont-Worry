package com.example.dontworry;

public class LocationHelper {

    private String Username;
    private double Longitude;
    private double Latitude;


    public LocationHelper(String username, double longitude, double latitude) {
        Username = username;
        Longitude = longitude;
        Latitude = latitude;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }
}
