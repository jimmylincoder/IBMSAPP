package com.suntek.ibmsapp.widget.hkivisionview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suntek.ibmsapp.R;

/**
 * 全屏时的控制界面
 *
 * @author jimmy
 */
public class FullOperController extends AbstractControlView
{
    private Context context;

    private OnItemClickListener onItemClickListener;

    public FullOperController(Context context)
    {
        super(context);
        init(context);
    }

    public FullOperController(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public FullOperController(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
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
        View fullOper = inflater.inflate(R.layout.view_full_oper, null);
        addView(fullOper, -1, params);

        LinearLayout llBack = (LinearLayout) fullOper.findViewById(R.id.ll_full_back);
        ImageView ivStop = (ImageView) fullOper.findViewById(R.id.iv_full_stop);
        ImageView ivSounds = (ImageView) fullOper.findViewById(R.id.iv_full_sounds);
        ImageView ivTakePic = (ImageView) fullOper.findViewById(R.id.iv_full_take_pic);
        ImageView ivRecord = (ImageView) fullOper.findViewById(R.id.iv_full_record);
        ImageView ivStream = (ImageView) fullOper.findViewById(R.id.iv_fulL_stream);

        llBack.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onItemClickListener.back(v);
            }
        });
        ivStop.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onItemClickListener.stop(v);
            }
        });
        ivSounds.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onItemClickListener.sounds(v);
            }
        });
        ivTakePic.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onItemClickListener.takePic(v);
            }
        });
        ivRecord.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onItemClickListener.record(v);
            }
        });
        ivStream.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onItemClickListener.streamType(v);
            }
        });
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
    }

    public static interface OnItemClickListener
    {
        void back(View view);

        void stop(View view);

        void sounds(View view);

        void takePic(View view);

        void record(View view);

        void streamType(View view);
    }
}
