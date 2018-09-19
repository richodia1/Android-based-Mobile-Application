package com.iita.iitagenebank;

import java.io.Serializable;

/**
 * Created by Simeon on 11/09/2015.
 */
public class Database implements Serializable
{
    private String lot;
    private String lotVariable;
    private String lotList;
    private String fieldVariable;
    private String childlocation;
    private String parentlocation;
    private String migration;
    private String variable;
    private String lotListLot;
    private String containerType;
    private String item;
    private String itemType;
    private String subtype;
    private String subtypeTransaction;
    private String deletedFieldVariable;
    private String allusers;

    public Database() {}

    public Database(String lot, String lotVariable, String lotList, String fieldVariable, String childlocation, String parentlocation, String migration, String variable, String lotListLot, String containerType, String item, String itemType, String subtype, String subtypeTransaction, String deletedFieldVariable, String allusers)
    {
        super();
        this.lot = lot;
        this.lotVariable = lotVariable;
        this.lotList = lotList;
        this.fieldVariable = fieldVariable;
        this.childlocation = childlocation;
        this.parentlocation = parentlocation;
        this.migration = migration;
        this.variable = variable;
        this.lotListLot = lotListLot;
        this.containerType = containerType;
        this. item =  item;
        this.itemType = itemType;
        this.subtype = subtype;
        this.subtypeTransaction = subtypeTransaction;
        this.deletedFieldVariable = deletedFieldVariable;
        this.allusers = allusers;
    }

    public String getLot()
    {
        return lot;
    }
    public void setLot(String lot)
    {
        this.lot = lot;
    }

    public String getLotVariable()
    {
        return lotVariable;
    }
    public void setLotVariable(String lotVariable)
    {
        this.lotVariable = lotVariable;
    }

    public String getLotList()
    {
        return lotList;
    }
    public void setLotList(String lotList)
    {
        this.lotList = lotList;
    }

    public String getFieldVariable()
    {
        return fieldVariable;
    }
    public void setFieldVariable(String fieldVariable)
    {
        this.fieldVariable = fieldVariable;
    }

    public String getChildLocation()
    {
        return childlocation;
    }
    public void setChildLocation(String childlocation)
    {
        this.childlocation = childlocation;
    }

    public String getParentLocation()
    {
        return parentlocation;
    }
    public void setParentLocation(String parentlocation)
    {
        this.parentlocation = parentlocation;
    }

    public String getMigration()
    {
        return migration;
    }
    public void setMigration(String migration)
    {
        this.migration = migration;
    }

    public String getVariable()
    {
        return variable;
    }
    public void setVariable(String variable)
    {
        this.variable = variable;
    }

    public String getLotListLot()
    {
        return lotListLot;
    }
    public void setLotListLot(String lotListLot)
    {
        this.lotListLot = lotListLot;
    }

    public String getContainerType()
    {
        return containerType;
    }
    public void setContainerType(String containerType)
    {
        this.containerType = containerType;
    }

    public String getItem()
    {
        return item;
    }
    public void setItem(String item)
    {
        this.item = item;
    }

    public String getItemType()
    {
        return itemType;
    }
    public void setItemType(String itemType)
    {
        this.itemType = itemType;
    }

    public String getSubtype()
    {
        return subtype;
    }
    public void setSubtype(String  subtype)
    {
        this.subtype = subtype;
    }

    public String getSubtypeTransaction()
    {
        return subtypeTransaction;
    }
    public void setSubtypeTransaction(String  subtypeTransaction)
    {
        this.subtypeTransaction = subtypeTransaction;
    }

    public String getDeletedFieldVariable()
    {
        return deletedFieldVariable;
    }
    public void setDeletedFieldVariable(String deletedFieldVariable)
    {
        this.deletedFieldVariable = deletedFieldVariable;
    }

    public String getAllusers()
    {
        return allusers;
    }
    public void setAllusers(String allusers)
    {
        this.allusers = allusers;
    }
}