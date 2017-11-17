package com.suntek.ibmsapp.component.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.suntek.ibmsapp.component.AppManager;
import com.suntek.ibmsapp.component.core.ComponentEngine;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * activity base类
 *
 * @author jimmy
 */

public abstract class BaseActivity extends AppCompatActivity
{
    private Unbinder bind;

    public static Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context = this;
        //设置布局内容
        setContentView(getLayoutId());
        //初始化黄油刀控件绑定框架
        bind = ButterKnife.bind(this);
        //组件绑定
        ComponentEngine.bind(this);
        //初始化控件
        initViews(savedInstanceState);
        //初始化ToolBar
        initToolBar();
        AppManager.addActivity(this);
    }

    /**
     * 设置布局layout
     *
     * @return
     */
    public abstract int getLayoutId();

    /**
     * 初始化views
     *
     * @param savedInstanceState
     */
    public abstract void initViews(Bundle savedInstanceState);

    /**
     * 初始化toolbar
     */
    public abstract void initToolBar();

    /**
     * 加载数据
     */
    public void loadData()
    {
    }

    /**
     * 显示进度条
     */
    public void showProgressBar()
    {
    }

    /**
     * 隐藏进度条
     */
    public void hideProgressBar()
    {
    }

    /**
     * 初始化recyclerView
     */
    public void initRecyclerView()
    {
    }

    /**
     * 初始化refreshLayout
     */
    public void initRefreshLayout()
    {
    }

    /**
     * 设置数据显示
     */
    public void finishTask()
    {
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        bind.unbind();
    }
}
