package com.suntek.ibmsapp.page.user;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.HttpRequest;
import com.suntek.ibmsapp.component.RequestBody;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.component.cache.ACache;
import com.suntek.ibmsapp.component.core.Autowired;
import com.suntek.ibmsapp.model.Area;
import com.suntek.ibmsapp.model.User;
import com.suntek.ibmsapp.page.main.MainActivity;
import com.suntek.ibmsapp.task.area.AreaRootTask;
import com.suntek.ibmsapp.task.base.BaseTask;
import com.suntek.ibmsapp.task.user.UserLoginTask;
import com.suntek.ibmsapp.util.PermissionRequest;
import com.suntek.ibmsapp.util.SaveDataWithSharedHelper;
import com.suntek.ibmsapp.widget.ClearEditText;
import com.suntek.ibmsapp.widget.LoadingDialog;
import com.suntek.ibmsapp.widget.ToastHelper;
import com.suntek.ibmsapp.widget.UnityDialog;


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
    ClearEditText etAccount;

    @BindView(R.id.et_password)
    ClearEditText etPassword;

    private ACache aCache;

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
        //设置全屏
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        aCache = ACache.get(this);
        initEditText();
    }

    private void initEditText()
    {
        String account = sharedHelper.getString("account");
        etAccount.setText(account);
        if(!"".equals(account))
        {
            String password = sharedHelper.getString("password");
            etPassword.setText(password);
        }
        etAccount.setOnClearListening(new ClearEditText.OnClearListening()
        {
            @Override
            public void onClick()
            {
                sharedHelper.save("account","");
                sharedHelper.save("password","");
                etPassword.setText("");
            }
        });
        etPassword.setOnClearListening(new ClearEditText.OnClearListening()
        {
            @Override
            public void onClick()
            {
                sharedHelper.save("password","");
            }
        });
    }

    @Override
    public void initToolBar()
    {

    }


    @OnClick(R.id.ll_login)
    public void login(View view)
    {
        PackageManager pm = getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.READ_PHONE_STATE", "com.suntek.ibmsapp"));
        if (permission)
            login();
        else
            //获取读取手机状态权限
            PermissionRequest.verfyReadPhoneStatePermissions(this);
    }

    private void login()
    {
        HttpRequest request = null;
        String account = etAccount.getText().toString();
        String password = etPassword.getText().toString();
        try
        {
            request = new RequestBody()
                    .putParams("user_name", account, true, "账号不能为空")
                    .putParams("password", password, true, "密码不能为空")
                    .build();
        } catch (Exception e)
        {
            ToastHelper.getInstance(this).shortShowMessage(e.getMessage());
            return;
        }

        LoadingDialog.getInstance(this).showLoading("登录中，请稍候...");
        //获取根节点缓存
        new AreaRootTask(this)
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                if(result.getError() == null)
                {
                    Area area = (Area) result.getResultData();
                    aCache.put("root_area",area);
                    aCache.put("choose_org_code",area.getOgrCode());
                    aCache.put("choose_name", area.getName());
                    login(account,password);
                }
            }
        }.execute();
    }

    private void login(String account,String password)
    {
        new UserLoginTask(this, account, password)
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                //LoadingDialog.getInstance(UserLoginActivity.this).loadingDiss();
                super.onPostExecute(result);
                if (result.getError() == null)
                {
                    User user = (User) result.getResultData();
                    aCache.put("user",user.getUserCode());
                    sharedHelper.save("account",account);
                    sharedHelper.save("password",password);
                    ToastHelper.getInstance(UserLoginActivity.this).shortShowMessage("登录成功");
                    Intent intent = new Intent(UserLoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1)
        {
            if (permissions[0].equals(Manifest.permission.READ_PHONE_STATE) && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                login();
            }
            else
            {
                //没有获得到权限
                Toast.makeText(this, "你不给权限我就不好干事了啦", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @OnClick(R.id.tv_forget_password)
    public void forgetPassword(View view)
    {
        new UnityDialog(this)
                .setTitle("温馨提示")
                .setHint("请找管理员获取账号和密码\n管理员联系方式QQ:523160615")
                .setConfirm("确定", new UnityDialog.OnConfirmDialogListener()
                {
                    @Override
                    public void confirm(UnityDialog unityDialog, String content)
                    {
                        unityDialog.dismiss();
                    }
                })
                .show();
    }

    @OnClick(R.id.iv_logo)
    public void setServer(View view)
    {
        Intent intent = new Intent(UserLoginActivity.this, ServerSettingActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_see_psw)
    public void seePsw(View view)
    {
        if (etPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        else
            etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
    }
}
