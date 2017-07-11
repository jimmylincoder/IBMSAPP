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
import com.suntek.ibmsapp.component.base.BaseFragment;
import com.suntek.ibmsapp.page.camera.CameraPlayActivity;
import com.suntek.ibmsapp.page.camera.CameraSearchActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @Override
    public int getLayoutId()
    {
        return R.layout.fragment_camear_history;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        List<Map<String,Object>> cameraList = new ArrayList<>();
        for(int i = 0;i < 10;i++)
        {
            Map<String,Object> map = new HashMap<>();
            cameraList.add(map);
        }
        cameraSearchAdapter = new CameraSearchAdapter(getActivity(),cameraList);
        ptrHistory.setAdapter(cameraSearchAdapter);
        ptrHistory.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        Intent intent = new Intent(getActivity(),CameraPlayActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView)
    {
        String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

        // Update the LastUpdatedLabel
        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

        // Do work to refresh the list here.
        new CameraHistoryFragment.GetDataTask().execute();
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
            ptrHistory.onRefreshComplete();

            super.onPostExecute(result);
        }
    }
}
