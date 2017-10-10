package com.suntek.ibmsapp.page.camera;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
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
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.model.Photo;
import com.suntek.ibmsapp.model.RecordItem;
import com.suntek.ibmsapp.page.photo.PhotoListActivity;
import com.suntek.ibmsapp.task.base.BaseTask;
import com.suntek.ibmsapp.task.camera.CameraAddHistoryTask;
import com.suntek.ibmsapp.task.camera.control.CameraChangePositionTask;
import com.suntek.ibmsapp.task.camera.control.CameraPauseTask;
import com.suntek.ibmsapp.task.camera.control.CameraPlayGB28181Task;
import com.suntek.ibmsapp.task.camera.control.CameraPlayHKTask;
import com.suntek.ibmsapp.task.camera.control.CameraQueryProgressTask;
import com.suntek.ibmsapp.task.camera.control.CameraQueryRecordTask;
import com.suntek.ibmsapp.task.camera.control.CameraResumeTask;
import com.suntek.ibmsapp.task.camera.control.CameraStopTask;
import com.suntek.ibmsapp.util.ByteArrayConveter;
import com.suntek.ibmsapp.util.DateUtil;
import com.suntek.ibmsapp.util.FileUtil;
import com.suntek.ibmsapp.util.NiceUtil;
import com.suntek.ibmsapp.util.PermissionRequest;
import com.suntek.ibmsapp.util.SizeUtil;
import com.suntek.ibmsapp.widget.TimeSeekBarView;
import com.suntek.ibmsapp.widget.ToastHelper;
import com.tv.danmaku.ijk.media.widget.media.IjkVideoView;
import com.tv.danmaku.ijk.media.widget.media.OnVideoTouchListener;

