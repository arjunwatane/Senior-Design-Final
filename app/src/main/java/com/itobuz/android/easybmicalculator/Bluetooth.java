package com.itobuz.android.easybmicalculator;

/**
 * Created by Victor on 2/22/2017.
 */

public class Bluetooth
{
    private String name;
    private String address;

    public Bluetooth(String name, String address)
    {
        this.name = name;
        this.address = address;

    }

    public String getName()
    {
        return name;
    }

    public String getAddress()
    {
        return address;
    }

}
