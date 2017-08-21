package com.suntek.ibmsapp.manager;

import com.suntek.ibmsapp.component.HttpResponse;
import com.suntek.ibmsapp.component.IbmsHttpEngine;
import com.suntek.ibmsapp.component.core.Autowired;
import com.suntek.ibmsapp.component.core.BaseComponent;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.model.User;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理类
 *
 * @author jimmy
 */
public class UserManager extends BaseComponent
{
    @Autowired
    IbmsHttpEngine ibmsHttpEngine;

    /**
     * 用户登录
     *
     * @param userName
     * @param password
     * @return
     */
    public User login(String userName,String password)
    {
        Map<String,Object> params = new HashMap<>();
        params.put("user_name",userName);
        params.put("password",password);

        HttpResponse response = ibmsHttpEngine.request("user.login",params);

        if(response.getCode() == HttpResponse.STATUS_SUCCESS)
        {
            Map<String,Object> content = response.getData();
            User user = User.generateByJson(content);
            return user;
        }
        else
        {
            throw new FHttpException(FHttpException.CODE_BUSINESS_ERROR,response.getErrorMessage());
        }
    }

}