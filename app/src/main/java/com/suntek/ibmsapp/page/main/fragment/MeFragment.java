package com.suntek.ibmsapp.page.main.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseFragment;
import com.suntek.ibmsapp.component.cache.ACache;
import com.suntek.ibmsapp.component.core.Autowired;
import com.suntek.ibmsapp.model.Version;
import com.suntek.ibmsapp.page.about.AboutActivity;
import com.suntek.ibmsapp.page.user.ResetPasswordActivity;
import com.suntek.ibmsapp.page.user.UserLoginActivity;
import com.suntek.ibmsapp.task.version.CheckVersionTask;
import com.suntek.ibmsapp.util.SaveDataWithSharedHelper;
import com.suntek.ibmsapp.util.VersionUtil;
import com.suntek.ibmsapp.widget.LoadingDialog;
import com.suntek.ibmsapp.widget.ToastHelper;
import com.suntek.ibmsapp.widget.UnityDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 我的界面
 *
 * @author jimmy
 */
public class MeFragment extends BaseFragment
{
    @Autowired
    SaveDataWithSharedHelper sharedHelper;

    @BindView(R.id.tv_user_name)
    TextView tvUserName;

    private ACache aCache;

    @Override
    public int getLayoutId()
    {
        return R.layout.fragment_me;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        String userName = sharedHelper.getString("userName");
        tvUserName.setText(userName);
        aCache = ACache.get(getActivity());
    }

    @OnClick(R.id.ll_login_out)
    public void loginout(View view)
    {
        sharedHelper.clear();
        aCache.clear();
        Intent intent = new Intent(getActivity(), UserLoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @OnClick(R.id.ll_update)
    public void checkUpdate(View view)
    {
        checkUpdate();
    }

    @OnClick(R.id.ll_reset_password)
    public void resetPassword(View view)
    {
        Intent intent = new Intent(getActivity(), ResetPasswordActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_help)
    public void helpCenter(View view)
    {
        ToastHelper.getInstance(getActivity()).shortShowMessage("功能完善中！！");
    }

    @OnClick(R.id.ll_about)
    public void about(View view)
    {
//        ToastHelper.getInstance(getActivity()).shortShowMessage("功能完善中！！");
        Intent intent = new Intent(getActivity(), AboutActivity.class);
        startActivity(intent);
    }

    private void checkUpdate()
    {
        LoadingDialog.getInstance(getActivity()).showLoading("正在检测更新中...");
        new CheckVersionTask(getActivity(), VersionUtil.getAppInfo(getActivity()))
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                if (result.getError() == null)
                {
                    LoadingDialog.getInstance(getActivity()).loadingDiss();
                    Version version = (Version) result.getResultData();
                    String isUpdate = version.getIsUpdate();
                    if ("0".equals(isUpdate))
                    {
                        new UnityDialog(getActivity())
                                .setTitle("温馨提示")
                                .setHint("已是最新版本")
                                .setConfirm("确定", new UnityDialog.OnConfirmDialogListener()
                                {
                                    @Override
                                    public void confirm(UnityDialog unityDialog, String content)
                                    {
                                        unityDialog.dismiss();
                                    }
                                }).show();
                    }
                    else
                    {
                        new UnityDialog(getActivity())
                                .setTitle("是否前往更新版本")
                                .setHint("当前版本:" + VersionUtil.getAppInfo(getActivity()) + "\n" +
                                        "更新版本:" + version.getVersionNum() + "\n" +
                                        "更新内容:" + version.getUpdateContent())
                                .setConfirm("确定", new UnityDialog.OnConfirmDialogListener()
                                {
                                    @Override
                                    public void confirm(UnityDialog unityDialog, String content)
                                    {
                                        unityDialog.dismiss();
                                        Intent intent = new Intent();
                                        intent.setAction("android.intent.action.VIEW");
                                        Uri uri = Uri.parse(version.getDownloadAddress());
                                        intent.setData(uri);
                                        startActivity(intent);
                                    }
                                })
                                .setCancel("取消", new UnityDialog.OnCancelDialogListener()
                                {
                                    @Override
                                    public void cancel(UnityDialog unityDialog)
                                    {
                                        unityDialog.dismiss();
                                    }
                                }).show();
                    }
                }
            }
        }.execute();
    }
}
