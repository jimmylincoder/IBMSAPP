package com.suntek.ibmsapp.page.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;


import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.page.main.fragment.CameraHistoryFragment;
import com.suntek.ibmsapp.page.main.fragment.CameraListFragment;
import com.suntek.ibmsapp.page.main.fragment.MeFragment;

import java.util.ArrayList;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener
{

    private ArrayList<Fragment> fragments;
    @BindView(R.id.bnb_tab)
    BottomNavigationBar bnbTab;
    int lastSelectedPosition = 0;

    private CameraListFragment cameraListFragment;
    private CameraHistoryFragment cameraHistoryFragment;
    private MeFragment meFragment;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_main;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        bnbTab.setMode(BottomNavigationBar.MODE_FIXED);
        bnbTab.addItem(new BottomNavigationItem(R.mipmap.ic_tv_play,"视频").setActiveColor(R.color.orange))
                .addItem(new BottomNavigationItem(R.mipmap.ic_history,"历史").setActiveColor(R.color.orange))
                .addItem(new BottomNavigationItem(R.mipmap.ic_account,"我的").setActiveColor(R.color.orange))
                .setFirstSelectedPosition(lastSelectedPosition)
                .initialise();;


        bnbTab.setTabSelectedListener(this);
        setDefaultFragment();
    }

    private void setDefaultFragment()
    {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        cameraListFragment = new CameraListFragment();
        transaction.replace(R.id.fl_content,cameraListFragment);
        transaction.commit();
    }

    @Override
    public void initToolBar()
    {
    }

    @Override
    public void onTabSelected(int position)
    {
        FragmentManager fm = this.getFragmentManager();
        FragmentTransaction transation = fm.beginTransaction();
        switch (position)
        {
            case 0:
                if(cameraListFragment == null)
                {
                    cameraListFragment = new CameraListFragment();
                }
                transation.replace(R.id.fl_content,cameraListFragment);
                break;

            case 1:
                if(cameraHistoryFragment == null)
                {
                    cameraHistoryFragment = new CameraHistoryFragment();
                }
                transation.replace(R.id.fl_content,cameraHistoryFragment);
                break;

            case 2:
                if(meFragment == null)
                {
                    meFragment = new MeFragment();
                }
                transation.replace(R.id.fl_content,meFragment);
                break;
            default:
                break;
        }

        transation.commit();
    }

    @Override
    public void onTabUnselected(int position)
    {

    }

    @Override
    public void onTabReselected(int position)
    {

    }
}
