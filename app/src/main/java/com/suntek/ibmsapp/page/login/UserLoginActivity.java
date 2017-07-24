package com.suntek.ibmsapp.page.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.HttpRequest;
import com.suntek.ibmsapp.component.HttpResponse;
import com.suntek.ibmsapp.component.RequestBody;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.component.core.Autowired;
import com.suntek.ibmsapp.model.User;
import com.suntek.ibmsapp.network.RetrofitHelper;
import com.suntek.ibmsapp.page.main.MainActivity;
import com.suntek.ibmsapp.util.SaveDataWithSharedHelper;
import com.suntek.ibmsapp.widget.LoadingDialog;
import com.suntek.ibmsapp.widget.ToastHelper;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 用户登录界面
 *
 * @author jimmy
 */
public class UserLoginActivity extends BaseActivity
{
    @BindView(R.id.et_account)
    EditText etAccount;

    @BindView(R.id.et_password)
    EditText etPassword;

    @Autowired
    SaveDataWithSharedHelper sharedHelper;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_user_login;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        etAccount.setText("admin");
        etPassword.setText("suntek");
        initArea();
    }

    @Override
    public void initToolBar()
    {

    }

    @OnClick(R.id.ll_login)
    public void login(View view)
    {
        HttpRequest request = null;
        String account = etAccount.getText().toString();
        String password  = etPassword.getText().toString();
        try
        {
            request = new RequestBody()
                    .putParams("user_name",account,true,"账号不能为空")
                    .putParams("password",password,true,"密码不能为空")
                    .build();
        }catch (Exception e)
        {
            ToastHelper.getInstance(this).shortShowMessage(e.getMessage());
            return;
        }

        LoadingDialog.getInstance(this).showLoading("登录中，请稍候...");
        RetrofitHelper.getUserApi()
                .login(request)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<HttpResponse>()
                {
                    @Override
                    public void call(HttpResponse userHttpResponse)
                    {
                        LoadingDialog.getInstance(UserLoginActivity.this).loadingDiss();
                        if (userHttpResponse.getCode() == HttpResponse.STATUS_SUCCESS)
                        {
                            Intent intent = new Intent(UserLoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            ToastHelper.getInstance(UserLoginActivity.this).shortShowMessage(userHttpResponse.getErrorMessage());
                        }
                    }
                }, new Action1<Throwable>()
                {
                    @Override
                    public void call(Throwable throwable)
                    {
                        LoadingDialog.getInstance(UserLoginActivity.this).loadingDiss();
                        ToastHelper.getInstance(UserLoginActivity.this).shortShowMessage("请检查网络设置是否连通");
                    }
                });
    }

    private void initArea()
    {
        String chooseArea = sharedHelper.getString("choose_area");
        if(chooseArea == null || "".equals(chooseArea))
        {
            sharedHelper.save("choose_area","1");
            sharedHelper.save("choose_name","华侨城中心小区");
        }
    }
}
