package com.suntek.ibmsapp.page.camera;

import android.os.Bundle;
import android.view.View;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.component.cache.ACache;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.model.RecordItem;
import com.suntek.ibmsapp.task.camera.control.CameraQueryRecordTask;
import com.suntek.ibmsapp.util.DateUtil;
import com.suntek.ibmsapp.widget.ToastHelper;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.OnClick;

/**
 * 录像历史日期选择
 *
 * @author jimmy
 */
public class CameraDateActivity extends BaseActivity
{

    private Camera camera;

    private ACache aCache = ACache.get(this);

    //存储录像对应时间段
    private Map<String, Object> recordMap = new HashMap<>();


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
        camera = (Camera) getIntent().getExtras().get("camera");
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

        new CameraQueryRecordTask(this, camera.getDeviceId(), camera.getParentId(), camera.getIp(), camera.getChannel(),
                camera.getUserName(), camera.getPassword(), beginDate, endDate, "Hikvision")
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                if (result.getError() == null)
                {
                    List<RecordItem> recordItems = (List<RecordItem>) result.getResultData();
                    handleRecordTime(recordItems);
                }
                else
                {
                    ToastHelper.getInstance(CameraDateActivity.this).shortShowMessage(result.getError().getMessage());
                }
            }
        }.execute();
    }

    /**
     * 处理返回的历史视频时间
     *
     * @param recordItems
     */
    private void handleRecordTime(List<RecordItem> recordItems)
    {
        //当前时间
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (RecordItem recordItem : recordItems)
        {
            String bt = format1.format(recordItem.getStartTime());
            String et = format1.format(recordItem.getEndTime());
            String[] beginStrs = bt.split(" ");
            String[] endStrs = et.split(" ");
            String beginDate = beginStrs[0];
            String endDate1 = endStrs[0];
            List<RecordItem> recordItems1 = (List<RecordItem>) recordMap.get(beginDate);
            List<RecordItem> recordItems2 = (List<RecordItem>) recordMap.get(endDate1);
            if (recordItems1 == null)
                recordItems1 = new ArrayList<RecordItem>();
            if (recordItems2 == null)
                recordItems2 = new ArrayList<RecordItem>();

            if (!beginDate.equals(endDate1))
            {
                RecordItem recordItem1 = new RecordItem();
                RecordItem recordItem2 = new RecordItem();

                recordItem1.setDeviceId(recordItem.getDeviceId());
                recordItem1.setStartTime(recordItem.getStartTime());
                recordItem1.setEndTime(DateUtil.convertToLong(beginDate + " 23:59:59", "yyyy-MM-dd HH:mm:ss"));
                recordItems1.add(recordItem1);

                recordItem2.setDeviceId(recordItem.getDeviceId());
                recordItem2.setStartTime(DateUtil.convertToLong(endDate1 + " 00:00:00", "yyyy-MM-dd HH:mm:ss"));
                recordItem2.setEndTime(recordItem.getEndTime());
                recordItems2.add(recordItem2);

                recordMap.put(beginDate, recordItems1);
                recordMap.put(endDate1, recordItems2);
            }
            else
            {
                recordItems1.add(recordItem);
                recordMap.put(beginDate, recordItems1);
            }
        }
        aCache.put("camera_history_" + camera.getId(), (Serializable) recordMap);
    }
}
