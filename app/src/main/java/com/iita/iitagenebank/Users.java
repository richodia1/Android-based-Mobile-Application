package com.iita.iitagenebank;

import java.io.Serializable;

/**
 * Created by Simeon on 11/09/2015.
 */
public class Users implements Serializable
{
    private String username;
    private String password;
    private int loginStatus;
    private int id;

    public Users() {}

    public Users(String username, String password, int loginStatus, int id)
    {
        super();
        this.username = username;
        this.password = password;
        this.loginStatus = loginStatus;
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }

    public int getLoginStatus()
    {
        return loginStatus;
    }
    public void setLoginStatus(int loginStatus)
    {
        this.loginStatus = loginStatus;
    }

    public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }
}