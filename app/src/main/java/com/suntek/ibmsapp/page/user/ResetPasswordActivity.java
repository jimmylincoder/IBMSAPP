package com.suntek.ibmsapp.page.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.AppManager;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.component.cache.ACache;
import com.suntek.ibmsapp.component.core.Autowired;
import com.suntek.ibmsapp.task.user.ChangePasswordTask;
import com.suntek.ibmsapp.widget.LoadingDialog;
import com.suntek.ibmsapp.widget.ToastHelper;
import com.suntek.ibmsapp.widget.UnityDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 修改密码
 *
 * @author jimmy
 */
public class ResetPasswordActivity extends BaseActivity
{

    @BindView(R.id.et_old_password)
    EditText etOldPsd;

    @BindView(R.id.et_new_password)
    EditText etNewPsd;

    @BindView(R.id.et_comfirm_password)
    EditText etConfirmPsd;

    private ACache aCache;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_reset_password;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        aCache = ACache.get(this);
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

    @OnClick(R.id.ll_confirm_rest)
    public void confirm(View view)
    {
        String oldPsd = etOldPsd.getText().toString();
        String newPsd = etNewPsd.getText().toString();
        String confirmPsd = etConfirmPsd.getText().toString();

        if (oldPsd == null || "".equals(oldPsd))
        {
            ToastHelper.getInstance(this).shortShowMessage("原密码不能为空");
            return;
        }
        if (newPsd == null || "".equals(newPsd))
        {
            ToastHelper.getInstance(this).shortShowMessage("新密码不能为空");
            return;
        }
        if (confirmPsd == null || "".equals(confirmPsd))
        {
            ToastHelper.getInstance(this).shortShowMessage("确认密码不能为空");
            return;
        }
        if (oldPsd.equals(newPsd))
        {
            ToastHelper.getInstance(this).shortShowMessage("原密码和新密码相同");
            return;
        }
        if (!newPsd.equals(confirmPsd))
        {
            ToastHelper.getInstance(this).shortShowMessage("新密码和确认密码不一致");
            return;
        }
        changePsd(oldPsd, newPsd);
    }


    private void changePsd(String oldPad, String newPsd)
    {
        String userCode = aCache.getAsString("user");
        LoadingDialog.getInstance(this).showLoading("修改中，请稍候...");
        new ChangePasswordTask(this, userCode, newPsd, oldPad)
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                if (result.getError() == null)
                {
                    LoadingDialog.getInstance(ResetPasswordActivity.this).loadingDiss();
                    aCache.clear();
                    new UnityDialog(ResetPasswordActivity.this).setTitle("温馨提示")
                            .setHint("修改密码成功，需重新登录")
                            .setConfirm("确认", new UnityDialog.OnConfirmDialogListener()
                            {
                                @Override
                                public void confirm(UnityDialog unityDialog, String content)
                                {
                                    AppManager.finishAllActivity();
                                    Intent intent = new Intent(ResetPasswordActivity.this, UserLoginActivity.class);
                                    startActivity(intent);
                                }
                            }).show();
                }
            }
        }.execute();
    }
}
