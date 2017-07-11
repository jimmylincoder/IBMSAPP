package com.suntek.ibmsapp.page.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseFragment;
import com.suntek.ibmsapp.page.login.UserLoginActivity;

import butterknife.OnClick;

/**
 * 我的界面
 *
 * @author jimmy
 */
public class MeFragment extends BaseFragment
{
    @Override
    public int getLayoutId()
    {
        return R.layout.fragment_me;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {

    }

    @OnClick(R.id.ll_login_out)
    public void loginout(View view)
    {
        Intent intent = new Intent(getActivity(), UserLoginActivity.class);
        startActivity(intent);
    }
}
