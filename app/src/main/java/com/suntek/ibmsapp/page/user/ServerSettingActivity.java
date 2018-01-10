package com.suntek.ibmsapp.page.user;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.component.cache.ACache;
import com.suntek.ibmsapp.component.core.Config;
import com.suntek.ibmsapp.widget.ToastHelper;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 服务器ip设置
 *
 * @author jimmy
 */

public class ServerSettingActivity extends BaseActivity
{
    private ACache aCache;

    @BindView(R.id.et_server_ip)
    EditText etServerIp;

    @BindView(R.id.et_server_port)
    EditText etServerPort;

    @Config("http.ibms_url")
    private String ibmsUrl;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_server_setting;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        aCache = ACache.get(this);
        String serverIp = aCache.getAsString("server_ip");
        String serverPort = aCache.getAsString("server_port");

        if(serverIp == null && serverPort == null)
        {
            String str = ibmsUrl.substring(7);
            String[] strs = str.split("/");
            String[] ipAndPort = strs[0].split(":");
            serverIp = ipAndPort[0];
            serverPort = ipAndPort[1];
        }
        etServerIp.setText(serverIp);
        etServerPort.setText(serverPort);
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

    @OnClick(R.id.ll_save_setting)
    public void save(View view)
    {
        String serverIp = etServerIp.getText() + "";
        String serverPort = etServerPort.getText() + "";
        aCache.put("server_ip",serverIp);
        aCache.put("server_port",serverPort);
        ToastHelper.getInstance(this).shortShowMessage("保存设置成功");
    }
}
