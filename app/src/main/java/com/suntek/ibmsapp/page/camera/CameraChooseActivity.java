package com.suntek.ibmsapp.page.camera;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.adapter.AreaListAdapter;

import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.component.cache.ACache;
import com.suntek.ibmsapp.model.Area;
import com.suntek.ibmsapp.task.area.AreaListTask;

import java.io.Serializable;
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

    private ACache aCache;

    private String chooseId = "";

    private String rootId = "";

    @BindView(R.id.tv_now_area)
    TextView tvNowArea;

    @BindView(R.id.ll_loading)
    LinearLayout llLoading;

    @BindView(R.id.ll_error)
    LinearLayout llError;

    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout srlRefresh;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_camera_choose;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        aCache = ACache.get(this);
        lvArea.setOnItemClickListener(this);
        initNowArea();
        initAreaListView(chooseId, true);
        srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                initAreaListView(chooseId, false);
            }
        });
        lvArea.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                boolean enable = false;
                if (lvArea != null && lvArea.getChildCount() > 0)
                {
                    boolean firstItemVisible = lvArea.getFirstVisiblePosition() == 0;
                    boolean topOfFirstItemVisible = lvArea.getChildAt(0).getTop() == 0;
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                srlRefresh.setEnabled(enable);
            }
        });
    }

    private void initNowArea()
    {
        llLoading.setVisibility(View.VISIBLE);
        lvArea.setVisibility(View.GONE);
        rootId = aCache.getAsString("root_id");
        chooseId = rootId;
        tvNowArea.setText(aCache.getAsString("choose_name"));
        areas = new ArrayList<>();
    }

    private void initAreaListView(String parentId, boolean isCache)
    {
        if (isCache)
        {
            List<Area> areaCache = (List<Area>) aCache.getAsObject(parentId);
            if (areaCache != null)
            {
                loadingView(false);
                areas = areaCache;
                areaListAdapter = new AreaListAdapter(CameraChooseActivity.this, areaCache);
                lvArea.setAdapter(areaListAdapter);
                return;
            }
        }

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
                            areas.clear();
                            if (parentId.equals(rootId))
                            {
                                Area area = (Area) aCache.getAsObject("root_area");
                                areas.add(area);
                                areas.addAll(newAreas);
                            }
                            else
                                areas = newAreas;
                            aCache.put(parentId, (Serializable) areas);
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
                if (srlRefresh != null)
                    srlRefresh.setRefreshing(false);
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
        initAreaListView(rootId, true);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        loadingView(true);
        String orgCode = areas.get(i).getId();
        chooseId = areas.get(i).getId();
        String name = areas.get(i).getName();
        aCache.put("choose_org_code", orgCode);
        aCache.put("choose_name", name);
        if (rootId.equals(orgCode))
        {
            finish();
        }
        else
        {
            areas.clear();
            initAreaListView(chooseId, true);
        }
    }

    private void loadingView(boolean isShow)
    {
        if (isShow)
        {
            llError.setVisibility(View.GONE);
            llLoading.setVisibility(View.VISIBLE);
            lvArea.setVisibility(View.GONE);
        }
        else
        {
            llError.setVisibility(View.GONE);
            llLoading.setVisibility(View.GONE);
            lvArea.setVisibility(View.VISIBLE);
        }
    }
}
