package com.thangtruong19.restaurantfinder.PlaceModel;

/**
 * Created by User on 20/11/2018.
 */

public class Periods {
    private Open open;

    private Close close;

    public Open getOpen ()
    {
        return open;
    }

    public void setOpen (Open open)
    {
        this.open = open;
    }

    public Close getClose ()
    {
        return close;
    }

    public void setClose (Close close)
    {
        this.close = close;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [open = "+open+", close = "+close+"]";
    }
}
