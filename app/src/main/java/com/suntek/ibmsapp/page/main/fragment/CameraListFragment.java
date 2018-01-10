package com.suntek.ibmsapp.page.main.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.adapter.CameraListAdapter;
import com.suntek.ibmsapp.component.Page;
import com.suntek.ibmsapp.component.base.BaseFragment;
import com.suntek.ibmsapp.component.cache.ACache;
import com.suntek.ibmsapp.component.core.Autowired;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.page.camera.CameraChooseActivity;
import com.suntek.ibmsapp.page.camera.CameraSearchActivity;
import com.suntek.ibmsapp.task.camera.CameraListTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 摄像机列表
 *
 * @author jimmy
 */
public class CameraListFragment extends BaseFragment
{
    private List<Camera> cameraList;

    private CameraListAdapter cameraListAdapter;

    private ListView actualListView;

    private ACache aCache;

    @BindView(R.id.ptr_camera_list)
    PullToRefreshListView ptrCameraList;

    @BindView(R.id.tv_area)
    TextView tvArea;

    @BindView(R.id.ll_loading)
    LinearLayout llLoading;

    @BindView(R.id.ll_retry)
    LinearLayout llRetry;

    private String areaId;

    //列表总页数
    private int totalPage;

    //当前页数，默认第1页
    private int currentPage = 1;

    @Override
    public int getLayoutId()
    {
        return R.layout.fragment_camera_list;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        init();
        initPtrListView();
        initCameraList();
    }

    private void initCameraList()
    {
        cameraList = (List<Camera>) aCache.getAsObject("camera_list");
        if (cameraList == null)
        {
            ptrCameraList.setVisibility(View.GONE);
            llLoading.setVisibility(View.VISIBLE);
            getCameraList(currentPage, true);
        }
        else
        {
            cameraListAdapter.setCameraList(cameraList);
            cameraListAdapter.notifyDataSetChanged();
        }
    }

    private void init()
    {
        aCache = ACache.get(getActivity());
        areaId = aCache.getAsString("choose_org_code");
    }

    /**
     * 初始化下拉刷新列表
     */
    private void initPtrListView()
    {
        ptrCameraList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        ptrCameraList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>()
        {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView)
            {
                String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                // Do work to refresh the list here.
                currentPage = 1;
                getCameraList(currentPage, true);
            }

        });



        ptrCameraList.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener()
        {
            @Override
            public void onLastItemVisible()
            {
                if (currentPage < totalPage)
                {
                    getCameraList(++currentPage, false);
                }
            }
        });

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.item_list_empty, null);
        ptrCameraList.setEmptyView(view);

        cameraList = new ArrayList<>();
        cameraListAdapter = new CameraListAdapter(getActivity(), cameraList);
        actualListView = ptrCameraList.getRefreshableView();
        actualListView.setAdapter(cameraListAdapter);
    }

    @OnClick(R.id.ll_choose_camera)
    public void chooseCamera(View view)
    {
        Intent intent = new Intent(getActivity(), CameraChooseActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_search)
    public void searchCamera(View view)
    {
        Intent intent = new Intent(getActivity(), CameraSearchActivity.class);
        startActivity(intent);
    }

    /**
     * 获取摄像机列表
     *
     * @param page      页数
     * @param isRefresh 是否为上拉刷新
     */
    private void getCameraList(int page, boolean isRefresh)
    {
        new CameraListTask(getActivity(), areaId, page)
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                if (result.getError() == null)
                {
                    Page<List<Camera>> cameraPage = (Page<List<Camera>>) result.getResultData();
                    List<Camera> newCameraList = cameraPage.getData();
                    totalPage = cameraPage.getTotalPage();

                    if (isRefresh)
                    {
                        aCache.put("camera_list", (Serializable) newCameraList);
                        cameraList = newCameraList;
                    }
                    else
                    {
                        cameraList.addAll(newCameraList);
                        aCache.put("camera_list", (Serializable) cameraList);
                    }
                    cameraListAdapter.setCameraList(cameraList);
                    cameraListAdapter.notifyDataSetChanged();

                    if (ptrCameraList != null)
                    {
                        ptrCameraList.setVisibility(View.VISIBLE);
                        ptrCameraList.onRefreshComplete();
                    }
                    if (llLoading != null)
                        llLoading.setVisibility(View.GONE);
                }
                else
                {
                    if (llLoading != null)
                        llLoading.setVisibility(View.GONE);
                    if (ptrCameraList != null)
                        ptrCameraList.onRefreshComplete();
                    if (llRetry != null)
                        llRetry.setVisibility(View.VISIBLE);
                }
            }
        }.execute();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        tvArea.setText(aCache.getAsString("choose_name"));
        String chooseAreaId = aCache.getAsString("choose_org_code");
        if (areaId == null || !areaId.equals(chooseAreaId))
        {
            currentPage = 1;
            areaId = chooseAreaId;
            ptrCameraList.setVisibility(View.GONE);
            llLoading.setVisibility(View.VISIBLE);
            getCameraList(currentPage, true);
        }
    }

    @OnClick(R.id.ll_search_area)
    public void search(View view)
    {
        Intent intent = new Intent(getActivity(), CameraSearchActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_retry)
    public void retry(View view)
    {
        llRetry.setVisibility(View.GONE);
        llLoading.setVisibility(View.VISIBLE);
        ptrCameraList.setVisibility(View.GONE);
        getCameraList(currentPage, true);
    }
}

