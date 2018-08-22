package com.suntek.ibmsapp.task.user;

import android.content.Context;

import com.suntek.ibmsapp.component.core.ComponentEngine;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.manager.UserManager;
import com.suntek.ibmsapp.task.base.BaseTask;

/**
 * 修改密码
 *
 * @author jimmy
 */
public class ChangePasswordTask extends BaseTask
{
    private UserManager userManager;

    private String userCode;

    private String newPassword;

    private String oldPassword;

    public ChangePasswordTask(Context context, String userCode, String newPassword, String oldPassword)
    {
        super(context);
        this.userCode = userCode;
        this.newPassword = newPassword;
        this.oldPassword = oldPassword;

        userManager = (UserManager) ComponentEngine.getInstance(UserManager.class);
    }

    @Override
    protected TaskResult doInBackground(Void... params)
    {
        try
        {
            userManager.changePassword(userCode, newPassword, oldPassword);
            return new TaskResult("", null);
        } catch (FHttpException e)
        {
            return new TaskResult(null, e);
        }
    }
}
