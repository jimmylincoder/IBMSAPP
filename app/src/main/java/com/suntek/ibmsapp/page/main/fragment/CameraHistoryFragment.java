package com.suntek.ibmsapp.page.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.adapter.CameraHistoryAdapter;

import com.suntek.ibmsapp.component.Page;
import com.suntek.ibmsapp.component.base.BaseFragment;
import com.suntek.ibmsapp.component.cache.ACache;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.page.camera.CameraPlayActivity;
import com.suntek.ibmsapp.page.camera.CameraPlayHKActivity;
import com.suntek.ibmsapp.task.camera.CameraHistoryListTask;
import com.suntek.ibmsapp.widget.ToastHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 历史查看视频列表
 *
 * @author jimmy
 */
public class CameraHistoryFragment extends BaseFragment implements AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener
{
    @BindView(R.id.ptr_history)
    PullToRefreshListView ptrHistory;

    private CameraHistoryAdapter cameraHistoryAdapter;

    private List<Camera> cameraList;

    private int totalPage;

    private int currentPage = 1;

    private ACache aCache;

    @BindView(R.id.ll_loading)
    LinearLayout llLoading;

    @BindView(R.id.ll_retry)
    LinearLayout llRetry;

    @Override
    public int getLayoutId()
    {
        return R.layout.fragment_camear_history;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        aCache = ACache.get(getActivity());
        cameraList = new ArrayList<>();
        cameraHistoryAdapter = new CameraHistoryAdapter(getActivity(), cameraList);
        ptrHistory.setAdapter(cameraHistoryAdapter);
        ptrHistory.setOnItemClickListener(this);
        ptrHistory.setOnRefreshListener(this);
        ptrHistory.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener()
        {
            @Override
            public void onLastItemVisible()
            {
                if (currentPage < totalPage)
                {
                    getCameraHistory(++currentPage, false);
                }
            }
        });

        cameraList = (List<Camera>) aCache.getAsObject("camera_history");
        if (cameraList == null)
        {
            llLoading.setVisibility(View.VISIBLE);
            ptrHistory.setVisibility(View.GONE);
            getCameraHistory(currentPage, true);
        }
        else
        {
            cameraHistoryAdapter.setCameraList(cameraList);
            cameraHistoryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        Intent intent = new Intent(getActivity(), CameraPlayHKActivity.class);
        Camera camera = cameraList.get(i - 1);
        Bundle bundle = new Bundle();
        bundle.putSerializable("camera", camera);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView)
    {
        String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

        // Update the LastUpdatedLabel
        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
        currentPage = 1;
        getCameraHistory(currentPage, true);
    }

    private void getCameraHistory(int page, boolean isRefresh)
    {
        new CameraHistoryListTask(getActivity(), page)
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
                        cameraList = newCameraList;
                    }
                    else
                    {
                        cameraList.addAll(newCameraList);
                    }
                    aCache.put("camera_history", (Serializable) cameraList);
                    cameraHistoryAdapter.setCameraList(cameraList);
                    cameraHistoryAdapter.notifyDataSetChanged();
                    if (ptrHistory != null)
                    {
                        ptrHistory.setVisibility(View.VISIBLE);
                        ptrHistory.onRefreshComplete();
                    }
                    if (llLoading != null)
                        llLoading.setVisibility(View.GONE);
                    if (llRetry != null)
                        llRetry.setVisibility(View.GONE);
                }
                else
                {
                    // ToastHelper.getInstance(getActivity()).shortShowMessage(result.getError().getMessage());
                    if(ptrHistory != null)
                        ptrHistory.onRefreshComplete();
                    if (llRetry != null)
                        llRetry.setVisibility(View.VISIBLE);
                    if (llLoading != null)
                        llLoading.setVisibility(View.GONE);
                }
            }
        }.execute();
    }

    @OnClick(R.id.ll_retry)
    public void reTry(View view)
    {
        llRetry.setVisibility(View.GONE);
        llLoading.setVisibility(View.VISIBLE);
        ptrHistory.setVisibility(View.GONE);
        getCameraHistory(currentPage, true);
    }
}
