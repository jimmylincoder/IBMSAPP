package com.suntek.ibmsapp.page.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseFragment;
import com.suntek.ibmsapp.component.cache.ACache;
import com.suntek.ibmsapp.component.core.Autowired;
import com.suntek.ibmsapp.page.about.AboutActivity;
import com.suntek.ibmsapp.page.login.UserLoginActivity;
import com.suntek.ibmsapp.util.SaveDataWithSharedHelper;
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
        //ToastHelper.getInstance(getActivity()).shortShowMessage("功能完善中！！");
        new UnityDialog(getActivity())
                .setTitle("版本更新")
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

    @OnClick(R.id.ll_reset_password)
    public void resetPassword(View view)
    {
        ToastHelper.getInstance(getActivity()).shortShowMessage("功能完善中！！");
    }

    @OnClick(R.id.ll_help)
    public void helpCenter(View view)
    {
        ToastHelper.getInstance(getActivity()).shortShowMessage("功能完善中！！");
    }

    @OnClick(R.id.ll_about)
    public void about(View view)
    {
        ToastHelper.getInstance(getActivity()).shortShowMessage("功能完善中！！");
//        Intent intent = new Intent(getActivity(), AboutActivity.class);
//        startActivity(intent);
    }
}
