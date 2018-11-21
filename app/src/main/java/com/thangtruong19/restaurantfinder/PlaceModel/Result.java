package com.thangtruong19.restaurantfinder.PlaceModel;


/**
 * Created by User on 20/11/2018.
 */

public class Result {
    private Address_components[] address_components;

    private String name;

    private String formatted_phone_number;

    private String rating;

    private Opening_hours opening_hours;

    private String formatted_address;

    private String url;

    public Address_components[] getAddress_components ()
    {
        return address_components;
    }

    public void setAddress_components (Address_components[] address_components)
    {
        this.address_components = address_components;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getFormatted_phone_number ()
    {
        return formatted_phone_number;
    }

    public void setFormatted_phone_number (String formatted_phone_number)
    {
        this.formatted_phone_number = formatted_phone_number;
    }

    public String getRating ()
    {
        return rating;
    }

    public void setRating (String rating)
    {
        this.rating = rating;
    }

    public Opening_hours getOpening_hours ()
    {
        return opening_hours;
    }

    public void setOpening_hours (Opening_hours opening_hours)
    {
        this.opening_hours = opening_hours;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString()
    {
        return "PlaceDetail [address_components = "+address_components+", name = "+name+", formatted_phone_number = "+formatted_phone_number+", rating = "+rating+", opening_hours = "+opening_hours+"]";
    }
}
