package com.suntek.ibmsapp.page.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.widget.GestureImageView.GestureImageView;
import com.suntek.ibmsapp.widget.UnityDialog;

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
                .Builder(getApplicationContext())
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
        Bitmap bitmap = BitmapFactory.decodeStream(fis);
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
        new UnityDialog(this)
                .setTitle("是否确认删除")
                .setHint("是否确认删除该照片")
                .setCancel("取消", new UnityDialog.OnCancelDialogListener()
                {
                    @Override
                    public void cancel(UnityDialog unityDialog)
                    {
                        unityDialog.dismiss();
                    }
                })
                .setConfirm("确定", new UnityDialog.OnConfirmDialogListener()
                {
                    @Override
                    public void confirm(UnityDialog unityDialog, String content)
                    {
                        File file = new File(photoPath);
                        if (file.exists())
                        {
                            file.delete();
                        }
                        unityDialog.dismiss();
                        finish();
                    }
                }).show();
    }
}
