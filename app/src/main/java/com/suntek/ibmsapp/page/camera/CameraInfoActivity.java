package com.suntek.ibmsapp.page.camera;

import android.os.Bundle;
import android.view.View;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;

import butterknife.OnClick;

/**
 * 摄像机详情界面
 *
 * @author jimmy
 */
public class CameraInfoActivity extends BaseActivity
{
    @Override
    public int getLayoutId()
    {
        return R.layout.activity_camera_info;
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
