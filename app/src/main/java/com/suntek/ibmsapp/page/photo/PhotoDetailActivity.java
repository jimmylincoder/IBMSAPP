package com.suntek.ibmsapp.page.photo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.widget.GestureImageView.GestureImageView;
import com.suntek.ibmsapp.widget.ToastHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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

    private String photoPath;

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
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(context)
                .build();
        ImageLoader.getInstance().init(config);
        photoPath = getIntent().getStringExtra("photoPath");
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(photoPath);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        Bitmap bitmap  = BitmapFactory.decodeStream(fis);
       // ImageLoader.getInstance().displayImage("file://" + photoPath, givPhoto);
        givPhoto.setImageBitmap(bitmap);
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
        File file = new File(photoPath);
        if (file.exists())
        {
            file.delete();
        }else
        {
            ToastHelper.getInstance(this).shortShowMessage("文件不存在");
        }
        finish();
    }
}
