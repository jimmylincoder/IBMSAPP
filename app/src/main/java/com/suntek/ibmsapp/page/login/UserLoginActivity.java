package com.suntek.ibmsapp.page.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.HttpRequest;
import com.suntek.ibmsapp.component.HttpResponse;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.model.User;
import com.suntek.ibmsapp.network.RetrofitHelper;
import com.suntek.ibmsapp.page.main.MainActivity;
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

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_user_login;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {

    }

    @Override
    public void initToolBar()
    {

    }

    @OnClick(R.id.ll_login)
    public void login(View view)
    {
        String account = etAccount.getText().toString();
        String password  = etPassword.getText().toString();

        if(account == null || "".equals(account))
        {
            ToastHelper.getInstance(this).shortShowMessage("账号不能为空");
            return;
        }
        if(password == null || "".equals(password))
        {
            ToastHelper.getInstance(this).shortShowMessage("密码不能为空");
            return;
        }

        HttpRequest httpRequest = new HttpRequest();
        Map<String,Object> params = new HashMap<>();
        params.put("user_name",account);
        params.put("password",password);
        httpRequest.setParams(params);

        LoadingDialog.getInstance(this).showLoading("登录中，请稍候...");
        RetrofitHelper.getUserApi()
                .login(httpRequest)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<HttpResponse<User>>()
                {
                    @Override
                    public void call(HttpResponse<User> userHttpResponse)
                    {
                        LoadingDialog.getInstance(UserLoginActivity.this).loadingDiss();
                        if (userHttpResponse.getStatus() == HttpResponse.STATUS_SUCCESS)
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
                        ToastHelper.getInstance(UserLoginActivity.this).shortShowMessage(throwable.getMessage());
                    }
                });
    }
}
