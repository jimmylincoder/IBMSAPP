package com.suntek.ibmsapp.page.camera;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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

    @BindView(R.id.ll_loading)
    LinearLayout llLoading;

    @BindView(R.id.ll_error)
    LinearLayout llError;

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
        llLoading.setVisibility(View.VISIBLE);
        lvArea.setVisibility(View.GONE);
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
        new AreaListTask(this, parentId)
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                if (result.getError() == null)
                {
                    List<Area> newAreas = (List<Area>) result.getResultData();
                    if (llLoading != null)
                        llLoading.setVisibility(View.GONE);
                    if (llError != null)
                        llError.setVisibility(View.GONE);
                    if (lvArea != null)
                        lvArea.setVisibility(View.VISIBLE);
                    if (!newAreas.isEmpty())
                    {
                        if (lvArea != null)
                        {
                            areas.addAll(newAreas);
                            areaListAdapter = new AreaListAdapter(CameraChooseActivity.this, areas);
                            lvArea.setAdapter(areaListAdapter);
                        }
                    }
                    else
                    {
                        finish();
                    }
                }
                else
                {
                    //ToastHelper.getInstance(CameraChooseActivity.this).shortShowMessage(result.getError().getMessage());
                    if (llError != null)
                        llError.setVisibility(View.VISIBLE);
                    if (llLoading != null)
                        llLoading.setVisibility(View.GONE);
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

    @OnClick(R.id.ll_error)
    public void reTry(View view)
    {
        llError.setVisibility(View.GONE);
        llLoading.setVisibility(View.VISIBLE);
        lvArea.setVisibility(View.GONE);
        initAreaListView("1");
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        String orgCode = areas.get(i).getOgrCode();
        String id = areas.get(i).getId();
        String name = areas.get(i).getName();
        sharedHelper.save("choose_org_code", orgCode);
        sharedHelper.save("choose_name", name);
        if ("01".equals(orgCode))
        {
            finish();
        }
        else
        {
            areas.clear();
            initAreaListView(id);
        }
    }
}