import org.MediaPlayer.PlayM4.Player;
import org.MediaPlayer.PlayM4.PlayerCallBack;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 摄像头播放界面
 * <p>
 * 功能
 * ------------------------
 * 1.播放实时视频
 * a.截图(可做)
 * b.录像(可做)
 * c.全屏(可做）
 * d.语音(尚不可做)
 * e.声音(尚不可做)
 * f.云台控制(先做界面)
 * 2.播放历史视频
 * a.可选择日期播放录像(可拖动一天的时间轴)
 * b.下载(分段下载？全天下载？）
 * <p>
 * 初始化处理流程
 * ------------------------
 * 1.界面初始化
 * a.初始化videoView(监听返回错误，加载信息)
 * b.初始化seekBarView(监听时间轴移动动作)
 * c.其它view初始化(其它控件view)
 * <p>
 * 2.数据初始化
 * a.接收上一界面传过来的数据(摄像头信息)
 * b.向视频服务器后台请求播放接口并通知其推流播放实时流
 * <p>
 * 3.线程初始化
 * a.顶部时间线程
 * b.时间轴时间线程
 * <p>
 * 4.各个按钮的点击事件(返回，截图，语音，声音，录像，全屏,云台控制)
 * <p>
 * 历史播放处理流程
 * -------------------------
 * 1.点击进入视频播放视频后，启动时间轴线程让时间轴从当前时间开始移动
 * 2.当点击视频界面后弹出时间轴，此时请求视频后台获得录像时间段并渲染时间轴,此时未进入历史视频
 * 3.移动时间轴停止后，判断是否在历史录像之中，如在则向后台服务器请求播放该时间段推流,
 * 如不在，大于当前时间时进入实时播放，小于于进入前一时间段的录像
 * <p>
 * 遇到问题
 * -------------------------
 * 1.录像时间和时间轴如何同步
 * <p>
 * 可做尚未做
 * -------------------------
 * 1.全屏后的界面显示
 * 2.云台界面显示
 * 3.是否将video做一个controller(需要时间)
 * 4.请求视频后台服务器拿视频播放接口
 * 5.日期选择
 *
 * @author jimmy
 */
public class CameraPlayHKActivity extends BaseActivity implements Runnable,
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
    SurfaceView ivvVideo;
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
    //请求播放线程
    private CameraPlayGB28181Task cameraPlayGB28181Task;
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

    private Socket client;
    private Thread tcpThread;
    private Player player;
    Timer keepLiveTimer;
    int port;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_camera_play_hk;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        camera = (Camera) getIntent().getExtras().getSerializable("camera");

        initSocket();
        //initVideoView();
        //getCameraAddress("2017-08-31 00:00:00", "2017-08-31 23:59:59");
        //getCameraAddress(null, null);
        initTimeView();
        loadData();
        initTimeSeekBarView();
        initRecord();
        DeviceBandwidthSampler.getInstance().startSampling();
        netSpeed();

    }

    private void initSocket()
    {
        tcpThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    client = new Socket("172.16.16.178", 9000);
                    if (client.isConnected())
                        keepLive();
                    else
                        return;
                    // 步骤1：创建输入流对象InputStream
                    InputStream is = client.getInputStream();
                    boolean isStart = false;
                    //流头数据
                    // 1)0x0520:表示MobileServer通知当前TCP连接的MediaChannel号，数据长度为4，数据内容为4字节的MediaChannel号
                    // 2)0x1314:表示MobileServer转发给移动客户端的数据，这些数据是视频或音频数据，也可能是系统头。此时数据长度字段为
                    byte[] header = new byte[2];
                    //长度
                    byte[] lengthByte = new byte[4];
                    //视频数据缓冲
                    byte[] buffer = new byte[1024];
                    //先读取头数据
                    while (is.read(header) != -1)
                    {
                        //连接成功后获取mideaChannel号，发送播放请求
                        if (header[0] == 5 && header[1] == 32 && !isStart)
                        {
                            //获取长度和mediaChannel
                            byte[] length = new byte[8];
                            is.read(length);
                            //将mideaChannel转成整型通道号
                            int mediaChannel = ByteArrayConveter.getInt(length, 4);
                            //请求播放
                            new CameraPlayHKTask(CameraPlayHKActivity.this, mediaChannel + "", camera.getIp(), camera.getPort(),
                                    camera.getChannel(), camera.getUserName(), camera.getPassword(), "1", null, null)
                            {
                                @Override
                                protected void onPostExecute(TaskResult result)
                                {
                                    super.onPostExecute(result);
                                    if (result.getError() == null)
                                    {
                                        Map<String, Object> map = (Map<String, Object>) result.getResultData();
                                        session = (String) map.get("session");
                                    }
                                }
                            }.execute();
                            isStart = true;
                        }
                        //视频流部分
                        else if (header[0] == 19 && header[1] == 20)
                        {
                            //读取长度字节
                            is.read(lengthByte);
                            //数据长度
                            int length = ByteArrayConveter.getInt(lengthByte, 0);
                            Log.e(CameraPlayHKActivity.class.getName(), "数据长度data length:" + length);
                            if (length > 0)
                            {
                                //获取视频流数据部分
                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                int len = -1;
                                int count = length / 1024;
                                int more = length % 1024;
                                for (int i = 0; i < count; i++)
                                {
                                    len = is.read(buffer);
                                    outputStream.write(buffer, 0, len);
                                }
                                if (more != 0)
                                {
                                    is.read(buffer, 0, more);
                                    outputStream.write(buffer, 0, more);
                                }
                                byte[] videoData = outputStream.toByteArray();

                                File file = new File("/sdcard/test.mp4");
                                if(!file.exists())
                                    file.createNewFile();
                                FileOutputStream fileOutputStream = new FileOutputStream("/sdcard/test.mp4",true);
                                // 头一个字节为数据的类型  1为系统头 2为视频流
                                if (videoData[0] == 1)
                                {
                                    //初始化播放器
                                    byte[] head = Arrays.copyOfRange(videoData, 1, videoData.length);
                                    fileOutputStream.write(head);
                                    String TAG = CameraPlayHKActivity.class.getName();
                                    player = Player.getInstance();
                                    port = player.getPort();
                                    Log.e(TAG, "初始化 port:" + port);
                                    Log.e(TAG, "初始化 setStreamOpenMode:" + player.setStreamOpenMode(port, Player.STREAM_REALTIME));
                                    Log.e(TAG, "初始化 openStream:" + player.openStream(port, head, head.length, 600 * 1024));
                                    Log.e(TAG, "初始化 setDisplayBuf:" + player.setDisplayBuf(port, 15));
                                    Log.e(TAG, "初始化 play:" + player.play(port, ivvVideo.getHolder()));
                                }
                                else if (videoData[0] == 2)
                                {
                                    //传入视频流数据
                                    byte[] dataContent = Arrays.copyOfRange(videoData, 1, videoData.length);
                                    fileOutputStream.write(dataContent);
                                    boolean isSuccess = player.inputData(port, dataContent, dataContent.length);
                                    Log.e(CameraPlayHKActivity.class.getName(), "data length:" + dataContent.length
                                            + "   isSuccess:" + isSuccess);
                                }
                            }

                        }

                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        });
        tcpThread.start();
    }

    private void keepLive()
    {
        Timer keepLiveTimer = new Timer();
        keepLiveTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    OutputStream os = client.getOutputStream();
                    byte[] b = new byte[]{5, 33, 0, 0, 0, 0};
                    os.write(b);
                    os.flush();
                    Log.e(CameraPlayHKActivity.class.getName(), "发送保活心跳!!");
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }, 1000, 50000);
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
                    ToastHelper.getInstance(CameraPlayHKActivity.this).shortShowMessage(result.getError().getMessage());
                }
            }
        }.execute();
    }

    private void getCameraAddress(String startTime, String endTime)
    {
        if (cameraPlayGB28181Task != null)
            cameraPlayGB28181Task.cancel(true);
        cameraPlayGB28181Task = new CameraPlayGB28181Task(this, camera.getDeviceId(), camera.getParentId(), camera.getIp(), camera.getChannel(), camera.getUserName(),
                camera.getPassword(), startTime, endTime)
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                if (result.getError() == null)
                {
                    Map<String, Object> map = (Map<String, Object>) result.getResultData();
                    String address = (String) map.get("address");
                    session = (String) map.get("session");
                    if (address != null || !"".equals(address))
                    {
                        try
                        {
                            // ivvVideo.setVideoPath(address);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                            ToastHelper.getInstance(CameraPlayHKActivity.this).shortShowMessage(e.getMessage());
                        }
                        //ivvVideo.start();

                        startQueryProgress();
                    }
                }
                else
                {
                    if (llLoading != null)
                        llLoading.setVisibility(View.GONE);
                    if (llFail != null)
                        llFail.setVisibility(View.VISIBLE);
                }
            }
        };
        cameraPlayGB28181Task.execute();
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
                    reloadVideoView(null, null);
                    tvPlayType.setText("直播");
                }
                else
                {
                    //判断该时间是否有录像
                    if (!isInRecord(date))
                    {
                        //  timeSeekBar.setValue(getLastRecordTime(date).getTime());
                        ToastHelper.getInstance(CameraPlayHKActivity.this).shortShowMessage("该时间点没有录像");
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
                            String beginTime = chooseDate + " 00:00:00";
                            String endTime = chooseDate + " 23:59:59";
                            reloadVideoView(beginTime, endTime);

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

    /**
     * 初始化视频控件
     */
    private void initVideoView()
    {
        player = Player.getInstance();
        port = player.getPort();
        player.setStreamOpenMode(port, Player.STREAM_REALTIME);
        player.openStream(port, null, 0, 600 * 1024);
        player.setDisplayBuf(port, 352 * 288 * 4);
        player.play(port, ivvVideo.getHolder());
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
        //  menu.dismiss();
        //cameraPlayGB28181Task.cancel(true);
        tcpThread.interrupt();
        tcpThread = null;
        //keepLiveTimer.cancel();
        try
        {
            client.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @OnClick(R.id.iv_back)
    public void back(View view)
    {
//        if(menu.isShowing())
//        {
//            menu.dismiss();
//            return;
//        }

        if (playerState == PLAYER_FULLSCREEN)
        {
            NiceUtil.showActionBar(this);
            NiceUtil.scanForActivity(this).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setFullScreenLayout(View.VISIBLE);
            playerState = PLAYER_NOT_FULL;
        }
        else
        {
            finish();
        }
    }


    @OnClick(R.id.ib_snapshot)
    public void takePic(View view)
    {
        PermissionRequest.verifyStoragePermissions(this);
    }

    @OnClick(R.id.ib_talk)
    public void talk(View view)
    {
        ToastHelper.getInstance(this).shortShowMessage("该摄像头不支持通话");
    }

    @OnClick(R.id.ib_voice)
    public void voice(View view)
    {
        ToastHelper.getInstance(this).shortShowMessage("该摄像头不支持播放声音");
    }

    @OnClick(R.id.ib_record)
    public void record(View view)
    {

    }

    @OnClick(R.id.ib_fullscreen)
    public void fullScreen(View view)
    {
        if (playerState == PLAYER_NOT_FULL)
        {
            NiceUtil.hideActionBar(this);
            NiceUtil.scanForActivity(this).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setFullScreenLayout(View.GONE);
            playerState = PLAYER_FULLSCREEN;
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
                    Intent intent = new Intent(CameraPlayHKActivity.this, CameraDownloadActivity.class);
                    startActivity(intent);
                }
            });

            llInfo.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    menu.dismiss();
                    Intent intent = new Intent(CameraPlayHKActivity.this, CameraInfoActivity.class);
                    intent.putExtra("camera", camera);
                    startActivity(intent);
                }
            });
