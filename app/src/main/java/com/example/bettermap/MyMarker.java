package com.example.bettermap;

public class MyMarker {
    private String locality;
    private String countryName;
    private String postalCode;
    private double latitude;
    private double longitude;
    private int id;


    public MyMarker() {
    }

    public MyMarker(String locality, String countryName, String postalCode,double latitude,double longitude) {
        this.locality = locality;
        this.countryName = countryName;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public MyMarker(int id,String locality, String countryName, String postalCode,double latitude,double longitude) {
        this.locality = locality;
        this.countryName = countryName;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
    }


    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
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

    @Override
    public String toString() {
        return "MyMarker{" +
                "locality='" + locality + '\'' +
                ", countryName='" + countryName + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", id=" + id +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
