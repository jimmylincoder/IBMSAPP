package com.suntek.ibmsapp.page.camera;

import android.os.Bundle;
import android.view.View;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;

import butterknife.OnClick;

/**
 * 录像下载
 *
 * @author jimmy
 */
public class CameraDownloadActivity extends BaseActivity
{
    @Override
    public int getLayoutId()
    {
        return R.layout.activity_camera_download;
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
