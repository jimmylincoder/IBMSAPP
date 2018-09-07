package com.suntek.ibmsapp.page.camera;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;

import com.dsw.calendar.component.MonthView;
import com.dsw.calendar.entity.CalendarInfo;
import com.dsw.calendar.views.GridCalendarView;
import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.component.cache.ACache;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.model.RecordItem;
import com.suntek.ibmsapp.task.camera.control.CameraQueryRecordTask;
import com.suntek.ibmsapp.util.DateUtil;
import com.suntek.ibmsapp.util.RecordHander;
import com.suntek.ibmsapp.widget.ToastHelper;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 录像历史日期选择
 *
 * @author jimmy
 */
public class CameraDateActivity extends BaseActivity
{

    private Camera camera;

    private ACache aCache;

    //存储录像对应时间段
    private Map<String, List<RecordItem>> recordMap = new HashMap<>();

    @BindView(R.id.avl_loading)
    AVLoadingIndicatorView avlLoading;

    @BindView(R.id.cv_date)
    GridCalendarView cvDate;

    private String chooseDate;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_camera_date;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        init();
    }

    private void init()
    {
        aCache = ACache.get(this);
        camera = (Camera) getIntent().getExtras().get("camera");

        initLastMonth();
    }

    private void initLastMonth()
    {
        //当前时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowDate = format.format(new Date());
        String endTime = nowDate + " 23:59:59";

        //当前时间加一个月
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        Date date = cal.getTime();
        String beginTime = format.format(date) + " 00:00:00";

        initRecord(beginTime, endTime);

        cvDate.setDateClick(new MonthView.IDateClick()
        {
            @Override
            public void onClickOnDate(int year, int month, int day)
            {
                String monthStr = month < 10 ? "0" + month : month + "";
                String dayString = day < 10 ? "0" + day : day + "";

                chooseDate = year + "-" + monthStr + "-" + dayString;
                if (recordMap != null)
                {
                    List<RecordItem> recordItems = (List<RecordItem>) recordMap.get(chooseDate);
                    if (recordItems == null)
                    {
                        ToastHelper.getInstance(CameraDateActivity.this).shortShowMessage("该时间无录像");
                        finish();
                    }
                    else
                    {
                        ToastHelper.getInstance(CameraDateActivity.this).shortShowMessage("该时间有录像");
                        Intent intent = new Intent();
                        intent.putExtra("choose_date", chooseDate);
                        setResult(1, intent);
                        finish();
                    }
                }

            }
        });
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

    /**
     * 初始化历史录像
     */
    private void initRecord(String beginDate, String endDate)
    {
        avlLoading.setVisibility(View.VISIBLE);
        new CameraQueryRecordTask(this, camera.getDeviceId(), camera.getParentId(),
                beginDate, endDate, "GB28181")
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                if (result.getError() == null)
                {
                    avlLoading.setVisibility(View.GONE);
                    List<RecordItem> recordItems = (List<RecordItem>) result.getResultData();
                    recordMap = RecordHander.handleRecordList(recordItems);
                    initDateChoose();
                }
            }
        }.execute();
    }

    /**
     * 初始化录像选择
     */
    private void initDateChoose()
    {
        List<CalendarInfo> calendarInfos = new ArrayList<>();
        if (recordMap != null)
        {
            for (Map.Entry<String, List<RecordItem>> entry : recordMap.entrySet())
            {
                String date = entry.getKey();
                String[] strs = date.split("-");
                int year = Integer.parseInt(strs[0]);
                int month = Integer.parseInt(strs[1]);
                int day = Integer.parseInt(strs[2]);

                CalendarInfo calendarInfo = new CalendarInfo(year, month, day, "有录像");
                calendarInfos.add(calendarInfo);
            }
        }
        cvDate.setCalendarInfos(calendarInfos);
    }
}
