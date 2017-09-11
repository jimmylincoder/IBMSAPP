package com.suntek.ibmsapp.page.photo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.widget.GestureImageView.GestureImageView;
import com.suntek.ibmsapp.widget.ToastHelper;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 查看相片界面
 *
 * @author jimmy
 */
public class PhotoDetailActivity extends BaseActivity
{
    @BindView(R.id.giv_detail)
    GestureImageView givPhoto;


    @Override
    public int getLayoutId()
    {
        return R.layout.activity_photo_detail;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        initData();
    }

    private void initData()
    {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
        ImageLoader.getInstance().init(config);
        String photoPath = getIntent().getStringExtra("photoPath");
        ImageLoader.getInstance().displayImage("file://" + photoPath, givPhoto);
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

    @OnClick(R.id.iv_del)
    public void delete(View view)
    {
        ToastHelper.getInstance(this).shortShowMessage("删除");
    }
}
