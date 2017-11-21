package com.suntek.ibmsapp.page.camera;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.dsw.calendar.views.GridCalendarView;
import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.component.tcp.CameraStreamSocketClient;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.model.RecordItem;
import com.suntek.ibmsapp.task.camera.control.CameraChangePositionTask;
import com.suntek.ibmsapp.task.camera.control.CameraPauseTask;
import com.suntek.ibmsapp.task.camera.control.CameraPlayHKTask;
import com.suntek.ibmsapp.task.camera.control.CameraQueryProgressTask;
import com.suntek.ibmsapp.task.camera.control.CameraQueryRecordTask;
import com.suntek.ibmsapp.task.camera.control.CameraResumeTask;
import com.suntek.ibmsapp.task.camera.control.CameraStopTask;
import com.suntek.ibmsapp.util.DateUtil;
import com.suntek.ibmsapp.widget.CustomSurfaceView;
import com.suntek.ibmsapp.widget.TimeSeekBarView;
import com.suntek.ibmsapp.widget.ToastHelper;

import org.MediaPlayer.PlayM4.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

/**
 * 视频播放抽象类
 *
 * @author jimmy
 */
public abstract class BaseCameraPlayActivity extends BaseActivity
{
    //全屏
    protected final int PLAYER_FULLSCREEN = 0;
    //非全屏
    protected final int PLAYER_NOT_FULL = 1;
    //停止
    protected final int STATE_STOP = 1;
    //播放
    protected final int STATE_PLAY = 2;
    //当前播放状态
    protected int playState = STATE_PLAY;
    //截图保存位置
    protected final String PIC_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/ibms";
    //视频view
    @BindView(R.id.video_view)
    CustomSurfaceView ivvVideo;
    //顶部标题
    @BindView(R.id.ll_head)
    LinearLayout llHead;
    //视频底部操作
    @BindView(R.id.ll_oper)
    LinearLayout llOper;
    //当前时间
    @BindView(R.id.ll_time)
    LinearLayout llTime;
    //云台控制
    @BindView(R.id.ll_message)
    LinearLayout llMessage;
    //视频界面
    @BindView(R.id.fl_video)
    FrameLayout flVideo;
    //当前时间
    @BindView(R.id.tv_nowtime)
    TextView tvNowTime;
    //时间轴
    @BindView(R.id.ta_time)
    TimeSeekBarView timeSeekBar;
    //摄像机名称
    @BindView(R.id.tv_camera_name)
    TextView tvCameraName;
    @BindView(R.id.tv_type)
    TextView tvPlayType;
    @BindView(R.id.ll_loading)
    LinearLayout llLoading;
    @BindView(R.id.tv_load_percent)
    TextView tvLoadPercent;
    @BindView(R.id.ll_fail)
    LinearLayout llFail;
    @BindView(R.id.iv_take_pic)
    ImageView ivTakePic;
    @BindView(R.id.ll_take_pic)
    LinearLayout llTakePic;
    @BindView(R.id.tv_net_state)
    TextView tvNetState;
    @BindView(R.id.tv_net_speed)
    TextView tvNetSpeed;
    @BindView(R.id.ll_change_time)
    LinearLayout llChangeTime;
    @BindView(R.id.tv_change_time)
    TextView tvChangeTime;
    @BindView(R.id.ll_record)
    LinearLayout llRecord;
    @BindView(R.id.tv_record_time)
    TextView tvRecordTime;
    @BindView(R.id.ll_sfv)
    LinearLayout llSfv;
    @BindView(R.id.iv_pot)
    ImageView ivPot;
    @BindView(R.id.ll_net_speed)
    LinearLayout llNetSpeed;
    //时间选择
    protected PopupWindow dateChoose;
    //菜单
    protected PopupWindow menu;
    //清晰度选择
    protected PopupWindow streamMenu;
    //当前日期 yyyy-MM-dd
    protected String chooseDate;
    //当前时间handler
    protected Handler timeHandler;
    protected boolean mBackPressed;
    //是否停止当前时间线程
    protected boolean isTimeRun = true;
    //全屏状态
    protected int playerState = PLAYER_NOT_FULL;
    //时间轴定时器
    protected Timer seekBarTimer;
    //查询进度
    protected Timer queryProgressTimer;
    //当前视频会话
    protected String session;
    //是否隐藏
    protected boolean isBackGround = false;
    //当前摄像头
    protected Camera camera;
    //存储录像对应时间段
    protected Map<String, Object> recordMap = new HashMap<>();
    //是否为录像状态
    protected boolean isRecorder = false;
    //日历选择
    GridCalendarView gcv;
    //记录录像开始时间
    protected long recordBeginTime;
    protected long changePosition;
    // 最后缓存的字节数
    protected long lastTotalRxBytes = 0;
    // 当前缓存时间
    protected long lastTimeStamp = 0;
    //网络检测线程
    protected Timer netSpeedTimer;
    protected boolean isChangeTimeStart;

    protected Player player;
    protected int port;

    //socket ip和端口
    protected String socketIp;
    protected int socketPort;


    protected int bitmapLen;
    protected int bitmapWidth;
    protected int bitmapHeight;

    //视频流sokcet连接类
    protected CameraStreamSocketClient cameraStreamSocketClient;
    protected String TAG = CameraPlayHKActivity.class.getName();


    protected String recordFilePath = "/sdcard/DCIM/Camera/ibms/";
    protected FileOutputStream fileOutputStream;
    protected File recordFile;

