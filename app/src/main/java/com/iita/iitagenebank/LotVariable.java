package com.iita.iitagenebank;

import java.io.Serializable;

/**
 * Created by Simeon on 02/03/2016.
 */
public class LotVariable implements Serializable {
    private long lotvariableId;
    private String createdBy;
    private String createdDate;
    private String lastUpdated;
    private String lastUpdatedBy;
    private int version;
    private double quantity;
    private String variabledate;
    private long lotId;
    private long variableId;
    private String variableName;
    private int updated; // Initialised to 0 if object not updated (i.e. if version downloaded is the same as version uploaded) or asigned 1 if object is updated

    public LotVariable() {}

    public LotVariable(long lotvariableId, String createdBy, String createdDate, String lastUpdated, String lastUpdatedBy, int version, double quantity, String variabledate, long lotId, long variableId, String variableName, int updated)
    {
        super();
        this.lotvariableId = lotvariableId;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastUpdated = lastUpdated;
        this.lastUpdatedBy = lastUpdatedBy;
        this.version = version;
        this.quantity = quantity;
        this.variabledate = variabledate;
        this.lotId = lotId;
        this.variableId = variableId;
        this.variableName = variableName;
        this.updated = updated;
    }

    public long getLotvariableId()
    {
        return lotvariableId;
    }
    public void setLotvariableId(long lotvariableId)
    {
        this.lotvariableId = lotvariableId;
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

    public double getQuantity()
    {
        return quantity;
    }
    public void setQuantity(double quantity)
    {
        this.quantity = quantity;
    }

    public String getVariabledate()
    {
        return variabledate;
    }
    public void setVariabledate(String variabledate)
    {
        this.variabledate = variabledate;
    }

    public long getLotId()
    {
        return lotId;
    }
    public void setLotId(long lotId)
    {
        this.lotId = lotId;
    }

    public long getVariableId()
    {
        return variableId;
    }
    public void setVariableId(long variableId)
    {
        this.variableId = variableId;
    }

    public String getVariableName()
    {
        return variableName;
    }
    public void setVariableName(String variableName)
    {
        this.variableName = variableName;
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