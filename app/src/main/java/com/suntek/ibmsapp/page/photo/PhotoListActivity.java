package com.suntek.ibmsapp.page.photo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.adapter.PhotoListAdapter;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.model.Photo;
import com.suntek.ibmsapp.page.main.fragment.adapter.PhotoFragmentAdapter;
import com.suntek.ibmsapp.page.main.fragment.photo.PhotoFragment;
import com.suntek.ibmsapp.page.main.fragment.photo.VideoFragment;
import com.suntek.ibmsapp.util.DateUtil;
import com.suntek.ibmsapp.util.PermissionRequest;
import com.suntek.ibmsapp.widget.NoScrollViewPager;
import com.suntek.ibmsapp.widget.UnityDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 相册列表
 *
 * @author jimmy
 */
public class PhotoListActivity extends BaseActivity
{
    @BindView(R.id.vp_content)
    NoScrollViewPager vpContent;

    @BindView(R.id.tv_photo)
    TextView tvPhoto;

    @BindView(R.id.tv_video)
    TextView tvVideo;

    @BindView(R.id.ll_title)
    LinearLayout llTitle;

    @BindView(R.id.ll_choose)
    LinearLayout llChoose;

    @BindView(R.id.iv_photo_choose)
    ImageView ivPhotoChoose;

    @BindView(R.id.iv_video_choose)
    ImageView ivVideoChoose;

    private int nowPosition = 0;

    private List<Fragment> fragmentList = new ArrayList<>();

    private PhotoFragmentAdapter photoFragmentAdapter;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_photo_list;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        initViewPager();
    }

    @Override
    public void initToolBar()
    {

    }

    private void initViewPager()
    {
        Fragment photoFragment = new PhotoFragment();
        Fragment videoFragment = new VideoFragment();
        fragmentList.add(photoFragment);
        fragmentList.add(videoFragment);

        photoFragmentAdapter = new PhotoFragmentAdapter(getSupportFragmentManager(), fragmentList);
        vpContent.setAdapter(photoFragmentAdapter);
        vpContent.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                nowPosition = position;
                pageChange(position);
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
    }

    @OnClick(R.id.tv_photo)
    public void photo(View view)
    {
        pageChange(0);
    }

    @OnClick(R.id.tv_video)
    public void video(View view)
    {
        pageChange(1);
    }

    @OnClick(R.id.iv_edit)
    public void edit(View view)
    {
        showHeader();
        vpContent.setNoScroll(true);
        if (nowPosition == 0)
            ((PhotoFragment) fragmentList.get(nowPosition)).setEdit(true);
        else
            ((VideoFragment) fragmentList.get(nowPosition)).setEdit(true);
    }

    private void showHeader()
    {
        if (llTitle.isShown())
        {
            llTitle.setVisibility(View.GONE);
            llChoose.setVisibility(View.VISIBLE);
        }
        else
        {
            llTitle.setVisibility(View.VISIBLE);
            llChoose.setVisibility(View.GONE);
        }
    }

    private void pageChange(int position)
    {
        if (position == 0)
        {
            vpContent.setCurrentItem(position);
            ivPhotoChoose.setVisibility(View.VISIBLE);
            ivVideoChoose.setVisibility(View.GONE);
        }
        if (position == 1)
        {
            vpContent.setCurrentItem(position);
            ivPhotoChoose.setVisibility(View.GONE);
            ivVideoChoose.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.tv_cancel)
    public void cancel(View view)
    {
        showNormal();
    }

    @OnClick(R.id.tv_all)
    public void selectAll(View view)
    {
        if (nowPosition == 0)
            ((PhotoFragment) fragmentList.get(0)).selecteAll();
        else
            ((VideoFragment) fragmentList.get(1)).selecteAll();
    }

    @OnClick(R.id.ll_back)
    public void back(View view)
    {
        finish();
    }

    public void showNormal()
    {
        if (nowPosition == 0)
        {
            ((PhotoFragment) fragmentList.get(nowPosition)).setEdit(false);
            ((PhotoFragment) fragmentList.get(nowPosition)).clearChoose();
        }
        else
        {
            ((VideoFragment) fragmentList.get(nowPosition)).setEdit(false);
            ((VideoFragment) fragmentList.get(nowPosition)).clearChoose();
        }
        vpContent.setNoScroll(false);
        showHeader();
    }
}
