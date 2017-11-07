package com.suntek.ibmsapp.page.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.model.Photo;
import com.suntek.ibmsapp.widget.GestureImageView.GestureImageView;
import com.suntek.ibmsapp.widget.UnityDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 查看相片界面
 *
 * @author jimmy
 */
public class PhotoDetailActivity extends BaseActivity
{
//    @BindView(R.id.giv_detail)
//    GestureImageView givPhoto;

    @BindView(R.id.vp_photo)
    ViewPager vpPhoto;

    private String photoPath;

    private List<String> pathList;

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
        pathList = new ArrayList<>();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getApplicationContext())
                .build();
        ImageLoader.getInstance().init(config);
        photoPath = getIntent().getStringExtra("photoPath");
        List<Photo> paths = (List<Photo>) getIntent().getSerializableExtra("paths");
        for (Photo photo : paths)
        {
            pathList.addAll(photo.getPhotoPaths());
        }

        vpPhoto.setAdapter(new PhotoPagerAdapter(getSupportFragmentManager()));
        vpPhoto.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                photoPath = pathList.get(position);
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
        int index = pathList.indexOf(photoPath);
        vpPhoto.setCurrentItem(index);
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

    private class PhotoPagerAdapter extends FragmentStatePagerAdapter
    {
        public PhotoPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            return PhotoPagerFragment.newInstance(pathList.get(position));
        }

        @Override
        public int getCount()
        {
            return pathList.size();
        }
    }
}
