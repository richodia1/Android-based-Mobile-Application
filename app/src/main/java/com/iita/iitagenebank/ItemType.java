package com.iita.iitagenebank;

import java.io.Serializable;

/**
 * Created by Simeon on 06/03/2016.
 */
public class ItemType implements Serializable
{
    private long itemtypeId;
    private String name;
    private int version;
    private String shortName;

    public ItemType() {}

    public ItemType(long itemtypeId, String name, int version, String shortName)
    {
        super();
        this.itemtypeId = itemtypeId;
        this.name = name;
        this.version = version;
        this.shortName = shortName;
    }

    public long getItemtypeId()
    {
        return itemtypeId;
    }
    public void setItemtypeId(long itemtypeId)
    {
        this.itemtypeId = itemtypeId;
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public int getVersion()
    {
        return version;
    }
    public void setVersion(int version)
    {
        this.version = version;
    }

    public String getShortName()
    {
        return shortName;
    }
    public void setShortName(String shortName)
    {
        this.shortName = shortName;
    }
}
