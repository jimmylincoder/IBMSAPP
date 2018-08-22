package com.suntek.ibmsapp.page.main;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.widget.Toast;


import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseFragmentActivity;
import com.suntek.ibmsapp.model.Version;
import com.suntek.ibmsapp.page.main.fragment.CameraHistoryFragment;
import com.suntek.ibmsapp.page.main.fragment.CameraListFragment;
import com.suntek.ibmsapp.page.main.fragment.MeFragment;
import com.suntek.ibmsapp.page.main.fragment.PhotoListFragment;
import com.suntek.ibmsapp.task.version.CheckVersionTask;
import com.suntek.ibmsapp.util.PermissionRequest;
import com.suntek.ibmsapp.util.VersionUtil;
import com.suntek.ibmsapp.widget.LoadingDialog;
import com.suntek.ibmsapp.widget.UnityDialog;

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

    //退出时的时间
    private long mExitTime;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_main;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        //申请读取内存权限
        PermissionRequest.verifyStoragePermissions(this);
        //检测更新版本
        checkUpdate();
        bnbTab.setMode(BottomNavigationBar.MODE_FIXED);
        bnbTab.addItem(new BottomNavigationItem(R.mipmap.ic_video_active, "视频")
                .setInactiveIcon(ContextCompat.getDrawable(this, R.mipmap.ic_video)))
                .addItem(new BottomNavigationItem(R.mipmap.ic_file_active, "相册")
                        .setInactiveIcon(ContextCompat.getDrawable(this, R.mipmap.ic_file)))
                .addItem(new BottomNavigationItem(R.mipmap.ic_history_active, "历史")
                        .setInactiveIcon(ContextCompat.getDrawable(this, R.mipmap.ic_history)))
                .addItem(new BottomNavigationItem(R.mipmap.ic_account_active, "我的")
                        .setInactiveIcon(ContextCompat.getDrawable(this, R.mipmap.ic_account)))
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {

            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit()
    {
        if ((System.currentTimeMillis() - mExitTime) > 2000)
        {
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        }
        else
        {
            finish();
            System.exit(0);
        }
    }

    private void checkUpdate()
    {
        new CheckVersionTask(this, VersionUtil.getAppInfo(this))
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                if (result.getError() == null)
                {
                    Version version = (Version) result.getResultData();
                    String isUpdate = version.getIsUpdate();
                    if ("0".equals(isUpdate))
                    {

                    }
                    else
                    {
                        new UnityDialog(MainActivity.this)
                                .setTitle("是否前往更新版本")
                                .setHint("当前版本:" + VersionUtil.getAppInfo(MainActivity.this) + "\n" +
                                        "更新版本:" + version.getVersionNum() + "\n" +
                                        "更新内容:" + version.getUpdateContent())
                                .setConfirm("确定", new UnityDialog.OnConfirmDialogListener()
                                {
                                    @Override
                                    public void confirm(UnityDialog unityDialog, String content)
                                    {
                                        unityDialog.dismiss();
                                        Intent intent = new Intent();
                                        intent.setAction("android.intent.action.VIEW");
                                        Uri uri = Uri.parse(version.getDownloadAddress());
                                        intent.setData(uri);
                                        startActivity(intent);
                                    }
                                })
                                .setCancel("取消", new UnityDialog.OnCancelDialogListener()
                                {
                                    @Override
                                    public void cancel(UnityDialog unityDialog)
                                    {
                                        unityDialog.dismiss();
                                    }
                                }).show();
                    }
                }
            }
        }.execute();
    }
}
