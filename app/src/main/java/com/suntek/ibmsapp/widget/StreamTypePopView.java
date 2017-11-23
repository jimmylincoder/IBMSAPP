package com.suntek.ibmsapp.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.suntek.ibmsapp.R;

/**
 * 视频高清，流畅弹出窗
 *
 * @author jimmy
 */
public class StreamTypePopView
{
    private Context context;

    private PopupWindow popWindow;

    private static StreamTypePopView streamTypePopView;

    private View contentView;

    private OnItemClickListener onItemClickListener;

    private TextView tvHighQuality;

    private TextView tvFluent;

    private int popupWidth;

    private int popupHeight;

    private int selectedPosition = 0;

    private StreamTypePopView(Context context)
    {
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        contentView = inflater.inflate(R.layout.view_stream_type, null);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popWindow.setOutsideTouchable(true);
        popupWidth = contentView.getMeasuredWidth();
        popupHeight = contentView.getMeasuredHeight();
        initClick();
    }

    private void initClick()
    {
        tvFluent = (TextView) contentView.findViewById(R.id.tv_fast);
        tvHighQuality = (TextView) contentView.findViewById(R.id.tv_clean);
        tvFluent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (selectedPosition == 1)
                {
                    selectedPosition = 0;
                    tvFluent.setTextColor(context.getResources().getColor(R.color.blue_30));
                    tvHighQuality.setTextColor(context.getResources().getColor(R.color.gray_light));
                    if (onItemClickListener != null)
                    {
                        onItemClickListener.OnFluent(v);
                    }
                    popWindow.dismiss();
                }
            }
        });
        tvHighQuality.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (selectedPosition == 0)
                {
                    selectedPosition = 1;
                    tvHighQuality.setTextColor(context.getResources().getColor(R.color.blue_30));
                    tvFluent.setTextColor(context.getResources().getColor(R.color.gray_light));
                    if (onItemClickListener != null)
                        onItemClickListener.OnHighQuality(v);
                    popWindow.dismiss();
                }
            }
        });
    }

    public static StreamTypePopView getInstance(Context context)
    {
        if (streamTypePopView == null)
        {
            streamTypePopView = new StreamTypePopView(context);
        }
        return streamTypePopView;
    }

    /**
     * 显示在上方
     *
     * @param view
     */
    public void showUp(View view)
    {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        popWindow.showAtLocation(view, Gravity.NO_GRAVITY, (location[0] + view.getWidth() / 2)
                - popupWidth / 2, location[1] - popupHeight);
    }

    /**
     * 显示在指定view下方
     *
     * @param view
     */
    public void showDown(View view)
    {
        popWindow.showAsDropDown(view);
    }

    /**
     * 关闭
     */
    public void dismiss()
    {
        popWindow.dismiss();
    }

    /**
     * 是否显示中
     *
     * @return
     */
    public boolean isShowing()
    {
        return popWindow.isShowing();
    }

    /**
     * 获取当前选中位置
     *
     * @return
     */
    public int getSelectedPosition()
    {
        return selectedPosition;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener
    {
        void OnHighQuality(View view);

        void OnFluent(View view);
    }
}
