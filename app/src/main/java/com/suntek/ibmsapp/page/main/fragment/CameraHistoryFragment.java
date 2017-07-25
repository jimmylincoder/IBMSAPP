package com.suntek.ibmsapp.page.main.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.adapter.CameraSearchAdapter;
import com.suntek.ibmsapp.component.HttpRequest;
import com.suntek.ibmsapp.component.HttpResponse;
import com.suntek.ibmsapp.component.RequestBody;
import com.suntek.ibmsapp.component.base.BaseFragment;
import com.suntek.ibmsapp.network.RetrofitHelper;
import com.suntek.ibmsapp.page.camera.CameraPlayActivity;
import com.suntek.ibmsapp.page.camera.CameraSearchActivity;
import com.suntek.ibmsapp.widget.ToastHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

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

    private List<Map<String,Object>> cameraList;

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
        Map<String,Object> camera = cameraList.get(i - 1);
        intent.putExtra("cameraId",camera.get("id") + "");
        intent.putExtra("cameraName",camera.get("name") + "");
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
        HttpRequest request = null;
        try
        {
            request = new RequestBody()
                    .putParams("page","1",false,"")
                    .build();
        } catch (Exception e)
        {
            ToastHelper.getInstance(getActivity()).shortShowMessage(e.getMessage());
        }
        RetrofitHelper.getCameraApi()
                .history(request)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<HttpResponse>()
                {
                    @Override
                    public void call(HttpResponse httpResponse)
                    {
                        if (httpResponse.getCode() == HttpResponse.STATUS_SUCCESS)
                        {
                            cameraList = (List) httpResponse.getData().get("camera_list");
                            cameraSearchAdapter.setCameraList(cameraList);
                            cameraSearchAdapter.notifyDataSetChanged();
                            ptrHistory.onRefreshComplete();
                        }
                    }
                }, new Action1<Throwable>()
                {
                    @Override
                    public void call(Throwable throwable)
                    {
                        ptrHistory.onRefreshComplete();
                    }
                });
    }


}
