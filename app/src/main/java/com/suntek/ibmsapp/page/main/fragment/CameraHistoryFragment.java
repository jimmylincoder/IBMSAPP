package com.suntek.ibmsapp.page.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.adapter.CameraSearchAdapter;

import com.suntek.ibmsapp.component.base.BaseFragment;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.page.camera.CameraPlayActivity;
import com.suntek.ibmsapp.task.camera.CameraHistoryListTask;
import com.suntek.ibmsapp.widget.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * 历史查看视频列表
 *
 * @author jimmy
 */
public class CameraHistoryFragment extends BaseFragment implements AdapterView.OnItemClickListener,PullToRefreshBase.OnRefreshListener
{
    @BindView(R.id.ptr_history)
    PullToRefreshListView ptrHistory;

    private CameraSearchAdapter cameraSearchAdapter;

    private List<Camera> cameraList;

    @Override
    public int getLayoutId()
    {
        return R.layout.fragment_camear_history;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        cameraList = new ArrayList<>();
        cameraSearchAdapter = new CameraSearchAdapter(getActivity(),cameraList);
        ptrHistory.setAdapter(cameraSearchAdapter);
        ptrHistory.setOnItemClickListener(this);
        getCameraHistory();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        Intent intent = new Intent(getActivity(),CameraPlayActivity.class);
        Camera camera = cameraList.get(i - 1);
        intent.putExtra("cameraId",camera.getId());
        intent.putExtra("cameraName",camera.getName());
        startActivity(intent);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView)
    {
        String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

        // Update the LastUpdatedLabel
        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
        getCameraHistory();
    }

    private void getCameraHistory()
    {
        new CameraHistoryListTask(getActivity(),1)
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                if(result.getError() == null)
                {
                    cameraList = (List<Camera>) result.getResultData();
                    cameraSearchAdapter.setCameraList(cameraList);
                    cameraSearchAdapter.notifyDataSetChanged();
                    ptrHistory.onRefreshComplete();
                }
                else
                {
                    ToastHelper.getInstance(getActivity()).shortShowMessage(result.getError().getMessage());

                }
            }
        }.execute();
    }


}
