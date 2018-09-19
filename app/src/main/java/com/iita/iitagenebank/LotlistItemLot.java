package com.iita.iitagenebank;

import java.io.Serializable;

/**
 * Created by Simeon on 06/03/2016.
 */
public class LotlistItemLot implements Serializable
{
    private long lotlistId;
    private long itemId;
    private String alternativeIdentifier;
    private String itemName;
    private String prefix;
    private long itemTypeId;
    private String latinName;
    private long lotId;
    private long containerId;
    private long locationId;
    private double quantity;
    private String scale;
    private String lastUpdated;
    private String lastUpdatedBy;

    public LotlistItemLot() {}

    public LotlistItemLot(long lotlistId, long itemId, String alternativeIdentifier, String itemName, String prefix, long itemTypeId, String latinName, long lotId, long containerId, long locationId, double quantity, String scale, String lastUpdated, String lastUpdatedBy)
    {
        super();
        this.lotlistId = lotlistId;
        this.itemId = itemId;
        this.alternativeIdentifier = alternativeIdentifier;
        this.itemName = itemName;
        this.prefix = prefix;
        this.itemTypeId = itemTypeId;
        this.latinName = latinName;
        this.lotId = lotId;
        this.containerId = containerId;
        this.locationId = locationId;
        this.quantity = quantity;
        this.scale = scale;
        this.lastUpdated = lastUpdated;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public long getLotlistId()
    {
        return lotlistId;
    }
    public void setLotlistId(long lotlistId)
    {
        this.lotlistId = lotlistId;
    }

    public long getItemId()
    {
        return itemId;
    }
    public void setItemId(long itemId)
    {
        this.itemId = itemId;
    }

    public String getAlternativeIdentifier()
    {
        return alternativeIdentifier;
    }
    public void setAlternativeIdentifier(String alternativeIdentifier)
    {
        this.alternativeIdentifier = alternativeIdentifier;
    }

    public String getItemName()
    {
        return itemName;
    }
    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    public String getPrefix()
    {
        return prefix;
    }
    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    public long getItemTypeId()
    {
        return itemTypeId;
    }
    public void setItemTypeId(long itemTypeId)
    {
        this.itemTypeId = itemTypeId;
    }

    public String getLatinName()
    {
        return latinName;
    }
    public void setLatinName(String latinName)
    {
        this.latinName = latinName;
    }

    public long getLotId()
    {
        return lotId;
    }
    public void setLotId(long lotId)
    {
        this.lotId = lotId;
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

    public double getQuantity()
    {
        return quantity;
    }
    public void setQuantity(double quantity)
    {
        this.quantity = quantity;
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
}
