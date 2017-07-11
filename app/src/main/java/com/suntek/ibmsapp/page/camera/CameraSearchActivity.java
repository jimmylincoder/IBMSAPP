package com.suntek.ibmsapp.page.camera;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.adapter.CameraSearchAdapter;
import com.suntek.ibmsapp.component.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 摄像头搜索
 *
 * @author jimmy
 */
public class CameraSearchActivity extends BaseActivity implements AdapterView.OnItemClickListener
{
    @BindView(R.id.lv_search_result)
    ListView lvSearchResult;

    private CameraSearchAdapter cameraSearchAdapter;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_camera_search;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        lvSearchResult.setOnItemClickListener(this);
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

    @OnClick(R.id.ll_btn_search)
    public void search(View view)
    {
        List<Map<String,Object>> cameraList = new ArrayList<>();
        for(int i = 0;i < 10;i++)
        {
            Map<String,Object> map = new HashMap<>();
            cameraList.add(map);
        }
        cameraSearchAdapter = new CameraSearchAdapter(this,cameraList);
        lvSearchResult.setAdapter(cameraSearchAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        Intent intent = new Intent(CameraSearchActivity.this,CameraPlayActivity.class);
        startActivity(intent);
        finish();
    }
}
