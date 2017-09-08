package com.suntek.ibmsapp.page.main.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseFragment;
import com.suntek.ibmsapp.page.main.fragment.adapter.PhotoFragmentAdapter;
import com.suntek.ibmsapp.page.main.fragment.photo.PhotoFragment;
import com.suntek.ibmsapp.page.main.fragment.photo.VideoFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 相册界面
 *
 * @author jimmy
 */
public class PhotoListFragment extends BaseFragment
{
    @BindView(R.id.vp_content)
    ViewPager vpContent;

    private List<Fragment> fragmentList = new ArrayList<>();

    private PhotoFragmentAdapter photoFragmentAdapter;

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

        photoFragmentAdapter = new PhotoFragmentAdapter(getFragmentManager(),fragmentList);
        vpContent.setAdapter(photoFragmentAdapter);
    }
}
