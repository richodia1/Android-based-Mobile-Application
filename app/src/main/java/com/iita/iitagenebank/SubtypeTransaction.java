package com.iita.iitagenebank;

import java.io.Serializable;

/**
 * Created by Simeon on 08/03/2016.
 */
public class SubtypeTransaction implements Serializable {
    private long subtypeTransId;
    private String subtype;
    private long lotId;
    private double quantity;
    private String date;
    private String scale;
    private String createdBy;
    private String createdDate;
    private String lastUpdated;
    private String lastUpdatedBy;

    public SubtypeTransaction() {
    }

    public SubtypeTransaction(long subtypeTransId, String subtype, long lotId, double quantity, String date, String scale, String createdBy, String createdDate, String lastUpdated, String lastUpdatedBy) {
        this.subtypeTransId = subtypeTransId;
        this.subtype = subtype;
        this.lotId = lotId;
        this.quantity = quantity;
        this.date = date;
        this.scale = scale;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastUpdated = lastUpdated;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public long getSubtypeTransId() {
        return subtypeTransId;
    }

    public void setSubtypeTransId(long subtypeTransId) {
        this.subtypeTransId = subtypeTransId;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public long getLotId() {
        return lotId;
    }

    public void setLotId(long lotId) {
        this.lotId = lotId;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
}