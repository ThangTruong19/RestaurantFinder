package com.thangtruong19.restaurantfinder.Model;

/**
 * Created by User on 19/11/2018.
 */

public class Opening_hours {
    private String open_now;

    public String getOpen_now ()
    {
        return open_now;
    }

    public void setOpen_now (String open_now)
    {
        this.open_now = open_now;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [open_now = "+open_now+"]";
    }
}
