package com.suntek.ibmsapp.page.main.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseFragment;
import com.suntek.ibmsapp.page.main.fragment.adapter.PhotoFragmentAdapter;
import com.suntek.ibmsapp.page.main.fragment.photo.PhotoFragment;
import com.suntek.ibmsapp.page.main.fragment.photo.VideoFragment;
import com.suntek.ibmsapp.widget.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 相册界面
 *
 * @author jimmy
 */
public class PhotoListFragment extends BaseFragment
{
    @BindView(R.id.vp_content)
    ViewPager vpContent;

    @BindView(R.id.tv_photo)
    TextView tvPhoto;

    @BindView(R.id.tv_video)
    TextView tvVideo;

    @BindView(R.id.ll_title)
    LinearLayout llTitle;

    @BindView(R.id.ll_choose)
    LinearLayout llChoose;

    private List<Fragment> fragmentList = new ArrayList<>();

    private PhotoFragmentAdapter photoFragmentAdapter;

    private PopupWindow popupMenu;

    @Override
    public int getLayoutId()
    {
        return R.layout.fragment_photo;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        initViewPager();
    }

    private void initViewPager()
    {
        Fragment photoFragment = new PhotoFragment();
        Fragment videoFragment = new VideoFragment();
        fragmentList.add(photoFragment);
        fragmentList.add(videoFragment);

        photoFragmentAdapter = new PhotoFragmentAdapter(getFragmentManager(), fragmentList);
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
        if (popupMenu == null)
        {
            View view1 = getActivity().getLayoutInflater().inflate(R.layout.view_photo_menu, null);
            popupMenu = new PopupWindow(view1, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            popupMenu.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
            ImageView ivDelete = (ImageView) view1.findViewById(R.id.iv_del);
            ivDelete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ToastHelper.getInstance(getActivity()).shortShowMessage("删除");
                }
            });
        }
        else
        {
            popupMenu.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        }
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
            tvPhoto.setTextColor(getResources().getColor(R.color.green));
            tvVideo.setTextColor(getResources().getColor(R.color.black_80));
        }
        if (position == 1)
        {
            vpContent.setCurrentItem(position);
            tvVideo.setTextColor(getResources().getColor(R.color.green));
            tvPhoto.setTextColor(getResources().getColor(R.color.black_80));
        }
    }

    @OnClick(R.id.tv_cancel)
    public void cancel(View view)
    {
        popupMenu.dismiss();
        showHeader();
    }
}
