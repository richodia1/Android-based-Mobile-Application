package com.iita.iitagenebank;

import java.io.Serializable;

/**
 * Created by Simeon on 26/02/2016.
 */
public class SettingDetail implements Serializable
{
    private String server;  //the current server you are uploading/downloading to/from; which is either Tomcat 1 (Production Server) or Tomcat 2 (Testing Server)
    private String serverUrl;  //the web service url of the chosen server
    private String updateData;  //the current data you are updating; which is either device or server data
    private String lastOperation; //the last operation performed; which is either data upload, data download, data re-initialization
    // is used to set the id of any new record added on any device database table. It is initialized and reset to 0 on any data
    // reinitialization or download, and decrement to -1 on any new table record added to provide unique id across the database
    private int newTableRecordID;
    private String updateUser; //the user that his/her lotlist items are to be downloaded
    public SettingDetail() {}

    public SettingDetail(String server, String serverUrl, String updateData, String lastOperation, int newTableRecordID, String updateUser)
    {
        super();
        this.server = server;
        this.serverUrl = serverUrl;
        this.updateData = updateData;
        this.lastOperation = lastOperation;
        this.newTableRecordID = newTableRecordID;
        this.updateUser = updateUser;
    }

    public String getServer()
    {
        return server;
    }
    public void setServer(String server)
    {
        this.server = server;
    }

    public String getServerUrl()
    {
        return serverUrl;
    }
    public void setServerUrl(String serverUrl)
    {
        this.serverUrl = serverUrl;
    }

    public String getUpdateData()
    {
        return updateData;
    }
    public void setUpdateData(String updateData)
    {
        this.updateData = updateData;
    }

    public String getLastOperation()
    {
        return lastOperation;
    }
    public void setLastOperation(String lastOperation)
    {
        this.lastOperation = lastOperation;
    }

    public int getNewTableRecordID()
    {
        return newTableRecordID;
    }
    public void setNewTableRecordID(int newTableRecordID)
    {
        this.newTableRecordID = newTableRecordID;
    }

    public String getUpdateUser()
    {
        return updateUser;
    }
    public void setUpdateUser(String updateUser)
    {
        this.updateUser = updateUser;
    }
}
