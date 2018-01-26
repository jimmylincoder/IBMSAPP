package com.suntek.ibmsapp.manager;

import com.suntek.ibmsapp.component.HttpResponse;
import com.suntek.ibmsapp.component.IbmsHttpEngine;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.component.cache.ACache;
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

    private ACache aCache = ACache.get(BaseActivity.context);

    /**
     * 用户登录
     *
     * @param userName
     * @param password
     * @return
     */
    public User login(String userName, String password)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("user_name", userName);
        params.put("password", password);

        HttpResponse response = ibmsHttpEngine.request("user.login", params);

        if (response.getCode() == HttpResponse.STATUS_SUCCESS)
        {
            Map<String, Object> content = (Map<String, Object>) response.getData().get("user");
            User user = User.generateByJson(content);
            aCache.put("userCode", user.getUserCode());
            aCache.put("userName", user.getUserName());
            return user;
        }
        else
        {
            throw new FHttpException(FHttpException.CODE_BUSINESS_ERROR, response.getErrorMessage());
        }
    }

    /**
     * 修改密码
     *
     * @param userName
     * @param newPassword
     * @param oldPassword
     */
    public void changePassword(String userName, String newPassword, String oldPassword)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("user_code", userName);
        params.put("new_password", newPassword);
        params.put("old_password", oldPassword);

        HttpResponse response = ibmsHttpEngine.request("user.change_password", params);

        if (response.getCode() == HttpResponse.STATUS_SUCCESS)
        {

        }
        else
        {
            throw new FHttpException(FHttpException.CODE_BUSINESS_ERROR, response.getErrorMessage());
        }
    }

    /**
     * 将用户数据保存至缓存
     *
     * @param user
     */
    public void saveUserByCache(User user)
    {
        aCache.put("user", user);
    }

    /**
     * 从缓存从取出用户数据
     */
    public User getUserByCache()
    {
        User user = (User) aCache.getAsObject("user");
        return user;
    }

}
