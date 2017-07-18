package com.suntek.ibmsapp.model;

import java.io.Serializable;

/**
 * 用户实体
 *
 * @author jimmy
 */
public class User
{
    private String userCode;

    private String userName;

    private String password;

    public String getUserCode()
    {
        return userCode;
    }

    public void setUserCode(String userCode)
    {
        this.userCode = userCode;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
