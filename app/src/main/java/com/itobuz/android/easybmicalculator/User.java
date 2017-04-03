package com.itobuz.android.easybmicalculator;

/**
 * Created by Victor on 11/15/2016.
 */

public class User
{
    String name, password;

    User(String name, String pass){
        this.name = name;
        this.password = pass;
    }


    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }
}