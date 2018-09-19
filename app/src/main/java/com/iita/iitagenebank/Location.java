package com.iita.iitagenebank;

import java.io.Serializable;

/**
 * Created by Simeon on 06/03/2016.
 */
public class Location implements Serializable
{
    private long locationId;
    private String locationType;
    private String name;
    private int version;
    private long parentId;

    public Location() {}

    public Location(long locationId, String locationType, String name, int version, long parentId)
    {
        super();
        this.locationId = locationId;
        this.locationType = locationType;
        this.name = name;
        this.version = version;
        this.parentId = parentId;
    }

    public long getLocationId()
    {
        return locationId;
    }
    public void setLocationId(long locationId)
    {
        this.locationId = locationId;
    }

    public String getLocationType()
    {
        return locationType;
    }
    public void setLocationType(String locationType)
    {
        this.locationType = locationType;
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

    public long getParentId()
    {
        return parentId;
    }
    public void setParentId(long parentId)
    {
        this.parentId = parentId;
    }
}