    protected boolean record = false;
    protected boolean isSetRecordSuccess = false;
    protected Timer recordTimer;
    protected SurfaceHolder surfaceHolder;

    protected boolean isRequestRecord = false;
    protected int pauseState = 0;
    //缓冲1m
    protected static final int MAX_BUFFER_SIZE = 1024 * 1024 * 1;
    protected static final int MIN_BUFFER_SIZE = 1;
    protected int size = MAX_BUFFER_SIZE;

    protected static final int STREAM_CLEAN = 0;
    protected static final int STREAM_FAST = 1;
    protected int streamType = STREAM_FAST;

    /**
     * 请求查询进度线程
     */
    protected void startQueryProgress()
    {
        if (queryProgressTimer == null)
        {
            queryProgressTimer = new Timer();
            queryProgressTimer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    queryProgress();
                }
            }, 5000, 8000);
        }
    }

    /**
     * 请求查询进度
     */
    private void queryProgress()
    {
        if (session == null)
            return;

        new CameraQueryProgressTask(this, session)
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                if (result.getError() == null)
                {

                }
            }
        }.execute();
    }

    /**
     * 初始化历史录像，获取最近一个月录像
     */
    protected void initRecord()
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
                    initTimeSeekRecord(date);
                }
                else
                {
                    ToastHelper.getInstance(BaseCameraPlayActivity.this).shortShowMessage(result.getError().getMessage());
                }
            }
        }.execute();
    }

    /**
     * 渲染时间轴录像
     *
     * @param date
     */
    protected void initTimeSeekRecord(String date)
    {
        List<RecordItem> recordItems = (List<RecordItem>) recordMap.get(date);
        if (recordItems == null)
            return;
        List<RecordItem> changeRecordItems = new ArrayList<>();
        for (RecordItem recordItem : recordItems)
        {
            RecordItem recordItem1 = new RecordItem();
            recordItem1.setStartTime(recordItem.getStartTime() / 1000);
            recordItem1.setEndTime(recordItem.getEndTime() / 1000);
            changeRecordItems.add(recordItem1);
        }
        if (timeSeekBar != null)
            timeSeekBar.setRecordList(changeRecordItems);
    }

    /**
     * 改变位置
     *
     * @param position
     */
    protected void changePosition(long position)
    {
        if (session == null)
            return;

        new CameraChangePositionTask(this, session, Long.toString(position))
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
            }
        }.execute();
    }

    /**
     * 通过当前日期获取最早日期记录
     *
     * @param date
     * @return
     */
    protected RecordItem getEarliestTimeByDate(String date)
    {
        List<RecordItem> recordItems = (List<RecordItem>) recordMap.get(date);
        return getEarliestTime(recordItems);
    }

    /**
     * 获取录像最早时间录像
     *
     * @param recordItems
     */
    protected RecordItem getEarliestTime(List<RecordItem> recordItems)
    {
        RecordItem recordItemTemp = new RecordItem();
        recordItemTemp.setStartTime(0);
        for (RecordItem recordItem : recordItems)
        {
            long bt = recordItem.getStartTime();
            if (recordItemTemp.getStartTime() == 0)
                recordItemTemp = recordItem;

            if (bt < recordItemTemp.getStartTime())
            {
                recordItemTemp = recordItem;
            }
        }

        return recordItemTemp;
    }

    /**
     * 停止播放视频
     */
    protected void stopPlay()
    {
        if (session == null)
            return;

        new CameraStopTask(this, session)
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                if (result.getError() != null)
                {
                    session = null;
                }
            }
        }.execute();
    }

    protected void pause()
    {
        if (session == null)
            return;
        new CameraPauseTask(this, session)
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                if (result.getError() == null)
                {
                    ToastHelper.getInstance(BaseCameraPlayActivity.this).shortShowMessage("暂停成功");
                }
                else
                {
                    ToastHelper.getInstance(BaseCameraPlayActivity.this).shortShowMessage(result.getError().getMessage());
                }
            }
        }.execute();
    }

    protected void resume()
    {
        if (session == null)
            return;
        new CameraResumeTask(this, session)
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                if (result.getError() == null)
                {
                    ToastHelper.getInstance(BaseCameraPlayActivity.this).shortShowMessage("恢复播放成功");
                }
                else
                {
                    ToastHelper.getInstance(BaseCameraPlayActivity.this).shortShowMessage(result.getError().getMessage());
                }
            }
        }.execute();
    }

    /**
     * 播放请求
     *
     * @param mediaChannel
     * @param startTime
     * @param endTime
     */
    protected void play(String mediaChannel, String startTime, String endTime, String streamType)
    {
        new CameraPlayHKTask(BaseCameraPlayActivity.this, mediaChannel, camera.getIp(), camera.getPort(),
                camera.getChannel(), camera.getUserName(), camera.getPassword(), streamType, startTime, endTime)
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                if (result.getError() == null)
                {
                    Map<String, Object> map = (Map<String, Object>) result.getResultData();
                    session = (String) map.get("session");
                    Log.e(TAG, "session:" + session);
                    if (isRecorder)
                        changePosition(changePosition);
                    if (!isRequestRecord)
                    {
                        initRecord();
                        isRequestRecord = true;
                    }
                }
                else
                {
                    //ToastHelper.getInstance(CameraPlayHKActivity.this).shortShowMessage(result.getError().getMessage());
                    if (llLoading != null)
                        llLoading.setVisibility(View.GONE);
                    if (llFail != null)
                        llFail.setVisibility(View.VISIBLE);
                }
            }
        }.execute();
    }


}
