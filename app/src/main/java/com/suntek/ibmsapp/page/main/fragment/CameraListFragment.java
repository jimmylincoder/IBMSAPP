package com.suntek.ibmsapp.page.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.adapter.CameraListAdapter;
import com.suntek.ibmsapp.component.Page;
import com.suntek.ibmsapp.component.base.BaseFragment;
import com.suntek.ibmsapp.component.core.Autowired;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.page.camera.CameraChooseActivity;
import com.suntek.ibmsapp.page.camera.CameraPlayActivity;
import com.suntek.ibmsapp.page.camera.CameraSearchActivity;
import com.suntek.ibmsapp.task.camera.CameraListTask;
import com.suntek.ibmsapp.util.SaveDataWithSharedHelper;
import com.suntek.ibmsapp.widget.ToastHelper;

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

    @BindView(R.id.ptr_camera_list)
    PullToRefreshListView ptrCameraList;

    @BindView(R.id.tv_area)
    TextView tvArea;

    @Autowired
    SaveDataWithSharedHelper sharedHelper;

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

        ptrCameraList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(getActivity(), CameraPlayActivity.class);
                Camera camera = cameraList.get(position - 1);
                Bundle bundle = new Bundle();
                bundle.putSerializable("camera", camera);
                intent.putExtras(bundle);
                intent.putExtra("cameraId", cameraList.get(position - 1).getId());
                intent.putExtra("cameraName", cameraList.get(position - 1).getName());
                startActivity(intent);
            }
        });

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.item_list_empty, null);
        ptrCameraList.setEmptyView(view);

        cameraList = new ArrayList<>();

        cameraListAdapter = new CameraListAdapter(getActivity(), cameraList);
        actualListView = ptrCameraList.getRefreshableView();
        actualListView.setAdapter(cameraListAdapter);

        areaId = sharedHelper.getString("choose_org_code");

        getCameraList(currentPage, true);
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
                        cameraList = newCameraList;
                    }
                    else
                    {
                        cameraList.addAll(newCameraList);
                    }
                    cameraListAdapter.setCameraList(cameraList);
                    cameraListAdapter.notifyDataSetChanged();
                    try{
                        ptrCameraList.onRefreshComplete();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
                else
                {
                    ToastHelper.getInstance(getActivity()).shortShowMessage(result.getError().getMessage());

                }
            }
        }.execute();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        tvArea.setText(sharedHelper.getString("choose_name"));
        String chooseAreaId = sharedHelper.getString("choose_org_code");
        if (!areaId.equals(chooseAreaId))
        {
            currentPage = 1;
            areaId = chooseAreaId;
            getCameraList(currentPage, true);
        }
    }

    @OnClick(R.id.ll_search_area)
    public void search(View view)
    {
        Intent intent = new Intent(getActivity(), CameraSearchActivity.class);
        startActivity(intent);
    }
}
