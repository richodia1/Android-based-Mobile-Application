package com.iita.iitagenebank;

import java.io.Serializable;

/**
 * Created by Simeon on 29/02/2016.
 */
public class FieldVariable implements Serializable
{
    private long fieldvarId;
    private String createdBy;
    private String createdDate;
    private String lastUpdated;
    private String lastUpdatedBy;
    private int version;
    private String date;
    private String qty;
    private String var;
    private long lotId;
    private int updated; // Initialised to 0 if object not updated (i.e. if version downloaded is the same as version uploaded) or asigned 1 if object is updated

    public FieldVariable() {}

    public FieldVariable(long fieldvarId, String createdBy, String createdDate, String lastUpdated, String lastUpdatedBy, int version, String date, String qty, String var, long lotId, int updated)
    {
        super();
        this.fieldvarId = fieldvarId;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastUpdated = lastUpdated;
        this.lastUpdatedBy = lastUpdatedBy;
        this.version = version;
        this.date = date;
        this.qty = qty;
        this.var = var;
        this.lotId = lotId;
        this.updated = updated;
    }

    public long getFieldvarId()
    {
        return fieldvarId;
    }
    public void setFieldvarId(long fieldvarId)
    {
        this.fieldvarId = fieldvarId;
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

    public int getVersion()
    {
        return version;
    }
    public void setVersion(int version)
    {
        this.version = version;
    }

    public String getDate()
    {
        return date;
    }
    public void setDate(String date)
    {
        this.date = date;
    }

    public String getQty()
    {
        return qty;
    }
    public void setQty(String qty)
    {
        this.qty = qty;
    }

    public String getVar()
    {
        return var;
    }
    public void setVar(String var)
    {
        this.var = var;
    }

    public long getLotId()
    {
        return lotId;
    }
    public void setLotId(long lotId)
    {
        this.lotId = lotId;
    }

    public int getUpdated()
    {
        return updated;
    }
    public void setUpdated(int updated)
    {
        this.updated = updated;
    }
}
