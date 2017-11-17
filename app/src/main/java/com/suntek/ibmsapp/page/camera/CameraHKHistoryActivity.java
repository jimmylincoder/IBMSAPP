package com.suntek.ibmsapp.page.camera;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.dsw.calendar.component.MonthView;
import com.dsw.calendar.entity.CalendarInfo;
import com.dsw.calendar.views.GridCalendarView;
import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.facebook.network.connectionclass.DeviceBandwidthSampler;
import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.component.tcp.CameraStreamSocketClient;
import com.suntek.ibmsapp.component.tcp.OnCameraStreamDataListener;
import com.suntek.ibmsapp.component.tcp.OnCameraStreamExceptionListener;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.model.RecordItem;
import com.suntek.ibmsapp.page.photo.PhotoListActivity;
import com.suntek.ibmsapp.task.camera.CameraAddHistoryTask;
import com.suntek.ibmsapp.task.camera.control.CameraChangePositionTask;
import com.suntek.ibmsapp.task.camera.control.CameraPauseTask;
import com.suntek.ibmsapp.task.camera.control.CameraPlayHKTask;
import com.suntek.ibmsapp.task.camera.control.CameraQueryProgressTask;
import com.suntek.ibmsapp.task.camera.control.CameraQueryRecordTask;
import com.suntek.ibmsapp.task.camera.control.CameraResumeTask;
import com.suntek.ibmsapp.task.camera.control.CameraSocketTask;
import com.suntek.ibmsapp.task.camera.control.CameraStopTask;
import com.suntek.ibmsapp.util.BitmapUtil;
import com.suntek.ibmsapp.util.DateUtil;
import com.suntek.ibmsapp.util.FileUtil;
import com.suntek.ibmsapp.util.NiceUtil;
import com.suntek.ibmsapp.util.PermissionRequest;
import com.suntek.ibmsapp.util.PreviewUtil;
import com.suntek.ibmsapp.util.SizeUtil;
import com.suntek.ibmsapp.util.ThumbnailUtil;
import com.suntek.ibmsapp.widget.CustomSurfaceView;
import com.suntek.ibmsapp.widget.TimeSeekBarView;
import com.suntek.ibmsapp.widget.ToastHelper;

import org.MediaPlayer.PlayM4.Player;
import org.MediaPlayer.PlayM4.PlayerCallBack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import butterknife.OnClick;

/**
 * 历史视频播放
 *
 * @author jimmy
 */
