package com.iita.iitagenebank;

import java.io.Serializable;

/**
 * Created by Simeon on 04/03/2016.
 */
public class LotList implements Serializable
{
    private long lotlistId;
    private String createdBy;
    private String createdDate;
    private String lastUpdated;
    private String lastUpdatedBy;
    private String name;
    private long owner_id;

    public LotList() {}

    public LotList(long lotlistId, String createdBy, String createdDate, String lastUpdated, String lastUpdatedBy, String name, long owner_id)
    {
        super();
        this.lotlistId = lotlistId;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastUpdated = lastUpdated;
        this.lastUpdatedBy = lastUpdatedBy;
        this.name = name;
        this.owner_id = owner_id;
    }

    public long getLotlistId()
    {
        return lotlistId;
    }
    public void setLotlistId(long lotlistId)
    {
        this.lotlistId = lotlistId;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }
    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }

    public String getCreatedDate()
    {
        return createdDate;
    }
    public void setCreatedDate(String createdDate)
    {
        this.createdDate = createdDate;
    }

    public String getLastUpdated()
    {
        return lastUpdated;
    }
    public void setLastUpdated(String lastUpdated)
    {
        this.lastUpdated = lastUpdated;
    }

    public String getLastUpdatedBy()
    {
        return lastUpdatedBy;
    }
    public void setLastUpdatedBy(String lastUpdatedBy)
    {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public long getOwner_id()
    {
        return owner_id;
    }
    public void setOwner_id(long owner_id)
    {
        this.owner_id = owner_id;
    }
}