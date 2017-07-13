package com.suntek.ibmsapp.page.camera;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.adapter.AreaListAdapter;
import com.suntek.ibmsapp.component.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 摄像机选择
 *
 * @author jimmy
 */
public class CameraChooseActivity extends BaseActivity implements AdapterView.OnItemClickListener
{
    @BindView(R.id.lv_area)
    ListView lvArea;

    private AreaListAdapter areaListAdapter;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_camera_choose;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        List<Map<String,Object>> areaList = new ArrayList<>();
        for(int i = 0; i < 10;i++)
        {
            Map<String,Object> map = new HashMap<>();
            areaList.add(map);
        }
        areaListAdapter = new AreaListAdapter(this,areaList);
        lvArea.setAdapter(areaListAdapter);
        lvArea.setOnItemClickListener(this);
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        finish();
    }
}
