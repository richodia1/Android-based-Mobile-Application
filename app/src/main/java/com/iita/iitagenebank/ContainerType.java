package com.iita.iitagenebank;

import java.io.Serializable;

/**
 * Created by Simeon on 06/03/2016.
 */
public class ContainerType implements Serializable
{
    private long containertypeId;
    private String name;
    private int version;

    public ContainerType() {}

    public ContainerType(long containertypeId, String name, int version)
    {
        super();
        this.containertypeId = containertypeId;
        this.name = name;
        this.version = version;
    }

    public long getContainertypeId()
    {
        return containertypeId;
    }
    public void setContainertypeId(long containertypeId)
    {
        this.containertypeId = containertypeId;
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
}