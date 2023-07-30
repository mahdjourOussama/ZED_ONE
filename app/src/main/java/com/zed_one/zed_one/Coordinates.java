package com.zed_one.zed_one;


public class Coordinates {
    private String Timestamp;
    private double Latitude, Longitude;

    public Coordinates() {
        // Empty constructor required for Firebase
    }
    public Coordinates(String timestamp, double latitude, double longitude){
        this.Timestamp = timestamp;
        this.Latitude = latitude;
        this.Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(float latitude) {
        this.Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(float longitude) {
        this.Longitude = longitude;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.Timestamp = timestamp;
    }


}
