package com.suntek.ibmsapp.component.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.suntek.ibmsapp.component.core.ComponentEngine;
import com.trello.rxlifecycle.components.RxFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 *  fragment基类
 */
public abstract class BaseFragment extends RxFragment
{
    private Unbinder bind;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(getLayoutId(),container,false);
        bind = ButterKnife.bind(this,view);
        ComponentEngine.bind(this);
        initViews(savedInstanceState);
        return view;
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

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        bind.unbind();
    }
}
