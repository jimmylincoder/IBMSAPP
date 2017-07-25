package com.suntek.ibmsapp.page.main.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.adapter.CameraListAdapter;
import com.suntek.ibmsapp.component.HttpRequest;
import com.suntek.ibmsapp.component.HttpResponse;
import com.suntek.ibmsapp.component.RequestBody;
import com.suntek.ibmsapp.component.base.BaseFragment;
import com.suntek.ibmsapp.component.core.Autowired;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.network.RetrofitHelper;
import com.suntek.ibmsapp.page.camera.CameraChooseActivity;
import com.suntek.ibmsapp.page.camera.CameraListActivity;
import com.suntek.ibmsapp.page.camera.CameraPlayActivity;
import com.suntek.ibmsapp.page.camera.CameraSearchActivity;
import com.suntek.ibmsapp.util.SaveDataWithSharedHelper;
import com.suntek.ibmsapp.widget.ToastHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 摄像机列表
 *
 * @author jimmy
 */
public class CameraListFragment extends BaseFragment
{
    private List<Map<String,Object>> cameraList;

    private CameraListAdapter cameraListAdapter;

    private ListView actualListView;

    @BindView(R.id.ptr_camera_list)
    PullToRefreshListView ptrCameraList;

    @BindView(R.id.tv_area)
    TextView tvArea;

    @Autowired
    SaveDataWithSharedHelper sharedHelper;

    private String areaId;

    @Override
    public int getLayoutId()
    {
        return R.layout.fragment_camera_list;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
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
                getCameraList();
            }
        });

        ptrCameraList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(getActivity(),CameraPlayActivity.class);
                Map<String,Object> camera = cameraList.get(position - 1);
                intent.putExtra("cameraId",camera.get("id") + "");
                intent.putExtra("cameraName",camera.get("name") + "");
                startActivity(intent);
            }
        });

        cameraList = new ArrayList<>();

        cameraListAdapter = new CameraListAdapter(getActivity(),cameraList);
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

    private void getCameraList()
    {
        HttpRequest request = null;
        try
        {
            request = new RequestBody()
                    .putParams("page","1",false,"")
                    .putParams("area_id",areaId,false,"")
                    .build();
        }catch (Exception e)
        {
            ToastHelper.getInstance(getActivity()).shortShowMessage(e.getMessage());
        }

        RetrofitHelper.getCameraApi()
                .list(request)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<HttpResponse>()
                {
                    @Override
                    public void call(HttpResponse listHttpResponse)
                    {
                        if(listHttpResponse.getCode() == HttpResponse.STATUS_SUCCESS)
                        {
                            cameraList = (List) listHttpResponse.getData().get("camera_list");
                            cameraList.addAll(cameraList);
                            cameraListAdapter.setCameraList(cameraList);
                            cameraListAdapter.notifyDataSetChanged();
                        }
                        else
                        {
                            ToastHelper.getInstance(getActivity()).shortShowMessage(listHttpResponse.getErrorMessage());
                        }
                        ptrCameraList.onRefreshComplete();
                    }
                }, new Action1<Throwable>()
                {
                    @Override
                    public void call(Throwable throwable)
                    {
                        ptrCameraList.onRefreshComplete();
                    }
                });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        tvArea.setText(sharedHelper.getString("choose_name"));
        areaId = sharedHelper.getString("choose_org_code");
        getCameraList();
    }
}
