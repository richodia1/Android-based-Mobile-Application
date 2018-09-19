package com.iita.iitagenebank;

import java.io.Serializable;

/**
 * Created by Simeon on 22/02/2016.
 */
public class wsSQLResult  implements Serializable
{
    public String WasSuccessful;
    public String Exception;

    public wsSQLResult() {}

    public String getWasSuccessful()
    {
        return WasSuccessful;
    }
    public void setWasSuccessful(String wasSuccessful)
    {
        this.WasSuccessful = wasSuccessful;
    }

    public String getException()
    {
        return Exception;
    }
    public void setException(String exception)
    {
        this.Exception = exception;
    }
}
