package com.suntek.ibmsapp.page.camera;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.adapter.CameraSearchAdapter;
import com.suntek.ibmsapp.component.HttpRequest;
import com.suntek.ibmsapp.component.HttpResponse;
import com.suntek.ibmsapp.component.RequestBody;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.network.RetrofitHelper;
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

    private List<Map<String,Object>> cameraList;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_camera_search;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
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
//        List<Map<String,Object>> cameraList = new ArrayList<>();
//        for(int i = 0;i < 10;i++)
//        {
//            Map<String,Object> map = new HashMap<>();
//            cameraList.add(map);
//        }

        String keyword = etKeyword.getText() + "";
        HttpRequest request = null;
        try
        {
            cameraList = new ArrayList<>();
            cameraSearchAdapter = new CameraSearchAdapter(this,cameraList);
            lvSearchResult.setAdapter(cameraSearchAdapter);
            request = new RequestBody()
                    .putParams("page","1",false,null)
                    .putParams("keyword",keyword,true,"关键字不能为空")
                    .build();
        } catch (Exception e)
        {
            ToastHelper.getInstance(this).shortShowMessage(e.getMessage());
            return;
        }

        RetrofitHelper.getCameraApi()
                .listByKeyWord(request)
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
                            cameraSearchAdapter.setCameraList(cameraList);
                            cameraSearchAdapter.notifyDataSetChanged();
                        }
                        else
                        {
                            ToastHelper.getInstance(CameraSearchActivity.this).shortShowMessage(listHttpResponse.getErrorMessage());
                        }

                    }
                }, new Action1<Throwable>()
                {
                    @Override
                    public void call(Throwable throwable)
                    {
                        ToastHelper.getInstance(CameraSearchActivity.this).shortShowMessage(throwable.getMessage());
                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        Intent intent = new Intent(CameraSearchActivity.this,CameraPlayActivity.class);
        Map<String,Object> camera = cameraList.get(i - 1);
        intent.putExtra("cameraId",camera.get("id") + "");
        intent.putExtra("cameraName",camera.get("name") + "");        startActivity(intent);
        finish();
    }
}
