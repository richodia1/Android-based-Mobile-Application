package com.iita.iitagenebank;

import java.io.Serializable;

/**
 * Created by Simeon on 04/03/2016.
 */
public class LotListLot implements Serializable
{
    private long lotlistId;
    private long lotId;

    public LotListLot() {}

    public LotListLot(long lotlistId, long lotId)
    {
        super();
        this.lotlistId = lotlistId;
        this.lotId = lotId;
    }

    public long getLotlistId()
    {
        return lotlistId;
    }
    public void setLotlistId(long lotlistId)
    {
        this.lotlistId = lotlistId;
    }

    public long getLotId()
    {
        return lotId;
    }
    public void setLotId(long lotId)
    {
        this.lotId = lotId;
    }
}
