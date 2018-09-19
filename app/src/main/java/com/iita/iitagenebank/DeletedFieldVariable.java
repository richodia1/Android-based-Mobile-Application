package com.iita.iitagenebank;

import java.io.Serializable;

/**
 * Created by Simeon on 12/04/2016.
 */
public class DeletedFieldVariable implements Serializable
{
    private long fieldvarId;

    public DeletedFieldVariable() {}

    public DeletedFieldVariable(long fieldvarId)
    {
        super();
        this.fieldvarId = fieldvarId;
    }

    public long getFieldvarId()
    {
        return fieldvarId;
    }
    public void setFieldvarId(long fieldvarId)
    {
        this.fieldvarId = fieldvarId;
    }
}
