package com.suntek.ibmsapp.page.camera;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.adapter.AreaListAdapter;
import com.suntek.ibmsapp.component.HttpRequest;
import com.suntek.ibmsapp.component.HttpResponse;
import com.suntek.ibmsapp.component.RequestBody;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.component.core.Autowired;
import com.suntek.ibmsapp.network.RetrofitHelper;
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
 * 摄像机选择
 *
 * @author jimmy
 */
public class CameraChooseActivity extends BaseActivity implements AdapterView.OnItemClickListener
{
    @BindView(R.id.lv_area)
    ListView lvArea;

    private AreaListAdapter areaListAdapter;

    private List<Map<String, Object>> areas;

    @Autowired
    SaveDataWithSharedHelper sharedHelper;

    @BindView(R.id.tv_now_area)
    TextView tvNowArea;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_camera_choose;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        lvArea.setOnItemClickListener(this);
        initNowArea();
        initAreaListView("1");
    }

    private void initNowArea()
    {
        tvNowArea.setText(sharedHelper.getString("choose_name"));
        areas = new ArrayList<>();
        Map<String,Object> all = new HashMap<>();
        all.put("id","1");
        all.put("org_code","01");
        all.put("name","华侨城中心小区");
        areas.add(all);
    }

    private void initAreaListView(String parentId)
    {
        HttpRequest request = null;
        try
        {
            request = new RequestBody().putParams("parent_id", parentId, false, null).build();
        } catch (Exception e)
        {
            ToastHelper.getInstance(this).shortShowMessage(e.getMessage());
        }
        RetrofitHelper.getAreaPai()
                .list(request)
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
                            List<Map<String,Object>> areaList = (List) httpResponse.getData().get("area_list");
                            if (!areaList.isEmpty())
                            {
                                areas.addAll(areaList);
                                areaListAdapter = new AreaListAdapter(CameraChooseActivity.this, areas);
                                lvArea.setAdapter(areaListAdapter);
                            }
                            else
                            {
                                finish();
                            }
                        }
                        else
                        {
                            ToastHelper.getInstance(CameraChooseActivity.this).shortShowMessage(httpResponse.getErrorMessage());
                        }
                    }
                }, new Action1<Throwable>()
                {
                    @Override
                    public void call(Throwable throwable)
                    {

                    }
                });
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

    @OnClick(R.id.ll_now_choose)
    public void nowArea(View view)
    {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        String orgCode = (String) areas.get(i).get("org_code");
        String id = (String) areas.get(i).get("id");
        String name = (String) areas.get(i).get("name");
        sharedHelper.save("choose_org_code", orgCode);
        sharedHelper.save("choose_name", name);
        if("01".equals(orgCode))
        {
            finish();
        }else
        {
            try
            {
                areas.clear();
                initAreaListView(id);
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }
}
