package com.suntek.ibmsapp.page.camera;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.adapter.CameraHistoryAdapter;

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

    @BindView(R.id.ll_message)
    LinearLayout llMessage;

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

        etKeyword.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                search(s.toString());
            }
        });
    }

    @Override
    public void initToolBar()
    {

    }

    @OnClick(R.id.ll_btn_cancel)
    public void back(View view)
    {
        finish();
    }

    public void search(String keyword)
    {
        new CameraSearchTask(this, keyword, 1)
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                if (result.getError() == null)
                {
                    Page<List<Camera>> cameraPage = (Page<List<Camera>>) result.getResultData();
                    cameraList = cameraPage.getData();
                    if (cameraList.isEmpty())
                    {
                        lvSearchResult.setVisibility(View.GONE);
                        llMessage.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        llMessage.setVisibility(View.GONE);
                        lvSearchResult.setVisibility(View.VISIBLE);
                    }
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
        Bundle bundle = new Bundle();
        bundle.putSerializable("camera", camera);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
