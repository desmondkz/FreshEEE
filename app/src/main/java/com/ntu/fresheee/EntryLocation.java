package com.ntu.fresheee;

public class EntryLocation {

    public double latitude, longitude;
    public String name, image;


    public EntryLocation() {

    }

    public EntryLocation(double latitude, double longitude, String name, String image) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.image = image;
    }
}
