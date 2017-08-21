package com.suntek.ibmsapp.page.camera;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.adapter.CameraSearchAdapter;

import com.suntek.ibmsapp.component.Page;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.model.Camera;

import com.suntek.ibmsapp.task.camera.CameraSearchTask;
import com.suntek.ibmsapp.widget.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 摄像头搜索
 *
 * @author jimmy
 */
public class CameraSearchActivity extends BaseActivity implements AdapterView.OnItemClickListener
{
    @BindView(R.id.lv_search_result)
    ListView lvSearchResult;

    @BindView(R.id.et_keyword)
    EditText etKeyword;

    private CameraSearchAdapter cameraSearchAdapter;

    private List<Camera> cameraList;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_camera_search;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        cameraList = new ArrayList<>();
        cameraSearchAdapter = new CameraSearchAdapter(this, cameraList);
        lvSearchResult.setAdapter(cameraSearchAdapter);
        lvSearchResult.setOnItemClickListener(this);
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

    @OnClick(R.id.ll_btn_search)
    public void search(View view)
    {
        String keyword = etKeyword.getText() + "";
        new CameraSearchTask(this, keyword, 1)
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                if (result.getError() == null)
                {
                    Page<List<Camera>> cameraPage  = (Page<List<Camera>>) result.getResultData();
                    cameraList = cameraPage.getData();
                    cameraSearchAdapter.setCameraList(cameraList);
                    cameraSearchAdapter.notifyDataSetChanged();

                }
                else
                {
                    ToastHelper.getInstance(CameraSearchActivity.this).shortShowMessage(result.getError().getMessage());
                }
            }
        }.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        Intent intent = new Intent(CameraSearchActivity.this, CameraPlayActivity.class);
        Camera camera = cameraList.get(i);
        intent.putExtra("cameraId", camera.getId());
        intent.putExtra("cameraName", camera.getName());
        startActivity(intent);
        finish();
    }
}
