package com.suntek.ibmsapp.widget.hkivisionview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.model.RecordItem;
import com.suntek.ibmsapp.widget.TimeAlgorithm;
import com.suntek.ibmsapp.widget.TimeSeekBarView;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 历史录像全屏操作
 *
 * @author jimmy
 */
public class FullRecordOperController extends AbstractControlView implements
        ConnectionClassManager.ConnectionClassStateChangeListener
{
    private Context context;

    private FullRecordOperController.OnItemClickListener onItemClickListener;

    private OnDateChangeListener onDateChangeListener;

    private Timer netSpeedTimer;

    private TimeSeekBarView taTime;

    // 最后缓存的字节数
    protected long lastTotalRxBytes = 0;
    // 当前缓存时间
    protected long lastTimeStamp = 0;

    private TextView tvNetSpeed;

    private ImageView ivStop;

    private ImageView ivSounds;

    public FullRecordOperController(Context context)
    {
        super(context);
        init(context);
    }

    public FullRecordOperController(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public FullRecordOperController(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
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
        View fullOper = inflater.inflate(R.layout.view_full_record_opr, null);
        addView(fullOper, -1, params);

        LinearLayout llBack = (LinearLayout) fullOper.findViewById(R.id.ll_full_back);
        ivStop = (ImageView) fullOper.findViewById(R.id.iv_full_stop);
        ivSounds = (ImageView) fullOper.findViewById(R.id.iv_full_sounds);
        ImageView ivTakePic = (ImageView) fullOper.findViewById(R.id.iv_full_take_pic);
        ImageView ivRecord = (ImageView) fullOper.findViewById(R.id.iv_full_record);
        taTime = (TimeSeekBarView) fullOper.findViewById(R.id.ta_time);
        LinearLayout llAnimNetSpeed = (LinearLayout) fullOper.findViewById(R.id.ll_anim_net_speed);
        tvNetSpeed = (TextView) fullOper.findViewById(R.id.tv_net_speed);
        final AnimationDrawable animDrawable = (AnimationDrawable) llAnimNetSpeed
                .getBackground();
        animDrawable.start();
        netSpeed();

        llBack.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onItemClickListener != null)
                    onItemClickListener.back(v);
            }
        });
        ivStop.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onItemClickListener != null)
                    onItemClickListener.stop(v);
            }
        });
        ivSounds.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onItemClickListener != null)
                    onItemClickListener.sounds(v);
            }
        });
        ivTakePic.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onItemClickListener != null)
                    onItemClickListener.takePic(v);
            }
        });
        ivRecord.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onItemClickListener != null)
                    onItemClickListener.record(v);
            }
        });

        taTime.setOnValueChangeListener(new TimeSeekBarView.OnValueChangeListener()
        {
            @Override
            public void onValueChange(Date date)
            {
                if (onDateChangeListener != null)
                    onDateChangeListener.onValueChange(date);
            }

            @Override
            public void onStartValueChange(Date date)
            {
                if (onDateChangeListener != null)
                    onDateChangeListener.onStartValueChange(date);
            }

            @Override
            public void onStopValueChange(Date date)
            {
                if (onDateChangeListener != null)
                    onDateChangeListener.onStopValueChange(date);
            }
        });
    }

    public void setValue(long miliSec)
    {
        taTime.setValue(miliSec);
    }

    public void setRecordList(List<RecordItem> itemList)
    {
        taTime.setRecordList(itemList);
    }

    public Date getNowDate(String date)
    {
        TimeAlgorithm timeAlgorithm = taTime.getNowTimeValue();
        long time = timeAlgorithm.getSec(date);
        return new Date(time);
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
                Message message = new Message();
                message.arg1 = Integer.parseInt(speed + "");
                handler.sendMessage(message);
            }
        }, 1000, 1000);
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            tvNetSpeed.setText(msg.arg1 + " KB/s");
        }
    };

    public void setOnItemClickListener(FullRecordOperController.OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBandwidthStateChange(ConnectionQuality bandwidthState)
    {

    }

    public void setIvStopView(Drawable drawable)
    {
        this.ivStop.setBackground(drawable);
    }

    public void setIvSoundsView(Drawable drawable)
    {
        this.ivSounds.setBackground(drawable);
    }

    public void setOnDateChangeListener(OnDateChangeListener onDateChangeListener)
    {
        this.onDateChangeListener = onDateChangeListener;
    }

    public interface OnItemClickListener
    {
        void back(View view);

        void stop(View view);

        void sounds(View view);

        void takePic(View view);

        void record(View view);
    }


    public interface OnDateChangeListener
    {
        void onValueChange(Date date);

        void onStartValueChange(Date date);

        void onStopValueChange(Date date);
    }
}
