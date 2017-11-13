package com.suntek.ibmsapp.page.camera;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.model.RecordItem;
import com.suntek.ibmsapp.task.camera.control.CameraQueryRecordTask;
import com.suntek.ibmsapp.util.DateUtil;
import com.suntek.ibmsapp.widget.ToastHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.OnClick;

/**
 * 历史视频播放
 *
 * @author jimmy
 */
public class CameraHKHistoryActivity extends BaseActivity
{
    private Camera camera;

    //存储录像对应时间段
    private Map<String, Object> recordMap = new HashMap<>();

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_camera_history_hk;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        init();
    }

    private void init()
    {
        camera = (Camera) getIntent().getExtras().get("camera");
        //initRecord();
    }

    @Override
    public void initToolBar()
    {

    }

    @OnClick(R.id.btn_choose_date)
    public void chooseDate(View view)
    {
        Intent intent = new Intent(CameraHKHistoryActivity.this, CameraDateActivity.class);
        startActivity(intent);
    }

    /**
     * 初始化历史录像，获取最近一个月录像
     */
    private void initRecord()
    {
        //当前时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowDate = format.format(new Date());
        String endTime = nowDate + " 23:59:59";


        //当前时间加一个月
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date date = cal.getTime();
        String beginTime = format.format(date) + " 00:00:00";

        new CameraQueryRecordTask(this, camera.getDeviceId(), camera.getParentId(), camera.getIp(), camera.getChannel(),
                camera.getUserName(), camera.getPassword(), beginTime, endTime, "Hikvision")
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                if (result.getError() == null)
                {
                    List<RecordItem> recordItems = (List<RecordItem>) result.getResultData();
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

                    //TODO 渲染时间轴
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String date = simpleDateFormat.format(new Date());
                }
                else
                {
                    ToastHelper.getInstance(CameraHKHistoryActivity.this).shortShowMessage(result.getError().getMessage());
                }
            }
        }.execute();
    }

}
