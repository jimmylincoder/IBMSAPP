package com.suntek.ibmsapp.page.camera;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.adapter.CameraListAdapter;
import com.suntek.ibmsapp.component.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 摄像机列表
 *
 * @author jimmy
 */
public class CameraListActivity extends BaseActivity
{
    private List<Map<String,Object>> cameraList;

    private CameraListAdapter cameraListAdapter;

    @BindView(R.id.ptr_camera_list)
    PullToRefreshListView ptrCameraList;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_camera_list;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {

        ptrCameraList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>()
        {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView)
            {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                // Do work to refresh the list here.
                new GetDataTask().execute();
            }
        });

        ptrCameraList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(CameraListActivity.this,CameraPlayActivity.class);
                startActivity(intent);
            }
        });

        cameraList = new ArrayList<>();

        for(int i = 0;i < 10;i++)
        {
            Map<String,Object> map = new HashMap<>();
            cameraList.add(map);
        }

        cameraListAdapter = new CameraListAdapter(this,cameraList);
        ListView actualListView = ptrCameraList.getRefreshableView();
        actualListView.setAdapter(cameraListAdapter);
    }

    @Override
    public void initToolBar()
    {

    }

    private class GetDataTask extends AsyncTask<Void, Void, String>
    {

        //后台处理部分
        @Override
        protected String doInBackground(Void... params)
        {
            // Simulates a background job.
            try
            {
                Thread.sleep(1000);
            } catch (InterruptedException e)
            {
            }
            String str = "Added after refresh...I add";
            return str;
        }

        //这里是对刷新的响应，可以利用addFirst（）和addLast()函数将新加的内容加到LISTView中
        //根据AsyncTask的原理，onPostExecute里的result的值就是doInBackground()的返回值
        @Override
        protected void onPostExecute(String result)
        {
            //在头部增加新添内容
          //  mListItems.addFirst(result);

            //通知程序数据集已经改变，如果不做通知，那么将不会刷新mListItems的集合
          //  mAdapter.notifyDataSetChanged();
            // Call onRefreshComplete when the list has been refreshed.
            ptrCameraList.onRefreshComplete();

            super.onPostExecute(result);
        }
    }

    @OnClick(R.id.iv_choose_camera)
    public void chooseCamera(View view)
    {
        Intent intent = new Intent(CameraListActivity.this,CameraChooseActivity.class);
        startActivity(intent);
    }
}
