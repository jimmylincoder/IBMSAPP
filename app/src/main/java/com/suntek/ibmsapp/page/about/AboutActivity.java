package com.suntek.ibmsapp.page.about;

import android.os.Bundle;
import android.view.View;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;

import butterknife.OnClick;

/**
 * 关于详情界面
 *
 * @author jimmy
 */
public class AboutActivity extends BaseActivity
{
    @Override
    public int getLayoutId()
    {
        return R.layout.activity_about;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {

    }

    @Override
    public void initToolBar()
    {

    }

    @OnClick(R.id.ll_back)
    public void back(View view)
    {
        finish();
    }

}
