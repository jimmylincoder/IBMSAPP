package com.suntek.ibmsapp.page.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.component.cache.ACache;
import com.suntek.ibmsapp.component.core.Autowired;
import com.suntek.ibmsapp.page.user.UserLoginActivity;
import com.suntek.ibmsapp.page.main.MainActivity;

/**
 * 欢迎界面
 *
 * @author jimmy
 */
public class WelcomeActivity extends BaseActivity
{
    @Override
    public int getLayoutId()
    {
        return R.layout.activity_welcome;
    }

    private ACache aCache;

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        aCache = ACache.get(this);
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(1000);
                    String userCode = aCache.getAsString("user");
                    Intent intent = null;
                    if (userCode == null || "".equals(userCode))
                        intent = new Intent(WelcomeActivity.this, UserLoginActivity.class);
                    else
                        intent = new Intent(WelcomeActivity.this, MainActivity.class);

                    startActivity(intent);

                    finish();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void initToolBar()
    {

    }
}
