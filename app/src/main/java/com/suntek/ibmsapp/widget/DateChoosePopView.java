package com.suntek.ibmsapp.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.cache.ACache;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.model.RecordItem;
import com.suntek.ibmsapp.task.base.BaseTask;
import com.suntek.ibmsapp.task.camera.control.CameraQueryRecordTask;
import com.suntek.ibmsapp.util.RecordHander;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 历史视频日期选择popview
 *
 * @author jimmy
 */
public class DateChoosePopView
{
    private static DateChoosePopView dateChoosePopView;

    private Context context;

    private PopupWindow popWindow;

    private View contentView;

    private int popupWidth;

    private int popupHeight;

    private MaterialCalendarView mcvDate;

    private OnDateSelectedListener onDateSelectedListener;

    private AVLoadingIndicatorView avlLoading;

    //存储录像对应时间段
    private Map<String, List<RecordItem>> recordMap = new HashMap<>();

    private Camera camera;

    private BaseTask historyTask;

    private ACache aCache;

    private DateChoosePopView(Context context, Camera camera)
    {
        this.context = context;
        this.camera = camera;
        LayoutInflater inflater = LayoutInflater.from(context);
        contentView = inflater.inflate(R.layout.view_pop_date, null);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        popWindow.setOutsideTouchable(true);
        popupWidth = contentView.getMeasuredWidth();
        popupHeight = contentView.getMeasuredHeight();
        popWindow.setAnimationStyle(R.style.anim_date_bottom);
        aCache = ACache.get(context);
        initClick();
    }

    private void initClick()
    {
        LinearLayout llBack = (LinearLayout) contentView.findViewById(R.id.ll_back);
        mcvDate = (MaterialCalendarView) contentView.findViewById(R.id.mcv_date);
        avlLoading = (AVLoadingIndicatorView) contentView.findViewById(R.id.avl_loading);
        llBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                popWindow.dismiss();
            }
        });

        mcvDate.setOnDateChangedListener(new com.prolificinteractive.materialcalendarview.OnDateSelectedListener()
        {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected)
            {
                if (onDateSelectedListener != null)
                {
                    int month = date.getMonth();
                    int day = date.getDay();
                    String monthStr = month + "";
                    String dayStr = day + "";
                    if(month < 10)
                        monthStr = "0" + month;
                    if(day < 10)
                        dayStr = "0" + day;

                    String chooseDate = date.getYear() + "-" + monthStr + "-" + dayStr;
                    if (recordMap != null)
                    {
                        List<RecordItem> recordItems = (List<RecordItem>) recordMap.get(chooseDate);
                        if (recordItems == null)
                        {
                            ToastHelper.getInstance(context).shortShowMessage("该时间无录像");
                        }
                        else
                        {
                            onDateSelectedListener.onDateSelected(chooseDate);
                        }
                    }
                    else
                        ToastHelper.getInstance(context).shortShowMessage("该时间无录像");

                }
                popWindow.dismiss();
            }
        });
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss()
            {
                if(historyTask!= null && !historyTask.isCancelled())
                {
                    historyTask.cancel(true);
                    historyTask = null;
                }
            }
        });
    }

    public static DateChoosePopView getInstance(Context context, Camera camera)
    {
        dateChoosePopView = new DateChoosePopView(context, camera);
        return dateChoosePopView;
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
     * 居中显示
     *
     * @param view
     */
    public void showCenter(View view)
    {
        popWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        initRecord();
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
     * 初始化历史录像
     */
    private void initRecord()
    {
        //当前时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowDate = format.format(new Date());
        String endTime = nowDate + " 23:59:59";

        //当前时间加一个星期
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_MONTH, -1);
        Date date = cal.getTime();
        String beginTime = format.format(date) + " 00:00:00";

        //判断是否有缓存
        String recordJson = aCache.getAsString(camera.getDeviceId() + "_" + nowDate);
        if(recordJson != null)
        {
            recordMap = (Map)JSON.parse(recordJson);
            initDateChoose();
            return;
        }

        avlLoading.setVisibility(View.VISIBLE);
        historyTask = new CameraQueryRecordTask(context, camera.getDeviceId(), camera.getParentId(),
                beginTime, endTime, "GB28181")
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                avlLoading.setVisibility(View.GONE);
                if (result.getError() == null)
                {
                    avlLoading.setVisibility(View.GONE);
                    List<RecordItem> recordItems = (List<RecordItem>) result.getResultData();
                    if(!recordItems.isEmpty())
                    {
                        recordMap = RecordHander.handleRecordList(recordItems);
                        //将历史记录存入缓存
                        aCache.put(camera.getDeviceId() + "_" + nowDate, JSON.toJSONString(recordMap));
                        initDateChoose();
                    }
                }
            }
        };
        historyTask.execute();
    }

    /**
     * 初始化录像选择
     */
    private void initDateChoose()
    {
        List<CalendarDay> calendarInfos = new ArrayList<>();
        if (recordMap != null)
        {
            for (Map.Entry<String, List<RecordItem>> entry : recordMap.entrySet())
            {
                String date = entry.getKey();
                String[] strs = date.split("-");
                int year = Integer.parseInt(strs[0]);
                int month = Integer.parseInt(strs[1]);
                int day = Integer.parseInt(strs[2]);

                CalendarDay calendarDay = CalendarDay.from(year, month, day);
                calendarInfos.add(calendarDay);
            }
        }
        mcvDate.addDecorator(new EventDecorator(R.color.red, calendarInfos));
        mcvDate.invalidateDecorators();
    }

    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener)
    {
        this.onDateSelectedListener = onDateSelectedListener;
    }

    public interface OnDateSelectedListener
    {
        void onDateSelected(String date);
    }

    public class EventDecorator implements DayViewDecorator
    {
        private int color;
        private HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates)
        {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day)
        {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view)
        {
            view.addSpan(new DotSpan(10, color));
        }

    }
}
