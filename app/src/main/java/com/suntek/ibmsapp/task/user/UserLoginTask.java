package com.suntek.ibmsapp.task.user;

import android.content.Context;

import com.suntek.ibmsapp.component.core.ComponentEngine;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.manager.UserManager;
import com.suntek.ibmsapp.model.User;
import com.suntek.ibmsapp.task.base.BaseTask;

/**
 * 用户登录任务
 *
 * @author jimmy
 */
public class UserLoginTask extends BaseTask
{
    private UserManager userManager;

    private String userName;

    private String password;

    public UserLoginTask(Context context,String userName,String password)
    {
        super(context);

        this.userName = userName;
        this.password = password;

        userManager = (UserManager) ComponentEngine.getInstance(UserManager.class);
    }

    @Override
    protected TaskResult doInBackground(Void... params)
    {
        try
        {
            User user = userManager.login(userName,password);
            return new TaskResult(user,null);
        }
        catch (FHttpException e)
        {
            return new TaskResult("",e);
        }
    }
}
