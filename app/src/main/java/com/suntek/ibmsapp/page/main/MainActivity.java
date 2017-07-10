package com.suntek.ibmsapp.page.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.network.RetrofitHelper;
import com.suntek.ibmsapp.page.camera.CameraPlayActivity;
import java.util.Map;
import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity
{
    @BindView(R.id.click_me_BN)
    Button clickMeBN;
    @BindView(R.id.result_TV)
    TextView resultTV;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_main;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        loadData();
    }

    @Override
    public void initToolBar()
    {
    }

    @Override
    public void loadData()
    {
        RetrofitHelper.getMovieAPI()
                .getTopMovie(0,10)
                .compose(this.<Map<String,Object>>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Map<String, Object>>()
                {
                    @Override
                    public void call(Map<String, Object> stringObjectMap)
                    {
                        resultTV.setText(stringObjectMap.toString());
                    }
                }, new Action1<Throwable>()
                {
                    @Override
                    public void call(Throwable throwable)
                    {
                        resultTV.setText(throwable.getMessage());
                    }
                });
    }

    @OnClick(R.id.click_me_BN)
    public void onClick()
    {
        Intent intent = new Intent(MainActivity.this, CameraPlayActivity.class);
        startActivity(intent);
    }
}
