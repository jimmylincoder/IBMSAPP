package com.suntek.ibmsapp.page.main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseFragmentActivity;
import com.suntek.ibmsapp.page.main.fragment.CameraHistoryFragment;
import com.suntek.ibmsapp.page.main.fragment.CameraListFragment;
import com.suntek.ibmsapp.page.main.fragment.MeFragment;
import com.suntek.ibmsapp.page.main.fragment.PhotoListFragment;

import java.util.ArrayList;

import butterknife.BindView;

public class MainActivity extends BaseFragmentActivity implements BottomNavigationBar.OnTabSelectedListener
{

    private ArrayList<Fragment> fragments;
    @BindView(R.id.bnb_tab)
    BottomNavigationBar bnbTab;
    int lastSelectedPosition = 0;

    private CameraListFragment cameraListFragment;
    private CameraHistoryFragment cameraHistoryFragment;
    private MeFragment meFragment;
    private PhotoListFragment photoListFragment;

    private FragmentTransaction transaction;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_main;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        bnbTab.setMode(BottomNavigationBar.MODE_FIXED);
        bnbTab.addItem(new BottomNavigationItem(R.mipmap.ic_tv_play, "视频").setActiveColor(R.color.orange))
                .addItem(new BottomNavigationItem(R.mipmap.ic_photo, "相册").setActiveColor(R.color.orange))
                .addItem(new BottomNavigationItem(R.mipmap.ic_history, "历史").setActiveColor(R.color.orange))
                .addItem(new BottomNavigationItem(R.mipmap.ic_account, "我的").setActiveColor(R.color.orange))
                .setFirstSelectedPosition(lastSelectedPosition)
                .initialise();
        ;


        bnbTab.setTabSelectedListener(this);
        setDefaultFragment();
    }

    private void setDefaultFragment()
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        cameraListFragment = new CameraListFragment();
        transaction.replace(R.id.fl_content, cameraListFragment);
        transaction.commit();
    }

    @Override
    public void initToolBar()
    {
    }

    @Override
    public void onTabSelected(int position)
    {
        FragmentManager fm = this.getSupportFragmentManager();
        transaction = fm.beginTransaction();
        hideFragment(transaction);
        switch (position)
        {
            case 0:
                if (cameraListFragment == null)
                {
                    cameraListFragment = new CameraListFragment();
                    transaction.add(R.id.fl_content, cameraListFragment);
                }
                else
                {
                    transaction.show(cameraListFragment);
                }
                //此方法会导致fragment销灭
                //transaction.replace(R.id.fl_content,cameraListFragment);
                break;
            case 1:
                if (photoListFragment == null)
                {
                    photoListFragment = new PhotoListFragment();
                    transaction.add(R.id.fl_content, photoListFragment);
                }
                else
                {
                    transaction.show(photoListFragment);
                }
                break;
            case 2:
                if (cameraHistoryFragment == null)
                {
                    cameraHistoryFragment = new CameraHistoryFragment();
                    transaction.add(R.id.fl_content, cameraHistoryFragment);
                }
                else
                {
                    transaction.show(cameraHistoryFragment);
                }
                //transaction.replace(R.id.fl_content,cameraHistoryFragment);
                break;

            case 3:
                if (meFragment == null)
                {
                    meFragment = new MeFragment();
                    transaction.add(R.id.fl_content, meFragment);
                }
                else
                {
                    transaction.show(meFragment);
                }
                //transaction.replace(R.id.fl_content,meFragment);
                break;
            default:
                break;
        }

        transaction.commit();
    }

    /**
     * 隐藏其他fragment,如果不隐藏将一直累加
     *
     * @param transaction
     */
    private void hideFragment(FragmentTransaction transaction)
    {
        if (cameraListFragment != null)
        {
            transaction.hide(cameraListFragment);
        }

        if (cameraHistoryFragment != null)
        {
            transaction.hide(cameraHistoryFragment);
        }

        if (meFragment != null)
        {
            transaction.hide(meFragment);
        }

        if (photoListFragment != null)
        {
            transaction.hide(photoListFragment);
        }
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