public class CameraHKHistoryActivity extends BaseActivity implements Runnable,
        ConnectionClassManager.ConnectionClassStateChangeListener
{
    //全屏
    private final int PLAYER_FULLSCREEN = 0;
    //非全屏
    private final int PLAYER_NOT_FULL = 1;
    //截图保存位置
    private final String PIC_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/ibms";
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
    //时间选择
    private PopupWindow dateChoose;
    //菜单
    private PopupWindow menu;
    //当前日期 yyyy-MM-dd
    private String chooseDate;
    //当前时间handler
    private Handler timeHandler;
    private boolean mBackPressed;
    //是否停止当前时间线程
    private boolean isTimeRun = true;
    //全屏状态
    private int playerState = PLAYER_NOT_FULL;
    //时间轴定时器
    private Timer seekBarTimer;
    //查询进度
    private Timer queryProgressTimer;
    //当前视频会话
    private String session;
    //是否隐藏
    private boolean isBackGround = false;
    //当前摄像头
    private Camera camera;
    //存储录像对应时间段
    private Map<String, Object> recordMap = new HashMap<>();
    //是否为录像状态
    private boolean isRecorder = false;
    //日历选择
    GridCalendarView gcv;
    //记录录像开始时间
    private long recordBeginTime;
    private long changePosition;
    // 最后缓存的字节数
    private long lastTotalRxBytes = 0;
    // 当前缓存时间
    private long lastTimeStamp = 0;
    //网络检测线程
    private Timer netSpeedTimer;
    private boolean isChangeTimeStart;

    private Player player;
    int port;

    //socket ip和端口
    private String socketIp;
    private int socketPort;


    private int bitmapLen;
    private int bitmapWidth;
    private int bitmapHeight;

    //视频流sokcet连接类
    private CameraStreamSocketClient cameraStreamSocketClient;
    private String TAG = CameraHKHistoryActivity.class.getName();


    private String recordFilePath = "/sdcard/DCIM/Camera/ibms/";
    private FileOutputStream fileOutputStream;
    private File recordFile;

    private boolean record = false;
    private boolean isSetRecordSuccess = false;
    private Timer recordTimer;
    private SurfaceHolder surfaceHolder;

    private boolean isRequestRecord = false;
    private int pauseState = 0;
    //缓冲1m
    private static final int MAX_BUFFER_SIZE = 1024 * 1024 * 1;
    private static final int MIN_BUFFER_SIZE = 1;
    private int size = MAX_BUFFER_SIZE;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_camera_history_hk;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        //获取摄像头信息
        camera = (Camera) getIntent().getExtras().getSerializable("camera");
        //获取socket连接ip和端口
        getSocketAddress();
        //初始化视频播放界面
        initSurfaceView();
        //初始化顶部时间
        initTimeView();
        //添加历史记录
        loadData();
        //初始化时间轴
        initTimeSeekBarView();
        //初始化历史记录
        //initRecord();
        //网络测速
        DeviceBandwidthSampler.getInstance().startSampling();
        netSpeed();

    }

    /**
     * 获取socket连接地址和端口
     */
    private void getSocketAddress()
    {
        llLoading.setVisibility(View.VISIBLE);
        new CameraSocketTask(this)
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                if (result.getError() == null)
                {
                    Map<String, Object> map = (Map<String, Object>) result.getResultData();
                    socketIp = (String) map.get("ip");
                    socketPort = Integer.parseInt((String) map.get("port"));
                    initSocket0(null, null);
                }
                else
                {
                    ToastHelper.getInstance(CameraHKHistoryActivity.this).shortShowMessage(result.getError().getMessage());
                }
            }
        }.execute();
    }

    /**
     * 初始化视频界面
     */
    private void initSurfaceView()
    {
//        ivvVideo.setOnClickListener(new CustomSurfaceView.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                if (playerState == PLAYER_NOT_FULL)
//                {
//                    if (llTime.getVisibility() == View.GONE)
//                    {
//                        llOper.setVisibility(View.GONE);
//                        llTime.setVisibility(View.VISIBLE);
//                    }
//                    else
//                    {
//                        llOper.setVisibility(View.VISIBLE);
//                        llTime.setVisibility(View.GONE);
//                    }
//                }
//            }
//        });
        initSurfaceViewSize();

        surfaceHolder = ivvVideo.getHolder();
        surfaceHolder.setKeepScreenOn(true);
        surfaceHolder.setFixedSize(800, 800);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(new SurfaceHolder.Callback()
        {
            @Override
            public void surfaceCreated(SurfaceHolder holder)
            {
                if (player != null)
                    player.play(port, holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
            {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder)
            {
            }
        });


    }

    /**
     * 初始化surface大小
     */
    private void initSurfaceViewSize()
    {
        ivvVideo.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                ivvVideo.setFatherW_H(llSfv.getRight(), llSfv.getBottom());
                ivvVideo.setFatherTopAndBottom(llSfv.getRight(), llSfv.getBottom());
            }
        }, 100);
    }


    private void createRecord()
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat format1 = new SimpleDateFormat("HHmmss");
        String fileName = "VID_" + format.format(new Date()) + "_" + format1.format(new Date()) + ".mp4";
        recordFile = new File(recordFilePath + fileName);
        try
        {
            recordFile.createNewFile();
            fileOutputStream = new FileOutputStream(recordFilePath + fileName);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 初始化 tcp连接
     *
     * @param beginTime
     * @param endTime
     */
    private void initSocket0(String beginTime, String endTime)
    {
        cameraStreamSocketClient = CameraStreamSocketClient.getInstance()
                .setOnCameraStreamDataListener(new OnCameraStreamDataListener()
                {
                    @Override
                    public void onReceiveMediaChannel(int mediaChannel)
                    {
                        //接收到mediaChannel就请求播放
                        play(mediaChannel + "", beginTime, endTime);
                        startQueryProgress();
                    }

                    @Override
                    public void onReceiveMediaHeader(byte[] header, int length, int totalSize, int remainLength)
                    {
                        //接收到头就初始化播放器
                        initPlayer(header);
                    }

                    @Override
                    public void onReceiveVideoData(byte[] videoData, int length, int totalSize, int remainLength)
                    {
                        //处理视频数据
                        handleVideoData(videoData);
                    }

                    @Override
                    public void onReceiveVoiceData(byte[] voiceData, int length, int totalSize, int remainLength)
                    {

                    }
                })
                .setOnCameraStreamExceptionListener(new OnCameraStreamExceptionListener()
                {
                    @Override
                    public void onReceiveDataException(Throwable throwable)
                    {
                        //Log.e(CameraPlayHKActivity.class.getName(), "异常" + throwable.getMessage());
                    }

                    @Override
                    public void onConnectException(Throwable throwable)
                    {

                    }

                    @Override
                    public void onHandleDataException(Throwable throwable)
                    {

                    }
                }).open(socketIp, socketPort + "");
    }

    /**
     * 视频数据处理
     *
     * @param videoData 视频数据
     */
    private void handleVideoData(byte[] videoData)
    {
        Log.e(TAG, "接收到数据 长度:" + videoData.length + " 接收到时间:" + new Date().getTime());
        player.inputData(port, videoData, videoData.length);
        dataBuffer();
        if (!isSetRecordSuccess)
        {
            isSetRecordSuccess = player.setPreRecordCallBack(port, new PlayerCallBack.PlayerPreRecordCB()
            {
                @Override
                public void onPreRecord(int port, byte[] data, int length)
                {
                    Log.e(TAG, "录像中,录像文件地址:" + recordFilePath);
                    writeToFile(data);
                }
            });
        }
    }

    private void dataBuffer()
    {
        //已缓冲多少
        int bufferSize = player.getSourceBufferRemain(port);

        Log.e(TAG, "缓冲大小：" + bufferSize);
        if (bufferSize != -1 && bufferSize > 0 && bufferSize < size)
        {
            size = MAX_BUFFER_SIZE;
            float percent = (float) bufferSize / size;
            Log.e(TAG, "缓冲大小：" + bufferSize + " 缓冲比例:" + percent);
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (tvLoadPercent != null)
                        tvLoadPercent.setText("(" + (int) (percent * 100) + "%)");
                }
            });
            if (pauseState == 0)
            {
                Log.e(TAG, "缓冲中");
                pauseState = 1;
                player.pause(port, pauseState);
            }
        }
        else
        {
            if (pauseState == 1)
            {
                Log.e(TAG, "缓冲结束");
                player.pause(port, 0);
                pauseState = 0;
                size = MIN_BUFFER_SIZE;

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (llLoading != null && llLoading.getVisibility() == View.VISIBLE)
                            llLoading.setVisibility(View.GONE);
                    }
                });
            }
        }

    }

    /**
     * 初始化播放器
     *
     * @param header 视频头
     */
    private void initPlayer(byte[] header)
    {
        size = MAX_BUFFER_SIZE;
        if (player == null)
            player = Player.getInstance();
        else
        {
            player.freePort(port);
            player.closeStream(port);
        }
        port = player.getPort();
        player.setStreamOpenMode(port, Player.STREAM_REALTIME);
        player.openStream(port, header, header.length, 100000 * 1024);
        player.setDisplayBuf(port, 15);
        player.setPreRecordFlag(port, false);
        player.setDisplayCB(port, new PlayerCallBack.PlayerDisplayCB()
        {
            @Override
            public void onDisplay(int port, byte[] data, int dataLen, int width,
                                  int height, int frameTime, int type, int reserved)
            {
                bitmapLen = dataLen;
                bitmapWidth = width;
                bitmapHeight = height;
            }
        });
        player.play(port, surfaceHolder);
        player.playSound(port);
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (llLoading != null && llLoading.getVisibility() == View.GONE)
                    llLoading.setVisibility(View.VISIBLE);
            }
        });
    }

    private void writeToFile(byte[] data)
    {
        try
        {
            fileOutputStream.write(data);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 播放请求
     *
     * @param mediaChannel
     * @param startTime
     * @param endTime
     */
    private void play(String mediaChannel, String startTime, String endTime)
    {
        new CameraPlayHKTask(CameraHKHistoryActivity.this, mediaChannel, camera.getIp(), camera.getPort(),
                camera.getChannel(), camera.getUserName(), camera.getPassword(), "0", startTime, endTime)
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
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        long nowTotalRxBytes = TrafficStats.getUidRxBytes(getApplicationInfo().uid)
                                == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);
                        long nowTimeStamp = System.currentTimeMillis(); // 当前时间
                        // kb/s
                        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp == lastTimeStamp ? nowTimeStamp : nowTimeStamp
                                - lastTimeStamp));// 毫秒转换
                        lastTimeStamp = nowTimeStamp;
                        lastTotalRxBytes = nowTotalRxBytes;
                        final ConnectionQuality connectionQuality = ConnectionClassManager.getInstance().getCurrentBandwidthQuality();
                        String netState = "";
                        final double downloadKBitsPerSecond = ConnectionClassManager.getInstance().getDownloadKBitsPerSecond();
                        if (tvNetState != null && tvNetSpeed != null)
                        {
                            switch (connectionQuality)
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
                            tvNetSpeed.setText(speed + " kb/s");
                        }
                    }
                });
            }
        }, 1000, 1000);
    }

    @Override
    public void loadData()
    {
        String cameraId = camera.getId();
        String cameraName = camera.getName();
        if (cameraName != null)
            tvCameraName.setText(cameraName);

        new CameraAddHistoryTask(this, cameraId)
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                if (result.getError() == null)
                {

                }
                else
                {
                    ToastHelper.getInstance(CameraHKHistoryActivity.this).shortShowMessage(result.getError().getMessage());
                }
            }
        }.execute();
    }


    /**
     * 初始化时间轴
     */
    private void initTimeSeekBarView()
    {
        timeSeekBar.setOnValueChangeListener(new TimeSeekBarView.OnValueChangeListener()
        {
            @Override
            public void onValueChange(Date date)
            {
                //TODO 当移动时间轴变化时将其显示在视频中心
                //如果已经开始移动就显示
                if (isChangeTimeStart)
                {
                    llChangeTime.setVisibility(View.VISIBLE);
                    tvChangeTime.setText(DateUtil.convertByFormat(date, "MM/dd HH:mm:ss"));
                    tvNowTime.setText(DateUtil.convertByFormat(date, "MM/dd HH:mm:ss"));
                }
            }

            @Override
            public void onStartValueChange(Date date)
            {
                //开始移动
                isChangeTimeStart = true;
            }

            @Override
            public void onStopValueChange(Date date)
            {
                if (isChangeTimeStart)
                {
                    llChangeTime.setVisibility(View.GONE);
                    tvNowTime.setText(DateUtil.convertByFormat(date, "MM/dd HH:mm:ss"));
                    isChangeTimeStart = false;
                }
                //TODO 移动到最终位置后，如果该位置没有录像的话，就移向前面有录像的一段，如果时间大于当前时间的话，则播放实时视频
                //如果大于当前时间则反回当前时间
                if (date.getTime() > new Date().getTime())
                {
                    timeSeekBar.setValue(new Date().getTime());
                    tvNowTime.setText(DateUtil.convertByFormat(new Date(), "MM/dd HH:mm:ss"));
                    isRecorder = false;
                    cameraStreamSocketClient.close();
                    stopPlay();
                    initSocket0(null, null);
                    tvPlayType.setText("直播");
                }
                else
                {
                    //判断该时间是否有录像
                    if (!isInRecord(date))
                    {
                        //  timeSeekBar.setValue(getLastRecordTime(date).getTime());
                        ToastHelper.getInstance(CameraHKHistoryActivity.this).shortShowMessage("该时间点没有录像");
                    }
                    else
                    {
                        tvPlayType.setText("回放");
                        cancelSeekBarTimer();

                        Date dayBegin = new Date(getEarliestTimeByDate(chooseDate).getStartTime());
                        if (!isRecorder)
                        {
                            //设备为录像，并加载当天起始录像点到23:59:59,然后移位置到当前
                            isRecorder = true;
                            String beginTime = DateUtil.convertByFormat(getEarliestTimeByDate(chooseDate)
                                    .getStartTime(), "yyyy-MM-dd HH:mm:ss");
                            String endTime = chooseDate + " 23:59:59";
                            //reloadVideoView(beginTime, endTime);
                            cameraStreamSocketClient.close();
                            stopPlay();
                            initSocket0(beginTime, endTime);
                            changePosition = (date.getTime() - dayBegin.getTime()) / 1000;
                        }
                        else
                        {
                            //如果已加载录像流则只改变播放流位置即可
                            long position = (date.getTime() - dayBegin.getTime()) / 1000;
                            changePosition(position);
                        }
                    }
                }
            }
        });
        chooseDate = DateUtil.convertYYYY_MM_DD(new Date());
        startSeekBarTimer();

    }

    /**
     * 启动时间轴定时器
     */
    private void startSeekBarTimer()
    {
        if (seekBarTimer == null)
        {
            seekBarTimer = new Timer();
        }
        seekBarTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (timeSeekBar != null)
                        {
                            timeSeekBar.updateTime(1);
                        }
                    }
                });
            }
        }, 1000, 1000);
    }

    /**
     * 取消时间轴时间线程
     */
    private void cancelSeekBarTimer()
    {
        if (seekBarTimer != null)
        {
            seekBarTimer.cancel();
            seekBarTimer.purge();
            seekBarTimer = null;
        }
    }

    /**
     * 判断是否已在录像内
     *
     * @return
     */
    private boolean isInRecord(Date date)
    {
        boolean isContain = false;
        List<RecordItem> recordList = (List<RecordItem>) recordMap.get(chooseDate);
        if (recordList == null)
            return false;
        for (RecordItem map : recordList)
        {
            long beginTime = map.getStartTime();
            long endTime = map.getEndTime();
            long nowTime = date.getTime();
            if (nowTime > beginTime && nowTime < endTime)
            {
                isContain = true;
                break;
            }
        }
        return isContain;
    }

    /**
     * 获取最近的录像时间
     *
     * @param date
     * @return
     */
    private Date getLastRecordTime(Date date)
    {
        List<RecordItem> lastNowTime = new ArrayList<>();
        long nowTime = date.getTime();
        long theLastedTime = 0;
        long theLast = 0;
        List<RecordItem> recordList = (List<RecordItem>) recordMap.get(chooseDate);
        for (RecordItem map : recordList)
        {
            long endTime = map.getEndTime();
            if (nowTime > endTime)
            {
                lastNowTime.add(map);
            }
        }

        for (RecordItem map : lastNowTime)
        {
            long endTime = map.getEndTime();
            long beginTime = map.getStartTime();
            if (theLast != 0)
            {
                long temp = nowTime - endTime;
                if (temp < theLast)
                {
                    theLast = temp;
                    theLastedTime = beginTime;
                }
            }
            else
            {
                theLast = nowTime - endTime;
                theLastedTime = beginTime;
            }
        }

        return new Date(theLastedTime);
    }

    /**
     * 初始化时间
     */
    private void initTimeView()
    {
        tvNowTime.setText(DateUtil.convertByFormat(new Date(), "MM/dd HH:mm:ss"));
        timeHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if (tvNowTime != null)
                {
                    String date = tvNowTime.getText().toString();
                    //Calendar calendar = Calendar.getInstance();
                    String year = chooseDate.substring(0, 4);
                    long time = DateUtil.convertToLong(year + "/" + date, "yyyy/MM/dd HH:mm:ss");
                    Date newDate = new Date(time + 1000);
                    tvNowTime.setText(DateUtil.convertByFormat(newDate, "MM/dd HH:mm:ss"));
                }
            }
        };
        //时间线程
        new Thread(this).start();
    }

    @Override
    public void initToolBar()
    {

    }

    @Override
    public void onBackPressed()
    {
        mBackPressed = true;
        super.onBackPressed();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        isBackGround = true;
    }

    @Override
    protected void onDestroy()
    {
        isTimeRun = false;
        if (seekBarTimer != null)
            seekBarTimer.cancel();
        if (netSpeedTimer != null)
            netSpeedTimer.cancel();
        if (queryProgressTimer != null)
            queryProgressTimer.cancel();
        stopPlay();
        if (cameraStreamSocketClient != null)
            cameraStreamSocketClient.close();
        super.onDestroy();
    }

    private Bitmap getBitmap()
    {
        byte[] bitmap = new byte[bitmapWidth * bitmapHeight * 3 / 2];
        Player.MPInteger mpInteger = new Player.MPInteger();
        boolean isSuccess = player.getJPEG(port, bitmap, bitmapLen, mpInteger);
        Bitmap bitmap1 = BitmapFactory.decodeByteArray(bitmap, 0, bitmapLen);
        return bitmap1;
    }

    @OnClick(R.id.iv_back)
    public void back(View view)
    {
        if (playerState == PLAYER_FULLSCREEN)
        {
            NiceUtil.showActionBar(this);
            NiceUtil.scanForActivity(this).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setFullScreenLayout(View.VISIBLE);
            playerState = PLAYER_NOT_FULL;

            surfaceHolder.setFixedSize(800, 800);
            ivvVideo.setStart_Top(-1);
            initSurfaceViewSize();
        }
        else
        {
            savePreview();
            finish();
        }
    }

    /**
     * 缓存预览图片
     */
    private void savePreview()
    {
        byte[] bitmap = new byte[bitmapWidth * bitmapHeight * 3 / 2];
        Player.MPInteger mpInteger = new Player.MPInteger();
        if (player != null)
        {
            player.getJPEG(port, bitmap, bitmapLen, mpInteger);
            Bitmap bitmap1 = BitmapFactory.decodeByteArray(bitmap, 0, bitmapLen);
            if (bitmap1 != null)
                PreviewUtil.getInstance().savePreview(bitmap1, camera.getId(), camera.getDeviceId());
        }
    }


    @OnClick(R.id.ib_snapshot)
    public void takePic(View view)
    {
        PermissionRequest.verifyStoragePermissions(this);
        File localFile = new File(PIC_PATH);
        localFile.mkdirs();
        try
        {
            byte[] bitmap = new byte[bitmapWidth * bitmapHeight * 3 / 2];
            Player.MPInteger mpInteger = new Player.MPInteger();
            boolean isSuccess = player.getJPEG(port, bitmap, bitmapLen, mpInteger);
            Log.e(TAG, "截图结果: " + isSuccess);
            Bitmap bitmap1 = BitmapFactory.decodeByteArray(bitmap, 0, bitmapLen);
            Bitmap preview = BitmapUtil.centerSquareScaleBitmap(bitmap1, 100);
            llTakePic.setVisibility(View.VISIBLE);
            ivTakePic.setImageBitmap(preview);
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Thread.sleep(3000);
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (llTakePic != null)
                                    llTakePic.setVisibility(View.GONE);
                            }
                        });
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }).start();
            FileUtil.saveImageToGallery(this, bitmap1);
            ToastHelper.getInstance(this).shortShowMessage("截图成功");
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.ib_talk)
    public void talk(View view)
    {
        //ToastHelper.getInstance(this).shortShowMessage("该摄像头不支持通话");
        Intent intent = new Intent(CameraHKHistoryActivity.this, CameraHKHistoryActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ib_voice)
    public void voice(View view)
    {
        ToastHelper.getInstance(this).shortShowMessage("该摄像头不支持播放声音");
        //player.pause(port, 1);
    }

    @OnClick(R.id.ib_record)
    public void record(View view)
    {
        if (recordTimer == null)
            recordTimer = new Timer();
        if (!record)
        {
            createRecord();
            isSetRecordSuccess = false;
            player.setPreRecordFlag(port, true);
            ToastHelper.getInstance(this).shortShowMessage("录像开始");
            llRecord.setVisibility(View.VISIBLE);
            Log.e(TAG, "录像开始");
            recordBeginTime = new Date().getTime();
            recordTimer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    int recordTime = (int) ((new Date().getTime() - recordBeginTime) / 1000);
                    if (recordTime < 3600)
                    {
                        String min = (recordTime / 60) + "";
                        String sec = (recordTime % 60) + "";

                        min = min.length() == 2 ? min : "0" + min;
                        sec = sec.length() == 2 ? sec : "0" + sec;

                        String finalMin = min;
                        String finalSec = sec;
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (tvRecordTime != null)
                                    tvRecordTime.setText(finalMin + ":" + finalSec);
                                if (ivPot.getVisibility() == View.VISIBLE)
                                    ivPot.setVisibility(View.INVISIBLE);
                                else
                                    ivPot.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            }, 0, 1000);
            record = true;
        }
        else
        {
            recordTimer.cancel();
            recordTimer = null;
            player.setPreRecordFlag(port, false);
            record = false;
            llRecord.setVisibility(View.GONE);

            int recordDuration = (int) ((new Date().getTime() - recordBeginTime) / 1000);

            if (recordDuration > 2 && recordFile.length() > 0)
            {
                ToastHelper.getInstance(this).shortShowMessage("录像结束");
                ThumbnailUtil.getInstance().display(recordFile.getAbsolutePath(), new ThumbnailUtil.OnGetThumbnail()
                {
                    @Override
                    public void onThumbnail(Bitmap bitmap)
                    {
                        PreviewUtil.getInstance().saveVideoPreview(bitmap, recordFile.getName() + "_thumbnail");
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                llTakePic.setVisibility(View.VISIBLE);
                                ivTakePic.setImageBitmap(bitmap);
                            }
                        });
                        new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try
                                {
                                    Thread.sleep(3000);
                                    runOnUiThread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            if (llTakePic != null)
                                                llTakePic.setVisibility(View.GONE);
                                        }
                                    });
                                } catch (InterruptedException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
                Log.e(TAG, "录像结束");
            }
            else
            {
                ToastHelper.getInstance(this).shortShowMessage("录像时间过短");
                recordFile.delete();
            }
        }
    }

    @OnClick(R.id.ib_fullscreen)
    public void fullScreen(View view)
    {
        if (playerState == PLAYER_NOT_FULL)
        {
            ivvVideo.setStart_Top(-1);
            //surfaceHolder.setFixedSize(800, 1920);
//            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) ivvVideo.getLayoutParams();
//            layoutParams.gravity = Gravity.CENTER;
//            layoutParams.weight = ViewGroup.LayoutParams.MATCH_PARENT;
//            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
//            ivvVideo.setLayoutParams(layoutParams);

            NiceUtil.hideActionBar(this);
            NiceUtil.scanForActivity(this).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setFullScreenLayout(View.GONE);
            playerState = PLAYER_FULLSCREEN;

            initSurfaceViewSize();
        }
    }

    @OnClick(R.id.ll_more)
    public void more(View view)
    {
        if (menu == null)
        {
            View view1 = getLayoutInflater().inflate(R.layout.view_pop_menu, null);
            menu = new PopupWindow(view1, ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            //menu.setAnimationStyle(R.style.Popupwindow);
            LinearLayout llDownload = (LinearLayout) view1.findViewById(R.id.ll_download);
            LinearLayout llInfo = (LinearLayout) view1.findViewById(R.id.ll_info);
            //LinearLayout llDate = (LinearLayout) view1.findViewById(R.id.ll_date);
            llDownload.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    menu.dismiss();
                    Intent intent = new Intent(CameraHKHistoryActivity.this, CameraDownloadActivity.class);
                    startActivity(intent);
                }
            });

            llInfo.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    menu.dismiss();
                    Intent intent = new Intent(CameraHKHistoryActivity.this, CameraInfoActivity.class);
                    intent.putExtra("camera", camera);
                    startActivity(intent);
                }
            });
            menu.setOutsideTouchable(true);
            menu.showAsDropDown(view);
        }
        else
        {
            if (menu.isShowing())
                menu.dismiss();
            else
                menu.showAsDropDown(view);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == event.KEYCODE_BACK)
        {
            if (playerState == PLAYER_FULLSCREEN)
            {
                NiceUtil.showActionBar(this);
                NiceUtil.scanForActivity(this).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                setFullScreenLayout(View.VISIBLE);
                playerState = PLAYER_NOT_FULL;

                surfaceHolder.setFixedSize(800, 800);
                ivvVideo.setStart_Top(-1);
                initSurfaceViewSize();
                return true;
            }
            else
            {
                savePreview();
                finish();
            }
        }
        return true;
    }

    private void setFullScreenLayout(int viewType)
    {
        if (viewType == View.GONE)
        {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            flVideo.setLayoutParams(layoutParams);
        }
        else
        {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) SizeUtil.getRawSize(this, TypedValue.COMPLEX_UNIT_DIP, 250));
            flVideo.setLayoutParams(layoutParams);
        }
        llHead.setVisibility(viewType);
        llOper.setVisibility(viewType);
        llMessage.setVisibility(viewType);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        ConnectionClassManager.getInstance().register(this);
        if (isBackGround)
        {
            isBackGround = false;
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        ConnectionClassManager.getInstance().remove(this);
    }

    @Override
    public void run()
    {
        try
        {
            while (isTimeRun)
            {
                String str = DateUtil.convertByFormat(new Date(), "MM/dd HH:mm:ss");
                timeHandler.sendMessage(timeHandler.obtainMessage(100, str));
                Thread.sleep(1000);
            }
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放视频
     */
    private void stopPlay()
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

    private void pause()
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
                    ToastHelper.getInstance(CameraHKHistoryActivity.this).shortShowMessage("暂停成功");
                }
                else
                {
                    ToastHelper.getInstance(CameraHKHistoryActivity.this).shortShowMessage(result.getError().getMessage());
                }
            }
        }.execute();
    }

    private void resume()
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
                    ToastHelper.getInstance(CameraHKHistoryActivity.this).shortShowMessage("恢复播放成功");
                }
                else
                {
                    ToastHelper.getInstance(CameraHKHistoryActivity.this).shortShowMessage(result.getError().getMessage());
                }
            }
        }.execute();
    }

    @OnClick(R.id.iv_pause)
    public void pause(View view)
    {
        pause();
    }

    @OnClick(R.id.iv_play)
    public void resume(View view)
    {
        resume();
    }

    @OnClick(R.id.iv_take_pic)
    public void jumpPhotoList(View view)
    {
        Intent intent = new Intent(CameraHKHistoryActivity.this, PhotoListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.tv_nowtime)
    public void date(View view)
    {
        if (dateChoose == null)
        {
            View view1 = getLayoutInflater().inflate(R.layout.view_popup_daee, null);
            dateChoose = new PopupWindow(view1, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            dateChoose.setOutsideTouchable(true);
            dateChoose.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
            gcv = (GridCalendarView) view1.findViewById(R.id.gcv_date);
            initDateChoose();
            gcv.setDateClick(new MonthView.IDateClick()
            {
                @Override
                public void onClickOnDate(int year, int month, int day)
                {
                    //   ToastHelper.getInstance(CameraPlayActivity.this).shortShowMessage(year + " " + month + " " + day);
                    String monthStr = month < 10 ? "0" + month : month + "";
                    String dayString = day < 10 ? "0" + day : day + "";

                    chooseDate = year + "-" + monthStr + "-" + dayString;
                    if (recordMap != null)
                    {
                        List<RecordItem> recordItems = (List<RecordItem>) recordMap.get(chooseDate);
                        if (recordItems == null)
                        {
                            ToastHelper.getInstance(CameraHKHistoryActivity.this).shortShowMessage("该时间无录像");
                        }
                        else
                        {
                            isRecorder = true;
                            initTimeSeekRecord(chooseDate);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            RecordItem recordItem = getEarliestTime(recordItems);
                            cancelSeekBarTimer();
                            timeSeekBar.setValue(recordItem.getStartTime());
                            tvNowTime.setText(DateUtil.convertByFormat(recordItem.getStartTime(), "MM/dd HH:mm:ss"));
                            String beginTime = format.format(new Date(recordItem.getStartTime()));
                            String endTime = chooseDate + " 23:59:59";
                            cameraStreamSocketClient.close();
                            initSocket0(beginTime, endTime);
                        }
                    }
                    //TODO 加载至该天有录像的地方  重新加载顶部时间
                    dateChoose.dismiss();
                }
            });
        }
        else
        {
            dateChoose.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        }
    }

    /**
     * 请求查询进度线程
     */
    private void startQueryProgress()
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
                    initTimeSeekRecord(date);
                }
                else
                {
                    ToastHelper.getInstance(CameraHKHistoryActivity.this).shortShowMessage(result.getError().getMessage());
                }
            }
        }.execute();
    }

    /**
     * 渲染时间轴录像
     *
     * @param date
     */
    private void initTimeSeekRecord(String date)
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
     * 初始化录像选择
     */
    private void initDateChoose()
    {
        List<CalendarInfo> calendarInfos = new ArrayList<>();
        if (recordMap != null)
        {
            for (Map.Entry<String, Object> entry : recordMap.entrySet())
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
        gcv.setCalendarInfos(calendarInfos);
    }

    /**
     * 通过当前日期获取最早日期记录
     *
     * @param date
     * @return
     */
    private RecordItem getEarliestTimeByDate(String date)
    {
        List<RecordItem> recordItems = (List<RecordItem>) recordMap.get(date);
        return getEarliestTime(recordItems);
    }

    /**
     * 获取录像最早时间录像
     *
     * @param recordItems
     */
    private RecordItem getEarliestTime(List<RecordItem> recordItems)
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
     * 改变位置
     *
     * @param position
     */
    private void changePosition(long position)
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

    @Override
    public void onBandwidthStateChange(ConnectionQuality bandwidthState)
    {
        Log.e(CameraPlayActivity.class.getName(), "网速：" + bandwidthState.toString());
    }

}
