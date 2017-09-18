package com.example.mohamedashour.cognitev;

/**
 * Created by Mohamed Ashour on 14/09/2017.
 */
public class foursquareElements {
    private String name;
    private String longtitude,latitude,address;

    private String icon;

    /*public foursquareElements(String name, String longtitude, String latitude, String address) {
        this.name = name;
        this.longtitude = longtitude;
        this.latitude = latitude;
        this.address= address;
    }*/

    public foursquareElements() {
        this.name = "";
        this.longtitude = "";
        this.latitude = "";
        this.address= "";
    }

    public void setCategoryIcon(String icon)
    {
        this.icon = icon;
    }
    public String getCategoryIcon()
    {
        return this.icon;
    }
    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getAddress()
    {
        return this.address;
    }

    public void setLongtitude(String longtitude)
    {
        this.longtitude = longtitude;
    }

    public double getLongtitude( )
    {
        return Double.parseDouble(this.longtitude)  ;
    }


    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    public double getLatitude( )
    {
        return  Double.parseDouble(this.latitude)  ;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}