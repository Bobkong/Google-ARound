package com.google.ar.core.codelabs.arlocalizer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CloudAnchor implements Serializable {
    @SerializedName("username")
    private String username;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("altitude")
    private double altitude;

    public CloudAnchor(String username, double latitude, double longitude, double altitude) {
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
}
