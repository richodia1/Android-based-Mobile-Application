package com.iita.iitagenebank;

import java.io.Serializable;

/**
 * Created by Simeon on 06/03/2016.
 */
public class Item implements Serializable
{
    private long itemId;
    private long accessionIdentifier;
    private String alternativeIdentifier;
    private String dateLastModified;
    private String name;
    private String prefix;
    private int version;
    private long itemTypeId;
    private String latinName;

    public Item() {}

    public Item(long itemId, long accessionIdentifier, String alternativeIdentifier, String dateLastModified, String name, String prefix, int version, long itemTypeId, String latinName)
    {
        super();
        this.itemId = itemId;
        this.accessionIdentifier = accessionIdentifier;
        this.alternativeIdentifier = alternativeIdentifier;
        this.dateLastModified = dateLastModified;
        this.name = name;
        this.prefix = prefix;
        this.version = version;
        this.itemTypeId = itemTypeId;
        this.latinName = latinName;
    }

    public long getItemId()
    {
        return itemId;
    }
    public void setItemId(long itemId)
    {
        this.itemId = itemId;
    }

    public long getAccessionIdentifier()
    {
        return accessionIdentifier;
    }
    public void setAccessionIdentifier(long accessionIdentifier)
    {
        this.accessionIdentifier = accessionIdentifier;
    }

    public String getAlternativeIdentifier()
    {
        return alternativeIdentifier;
    }
    public void setAlternativeIdentifier(String alternativeIdentifier)
    {
        this.alternativeIdentifier = alternativeIdentifier;
    }

    public String getDateLastModified()
    {
        return dateLastModified;
    }
    public void setDateLastModified(String dateLastModified)
    {
        this.dateLastModified = dateLastModified;
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getPrefix()
    {
        return prefix;
    }
    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    public int getVersion()
    {
        return version;
    }
    public void setVersion(int version)
    {
        this.version = version;
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
}
