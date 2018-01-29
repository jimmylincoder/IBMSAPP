package com.suntek.ibmsapp.model;

import java.io.Serializable;
import java.util.Map;

/**
 * 用户实体
 *
 * @author jimmy
 */
public class User implements Serializable
{
    private String userCode;

    private String userName;

    private String deptCode;

    private String deptName;

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

    public String getDeptCode()
    {
        return deptCode;
    }

    public void setDeptCode(String deptCode)
    {
        this.deptCode = deptCode;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public static User generateByJson(Map<String,Object> content)
    {
        User user = new User();
        user.setUserName((String) content.get("user_name"));
        user.setUserCode((String) content.get("user_code"));
        user.setDeptCode((String) content.get("dept_code"));
        user.setDeptName((String) content.get("dept_name"));
        return user;
    }
}
