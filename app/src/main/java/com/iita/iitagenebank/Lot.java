package com.iita.iitagenebank;

import java.io.Serializable;

/**
 * Created by Simeon on 18/02/2016.
 */
public class Lot implements Serializable
{
    private long lotId;
    private long itemId;
    private long containerId;
    private long locationId;
    private long barcode;
    private String itemName;
    private String locationDetail;
    private double quantity;
    private int status;
    private int version;
    private String scale;
    private String lastUpdated;
    private String lastUpdatedBy;
    private int updated; // Initialised to 0 if object not updated (i.e. if version downloaded is the same as version uploaded) or asigned 1 if object is updated

    public Lot() {}

    public Lot(long lotId, long itemId, long containerId, long locationId, long barcode, String itemName, String locationDetail, double quantity, int status, int version, String scale, String lastUpdated, String lastUpdatedBy, int updated)
    {
        super();
        this.lotId = lotId;
        this.itemId = itemId;
        this.containerId = containerId;
        this.locationId = locationId;
        this.barcode = barcode;
        this.itemName = itemName;
        this.locationDetail = locationDetail;
        this.quantity = quantity;
        this.status = status;
        this.version = version;
        this.scale = scale;
        this.lastUpdated = lastUpdated;
        this.lastUpdatedBy = lastUpdatedBy;
        this.updated = updated;
    }

    public long getLotId()
    {
        return lotId;
    }
    public void setLotId(long lotId)
    {
        this.lotId = lotId;
    }

    public long getItemId()
    {
        return itemId;
    }
    public void setItemId(long itemId)
    {
        this.itemId = itemId;
    }

    public long getContainerId()
    {
        return containerId;
    }
    public void setContainerId(long containerId)
    {
        this.containerId = containerId;
    }

    public long getLocationId()
    {
        return locationId;
    }
    public void setLocationId(long locationId)
    {
        this.locationId = locationId;
    }

    public long getBarcode()
    {
        return barcode;
    }
    public void setBarcode(long barcode)
    {
        this.barcode = barcode;
    }

    public String getItemName()
    {
        return itemName;
    }
    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    public String getLocationDetail()
    {
        return locationDetail;
    }
    public void setLocationDetail(String locationDetail)
    {
        this.locationDetail = locationDetail;
    }

    public double getQuantity()
    {
        return quantity;
    }
    public void setQuantity(double quantity)
    {
        this.quantity = quantity;
    }

    public int getStatus()
    {
        return status;
    }
    public void setStatus(int status)
    {
        this.status = status;
    }

    public int getVersion()
    {
        return version;
    }
    public void setVersion(int version)
    {
        this.version = version;
    }

    public String getScale()
    {
        return scale;
    }
    public void setScale(String scale)
    {
        this.scale = scale;
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

    public int getUpdated()
    {
        return updated;
    }
    public void setUpdated(int updated)
    {
        this.updated = updated;
    }
}
