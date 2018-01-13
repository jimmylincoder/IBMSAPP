package com.suntek.ibmsapp.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.util.DateUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * 日期选择
 *
 * @author jimmy
 */
public class SelectDateView extends LinearLayout
{
    private String chooseDate;

    private Context context;

    private OnDateListener onDateListener;

    private OnDateClickListener onDateClickListener;

    private ImageView ivBack;

    private ImageView ivForward;

    private TextView tvNowTime;

    public SelectDateView(Context context)
    {
        super(context);
        init(context);
    }

    public SelectDateView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public SelectDateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    private void init(Context context)
    {
        this.context = context;
        this.chooseDate = DateUtil.convertYYYY_MM_DD(new Date());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_select_date, null);
        addView(view, -1, params);

        ivBack = (ImageView) view.findViewById(R.id.iv_left_time);
        ivForward = (ImageView) view.findViewById(R.id.iv_right_time);
        tvNowTime = (TextView) view.findViewById(R.id.tv_now_time);

        tvNowTime.setText(chooseDate);
        ivBack.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                subOrReduceDate(-1);
            }
        });
        ivForward.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                subOrReduceDate(1);
            }
        });
        tvNowTime.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onDateClickListener != null)
                    onDateClickListener.onClick(v);
            }
        });
    }

    private void subOrReduceDate(int day)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.convertToDate(chooseDate + " 00:00:00", "yyyy-MM-dd HH:mm:ss"));
        calendar.add(Calendar.DAY_OF_MONTH, day);
        chooseDate = DateUtil.convertYYYY_MM_DD(calendar.getTime());
        if (onDateListener != null)
            onDateListener.onChange(chooseDate);
        tvNowTime.setText(chooseDate);
    }

    public void setNowTimeText(String date)
    {
        tvNowTime.setText(date);
    }

    public void setOnDateListener(OnDateListener onDateListener)
    {
        this.onDateListener = onDateListener;
    }

    public void setOnDateClickListener(OnDateClickListener onDateClickListener)
    {
        this.onDateClickListener = onDateClickListener;
    }

    public interface OnDateListener
    {
        void onChange(String date);
    }

    public interface OnDateClickListener
    {
        void onClick(View view);
    }
}
