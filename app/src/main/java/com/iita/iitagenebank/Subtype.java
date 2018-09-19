package com.iita.iitagenebank;

import java.io.Serializable;

/**
 * Created by Simeon on 08/03/2016.
 */
public class Subtype implements Serializable
{
    private String name;

    public Subtype() {}

    public Subtype(String name)
    {
        super();
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
}