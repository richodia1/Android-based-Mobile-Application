package com.iita.iitagenebank;

import java.io.Serializable;

/**
 * Created by Simeon on 02/03/2016.
 */
public class Migration implements Serializable
{
    private long migId;
    private String migrationDate;
    private long newLocationId;
    private String newLocationName;
    private long oldLocationId;
    private String oldLocationName;
    private long lotId;
    private String reason;
    private String createdBy;
    private String createdDate;
    private String lastUpdated;
    private String lastUpdatedBy;

    public Migration() {}

    public Migration(long migId, String migrationDate, long newLocationId, String newLocationName, long oldLocationId, String oldLocationName, long lotId, String reason, String createdBy, String createdDate, String lastUpdated, String lastUpdatedBy)
    {
        super();
        this.migId = migId;
        this.migrationDate = migrationDate;
        this.newLocationId = newLocationId;
        this.newLocationName = newLocationName;
        this.oldLocationId = oldLocationId;
        this.oldLocationName = oldLocationName;
        this.lotId = lotId;
        this.reason = reason;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastUpdated = lastUpdated;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public long getMigId()
    {
        return migId;
    }
    public void setMigId(long migId)
    {
        this.migId = migId;
    }

    public String getMigrationDate()
    {
        return migrationDate;
    }
    public void setMigrationDate(String migrationDate)
    {
        this.migrationDate = migrationDate;
    }

    public long getNewLocationId()
    {
        return newLocationId;
    }
    public void setNewLocationId(long newLocationId)
    {
        this.newLocationId = newLocationId;
    }

    public String getNewLocationName()
    {
        return newLocationName;
    }
    public void setNewLocationName(String newLocationName)
    {
        this.newLocationName = newLocationName;
    }

    public long getOldLocationId()
    {
        return oldLocationId;
    }
    public void setOldLocationId(long oldLocationId)
    {
        this.oldLocationId = oldLocationId;
    }

    public String getOldLocationName()
    {
        return oldLocationName;
    }
    public void setOldLocationName(String oldLocationName)
    {
        this.oldLocationName = oldLocationName;
    }

    public long getLotId()
    {
        return lotId;
    }
    public void setLotId(long lotId)
    {
        this.lotId = lotId;
    }

    public String getReason()
    {
        return reason;
    }
    public void setReason(String reason)
    {
        this.reason = reason;
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
}
