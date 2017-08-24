package com.suntek.ibmsapp.page.camera;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.model.Camera;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 摄像机详情界面
 *
 * @author jimmy
 */
public class CameraInfoActivity extends BaseActivity
{
    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.tv_channel)
    TextView tvChannel;

    @BindView(R.id.tv_ip)
    TextView tvIp;

    @BindView(R.id.tv_port)
    TextView tvPort;

    @BindView(R.id.tv_area)
    TextView tvArea;

    @BindView(R.id.tv_vendor)
    TextView tvVendor;

    @BindView(R.id.tv_type)
    TextView tvType;

    private Camera camera;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_camera_info;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        camera = (Camera) getIntent().getSerializableExtra("camera");
        tvName.setText(camera.getName());
        tvChannel.setText(camera.getChannel());
        tvIp.setText(camera.getIp());
        tvPort.setText(camera.getPort());
        tvArea.setText(camera.getOrgName());
        tvVendor.setText(camera.getVendorName());
        if("1".equals(camera.getType()))
        {
            tvType.setText("枪机");
        }
        else
        {
            tvType.setText("球机");
        }

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
}
