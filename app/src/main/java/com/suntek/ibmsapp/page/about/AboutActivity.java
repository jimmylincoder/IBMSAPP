package com.suntek.ibmsapp.page.about;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.util.VersionUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 关于详情界面
 *
 * @author jimmy
 */
public class AboutActivity extends BaseActivity
{
    @BindView(R.id.tv_version)
    TextView tvVersion;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_about;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
       String version =  VersionUtil.getAppInfo(this);
       tvVersion.setText("版本号: " + version);
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

}
