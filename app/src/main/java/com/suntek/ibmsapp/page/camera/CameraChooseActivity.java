package com.suntek.ibmsapp.page.camera;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.adapter.AreaListAdapter;

import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.component.core.Autowired;
import com.suntek.ibmsapp.model.Area;
import com.suntek.ibmsapp.task.area.AreaListTask;
import com.suntek.ibmsapp.util.SaveDataWithSharedHelper;
import com.suntek.ibmsapp.widget.ToastHelper;

import java.util.ArrayList;
import java.util.List;

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

    private List<Area> areas;

    @Autowired
    SaveDataWithSharedHelper sharedHelper;

    @BindView(R.id.tv_now_area)
    TextView tvNowArea;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_camera_choose;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        lvArea.setOnItemClickListener(this);
        initNowArea();
        initAreaListView("1");
    }

    private void initNowArea()
    {
        tvNowArea.setText(sharedHelper.getString("choose_name"));
        areas = new ArrayList<>();
        Area area = new Area();
        area.setId("1");
        area.setOgrCode("01");
        area.setName("华侨城中心小区");
        areas.add(area);
    }

    private void initAreaListView(String parentId)
    {
        new AreaListTask(this,parentId)
        {

            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                if(result.getError() == null)
                {
                    areas = (List<Area>) result.getResultData();
                    if(!areas.isEmpty())
                    {
                        areaListAdapter = new AreaListAdapter(CameraChooseActivity.this, areas);
                        lvArea.setAdapter(areaListAdapter);
                    }
                    else
                    {
                        finish();
                    }
                }
                else
                {
                    ToastHelper.getInstance(CameraChooseActivity.this).shortShowMessage(result.getError().getMessage());

                }
            }
        }.execute();
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

    @OnClick(R.id.ll_now_choose)
    public void nowArea(View view)
    {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        String orgCode = (String) areas.get(i).getOgrCode();
        String id = (String) areas.get(i).getId();
        String name = (String) areas.get(i).getName();
        sharedHelper.save("choose_org_code", orgCode);
        sharedHelper.save("choose_name", name);
        if("01".equals(orgCode))
        {
            finish();
        }else
        {
            try
            {
                areas.clear();
                initAreaListView(id);
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }
}