//            llDate.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    menu.dismiss();
//                    Intent intent = new Intent(CameraPlayActivity.this, CameraDateActivity.class);
//                    startActivity(intent);
//                }
//            });
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
            //menu.dismiss();
//            if(menu.isShowing())
//            {
//                menu.dismiss();
//                return true;
//            }
            if (playerState == PLAYER_FULLSCREEN)
            {
                NiceUtil.showActionBar(this);
                NiceUtil.scanForActivity(this).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                setFullScreenLayout(View.VISIBLE);
                playerState = PLAYER_NOT_FULL;
                return true;
            }
            else
            {
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
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) SizeUtil.getRawSize(this, TypedValue.COMPLEX_UNIT_DIP, 320));
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
            //initVideoView();
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

    /**
     * 重新加载videoview进入历史回放
     *
     * @param startTime
     * @param endTime
     */
    private void reloadVideoView(String startTime, String endTime)
    {
//        ivvVideo.stopPlayback();
//        ivvVideo.release(true);
//        ivvVideo.stopBackgroundPlay();
//        IjkMediaPlayer.native_profileEnd();
//        if (!cameraPlayGB28181Task.isCancelled())
//            cameraPlayGB28181Task.cancel(true);
//        stopPlay();
//        initVideoView();
//        if (llFail.getVisibility() == View.VISIBLE)
//            llFail.setVisibility(View.GONE);
//        llLoading.setVisibility(View.VISIBLE);
//        getCameraAddress(startTime, endTime);
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
                    ToastHelper.getInstance(CameraPlayHKActivity.this).shortShowMessage("暂停成功");
                }
                else
                {
                    ToastHelper.getInstance(CameraPlayHKActivity.this).shortShowMessage(result.getError().getMessage());
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
                    ToastHelper.getInstance(CameraPlayHKActivity.this).shortShowMessage("恢复播放成功");
                }
                else
                {
                    ToastHelper.getInstance(CameraPlayHKActivity.this).shortShowMessage(result.getError().getMessage());
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
        Intent intent = new Intent(CameraPlayHKActivity.this, PhotoListActivity.class);
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
                            ToastHelper.getInstance(CameraPlayHKActivity.this).shortShowMessage("该时间无录像");
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
                            reloadVideoView(beginTime, endTime);
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
        cal.add(Calendar.MONTH, -1);
        Date date = cal.getTime();
        String beginTime = format.format(date) + " 00:00:00";

        new CameraQueryRecordTask(this, camera.getDeviceId(), camera.getParentId(), camera.getIp(), camera.getChannel(),
                camera.getUserName(), camera.getPassword(), beginTime, endTime,"Hikvision")
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
                        //String et = format1.format(recordItem.getEndTime());
                        String[] strs = bt.split(" ");
                        String date = strs[0];
                        List<RecordItem> recordItems1 = (List<RecordItem>) recordMap.get(date);
                        if (recordItems1 == null)
                        {
                            recordItems1 = new ArrayList<RecordItem>();
                        }
                        recordItems1.add(recordItem);
                        recordMap.put(date, recordItems1);
                    }

                    //TODO 渲染时间轴
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String date = simpleDateFormat.format(new Date());
                    initTimeSeekRecord(date);
                }
                else
                {
                    ToastHelper.getInstance(CameraPlayHKActivity.this).shortShowMessage(result.getError().getMessage());
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
