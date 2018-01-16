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
import com.suntek.ibmsapp.widget.NoScrollGridView;
import com.suntek.ibmsapp.widget.NoScrollViewPager;
import com.suntek.ibmsapp.widget.ToastHelper;
import com.suntek.ibmsapp.widget.UnityDialog;

import java.io.File;
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

    Fragment photoFragment = null;

    Fragment videoFragment = null;

    private int nowPosition = 0;

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
        //initViewPager();
        photoFragment = new PhotoFragment();
        videoFragment = new VideoFragment();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        initViewPager();
    }

    private void initViewPager()
    {
        if (!fragmentList.contains(photoFragment))
            fragmentList.add(photoFragment);
        if (!fragmentList.contains(videoFragment))
            fragmentList.add(videoFragment);

        if (photoFragmentAdapter == null)
            photoFragmentAdapter = new PhotoFragmentAdapter(getFragmentManager(), fragmentList);
        vpContent.setAdapter(photoFragmentAdapter);
        pageChange(nowPosition);
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
        if (popupMenu == null)
        {
            View view1 = getActivity().getLayoutInflater().inflate(R.layout.view_photo_menu, null);
            popupMenu = new PopupWindow(view1, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            popupMenu.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
            LinearLayout llDelete = (LinearLayout) view1.findViewById(R.id.ll_del);
            llDelete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    List<String> paths = new ArrayList<String>();
                    if (nowPosition == 0)
                        paths = ((PhotoFragment) fragmentList.get(0)).getSelectedPath();
                    else
                        paths = ((VideoFragment) fragmentList.get(1)).getSelectedPath();

                    List<String> finalPaths = paths;
                    new UnityDialog(getActivity())
                            .setTitle("是否确认删除")
                            .setHint("是否确认删除选中照片")
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
                                    for (String path : finalPaths)
                                    {
                                        File file = new File(path);
                                        if (file.exists())
                                        {
                                            file.delete();
                                        }
                                    }
                                    if (nowPosition == 0)
                                        ((PhotoFragment) fragmentList.get(nowPosition)).update();
                                    else
                                        ((VideoFragment) fragmentList.get(nowPosition)).update();
                                    unityDialog.dismiss();
                                    showNormal();
                                }
                            }).show();
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
        popupMenu.dismiss();
        showHeader();
    }
}
