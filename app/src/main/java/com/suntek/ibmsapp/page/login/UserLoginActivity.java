package com.suntek.ibmsapp.page.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.page.main.MainActivity;

import butterknife.OnClick;

/**
 * 用户登录界面
 *
 * @author jimmy
 */
public class UserLoginActivity extends BaseActivity
{
    @Override
    public int getLayoutId()
    {
        return R.layout.activity_user_login;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {

    }

    @Override
    public void initToolBar()
    {

    }

    @OnClick(R.id.ll_login)
    public void login(View view)
    {
        Intent intent = new Intent(UserLoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
