package com.suntek.ibmsapp.widget.hkivisionview;

import android.content.Context;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.facebook.network.connectionclass.DeviceBandwidthSampler;
import com.suntek.ibmsapp.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 网速控制界面
 *
 * @author jimmy
 */
public class NetSpeedController extends AbstractControlView implements
        ConnectionClassManager.ConnectionClassStateChangeListener
{
    private Timer netSpeedTimer;

    private TextView tvNetState;

    private TextView tvNetSpeed;

    private String netState;

    private final int POOR = 301;
    private final int MODERATE = 302;
    private final int GOOD = 303;
    private final int EXCELLENT = 304;

    // 最后缓存的字节数
    protected long lastTotalRxBytes = 0;
    // 当前缓存时间
    protected long lastTimeStamp = 0;

    private Context context;

    public NetSpeedController(Context context)
    {
        super(context);
        init(context);
    }

    public NetSpeedController(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public NetSpeedController(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        this.context = context;
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        setOrientation(HORIZONTAL);
        LayoutInflater inflater = LayoutInflater.from(context);
        View netSpeed = inflater.inflate(R.layout.view_net_speed, null);
        addView(netSpeed, -1, params);
        //网络测速
        DeviceBandwidthSampler.getInstance().startSampling();

        tvNetSpeed = (TextView) netSpeed.findViewById(R.id.tv_net_speed);
        tvNetState = (TextView) netSpeed.findViewById(R.id.tv_net_state);
        netSpeed();
    }

    /**
     * 网速
     */
    private void netSpeed()
    {
        if (netSpeedTimer == null)
            netSpeedTimer = new Timer();
        netSpeedTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid)
                        == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);
                long nowTimeStamp = System.currentTimeMillis(); // 当前时间
                // kb/s
                long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp == lastTimeStamp ? nowTimeStamp : nowTimeStamp
                        - lastTimeStamp));// 毫秒转换
                lastTimeStamp = nowTimeStamp;
                lastTotalRxBytes = nowTotalRxBytes;
                final ConnectionQuality connectionQuality = ConnectionClassManager.getInstance().getCurrentBandwidthQuality();
                final double downloadKBitsPerSecond = ConnectionClassManager.getInstance().getDownloadKBitsPerSecond();
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putLong("net_speed", speed);
                message.setData(bundle);
                if (tvNetState != null && tvNetSpeed != null)
                {
                    switch (connectionQuality)
                    {
                        case POOR:
                            message.arg1 = POOR;
                            break;
                        case MODERATE:
                            message.arg1 = MODERATE;
                            break;
                        case GOOD:
                            message.arg1 = GOOD;
                            break;
                        case EXCELLENT:
                            message.arg1 = EXCELLENT;
                            break;
                    }
                    handler.sendMessage(message);
                }
            }
        }, 1000, 1000);
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            double netSpeed = msg.getData().getLong("net_speed");
            switch (msg.arg1)
            {
                case POOR:
                    netState = "差强人意";
                    tvNetState.setTextColor(getResources().getColor(R.color.red));
                    break;
                case MODERATE:
                    netState = "马马虎虎";
                    tvNetState.setTextColor(getResources().getColor(R.color.white));
                    break;
                case GOOD:
                    netState = "不错哟";
                    tvNetState.setTextColor(getResources().getColor(R.color.green));
                    break;
                case EXCELLENT:
                    netState = "无人匹敌";
                    tvNetState.setTextColor(getResources().getColor(R.color.green));
                    break;
            }
            tvNetState.setText(netState);
            tvNetSpeed.setText(netSpeed + " kb/s");
        }
    };

    @Override
    public void onBandwidthStateChange(ConnectionQuality bandwidthState)
    {

    }
}
