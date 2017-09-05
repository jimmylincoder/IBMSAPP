package com.suntek.ibmsapp.page.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.HttpRequest;
import com.suntek.ibmsapp.component.RequestBody;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.component.core.Autowired;
import com.suntek.ibmsapp.page.main.MainActivity;
import com.suntek.ibmsapp.task.user.UserLoginTask;
import com.suntek.ibmsapp.util.SaveDataWithSharedHelper;
import com.suntek.ibmsapp.widget.LoadingDialog;
import com.suntek.ibmsapp.widget.ToastHelper;



import butterknife.BindView;
import butterknife.OnClick;

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
        new UserLoginTask(this,account,password)
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                LoadingDialog.getInstance(UserLoginActivity.this).loadingDiss();
                if(result.getError() == null)
                {
                    ToastHelper.getInstance(UserLoginActivity.this).shortShowMessage("登录成功");
                    Intent intent =  new Intent(UserLoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    ToastHelper.getInstance(UserLoginActivity.this).shortShowMessage(result.getError().getMessage());
                }
            }
        }.execute();
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
