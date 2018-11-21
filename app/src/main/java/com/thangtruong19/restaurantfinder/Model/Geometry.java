package com.thangtruong19.restaurantfinder.Model;


/**
 * Created by User on 19/11/2018.
 */

public class Geometry {
    private Viewport viewport;

    private Location1 location;

    public Viewport getViewport ()
    {
        return viewport;
    }

    public void setViewport (Viewport viewport)
    {
        this.viewport = viewport;
    }

    public Location1 getLocation ()
    {
        return location;
    }

    public void setLocation (Location1 location)
    {
        this.location = location;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [viewport = "+viewport+", location = "+location+"]";
    }
}
