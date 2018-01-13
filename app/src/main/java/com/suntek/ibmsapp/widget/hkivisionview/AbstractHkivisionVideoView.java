package com.suntek.ibmsapp.widget.hkivisionview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.tcp.CameraStreamSocketClient;
import com.suntek.ibmsapp.component.tcp.OnCameraStreamDataListener;
import com.suntek.ibmsapp.component.tcp.OnCameraStreamExceptionListener;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.model.RecordItem;
import com.suntek.ibmsapp.page.camera.CameraHKHistoryActivity;
import com.suntek.ibmsapp.task.base.BaseTask;
import com.suntek.ibmsapp.task.camera.control.CameraChangePositionTask;
import com.suntek.ibmsapp.task.camera.control.CameraPlayHKTask;
import com.suntek.ibmsapp.task.camera.control.CameraQueryProgressTask;
import com.suntek.ibmsapp.task.camera.control.CameraQueryRecordTask;
import com.suntek.ibmsapp.task.camera.control.CameraSocketTask;
import com.suntek.ibmsapp.task.camera.control.CameraStopTask;
import com.suntek.ibmsapp.util.DateUtil;
import com.suntek.ibmsapp.util.RecordHander;
import com.suntek.ibmsapp.widget.CustomSurfaceView;
import com.suntek.ibmsapp.widget.ToastHelper;

import org.MediaPlayer.PlayM4.Player;
import org.MediaPlayer.PlayM4.PlayerCallBack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 播放view抽像类,负责实现请求接口
 *
 * @author jimmy
 */
public abstract class AbstractHkivisionVideoView extends FrameLayout
{
    //播放状态
    public static final int PLAYING = 100;
    public static final int PAUSE = 101;
    public static final int STOP = 102;
    public static final int FAIL = 103;
    public static final int PREPARE = 104;

    //清晰度
    //高清
    public static final int STREAM_HIGH_QUALITY = 0;
    //流畅
    public static final int STREAM_FLUENT = 1;

    //当前播放状态
    protected int playerState = STOP;

    //播放view
    protected LinearLayout llVideoView;
    protected CustomSurfaceView videoView;

    protected Context context;

    //取流Ip和端口
    protected String socketIp;
    protected String socketPort;

    //请求session
    private String session;

    private Camera camera;

    //请求任务栈
    private Stack<BaseTask> taskStack;
    //tcp请求类
    protected CameraStreamSocketClient cameraStreamSocketClient;

    protected OnPlayStateListener onPlayStateListener;
    protected OnRecordListener onRecordListener;
    protected OnPlayListener onPlayListener;
    protected OnHistoryRecordListener onHistoryRecordListener;

    //播放器
    protected Player player;
    protected int port;

    protected Timer queryProgressTimer;

    //用于获取截图
    protected int bitmapLen;
    protected int bitmapWidth;
    protected int bitmapHeight;

    //设置录像监听标志
    private boolean isSetRecordSuccess = false;

    private SurfaceHolder surfaceHolder;
    //状态显示容器
    private LinearLayout llStateContent;
    //加载中view
    private View loadingView;
    //播放失败view
    private View failView;
    //暂停时view
    private View stopView;

    protected File recordFile;
    protected FileOutputStream recordOutStream;

    protected AbstractControlView controlView;
    private boolean isShowController = false;

    public AbstractHkivisionVideoView(@NonNull Context context)
    {
        super(context);
        init(context);
    }

    public AbstractHkivisionVideoView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public AbstractHkivisionVideoView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        this.context = context;
        this.taskStack = new Stack<>();
        llVideoView = new LinearLayout(context);
        videoView = new CustomSurfaceView(context);
        //添加视频显示surfaceView
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        videoView.setLayoutParams(layoutParams);
        //surfaceview上的LinerarLayout
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        llVideoView.setLayoutParams(layoutParams);
        llVideoView.addView(videoView);
        this.addView(llVideoView);

        //状态显示view
        LinearLayout.LayoutParams stateParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        stateParams.gravity = Gravity.CENTER;
        llStateContent = new LinearLayout(context);
        llStateContent.setOrientation(LinearLayout.HORIZONTAL);
        llStateContent.setLayoutParams(stateParams);
        llStateContent.setGravity(Gravity.CENTER);
        addView(llStateContent);

        LayoutInflater inflater = LayoutInflater.from(context);
        loadingView = inflater.inflate(R.layout.view_video_loading, null);
        failView = inflater.inflate(R.layout.view_video_fail, null);
        stopView = inflater.inflate(R.layout.view_video_stop, null);
        llStateContent.addView(stopView);
        initSurface();
        initClick();
    }


    private void initClick()
    {
        ImageView ivPlay = (ImageView) stopView.findViewById(R.id.iv_play);
        ivPlay.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onPlayListener != null)
                    onPlayListener.onPlay(v);
            }
        });
        videoView.setOnClickListener(new CustomSurfaceView.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (playerState == PLAYING && controlView != null)
                {
                    if (isShowController)
                    {
                        removeView(controlView);
                        isShowController = false;
                    }
                    else
                    {
                        addView(controlView);
                        isShowController = true;
                    }
                }
            }
        });
    }

    protected void initController(AbstractControlView controller)
    {
        removeView(controlView);
        this.controlView = controller;
    }

    protected void initSurface()
    {
        surfaceHolder = videoView.getHolder();
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
        videoView.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                videoView.setFatherW_H(llVideoView.getRight(), llVideoView.getBottom());
                videoView.setFatherTopAndBottom(llVideoView.getRight(), llVideoView.getBottom());
            }
        }, 100);
    }

    /**
     * 初始化摄像机信息
     *
     * @param camera
     */
    public void initInfo(Camera camera)
    {
        this.camera = camera;
    }

    protected void initSocket(String beginTime, String endTime, int streamType)
    {
        cameraStreamSocketClient = CameraStreamSocketClient.getInstance().
                setOnCameraStreamDataListener(new OnCameraStreamDataListener()
                {
                    @Override
                    public void onReceiveMediaChannel(int mediaChannel)
                    {
                        play(mediaChannel + "", beginTime, endTime, streamType + "");
                        queryProgress();
                    }

                    @Override
                    public void onReceiveMediaHeader(byte[] header, int length, int totalSize, int remainLength)
                    {
                        initPlayer(header);
                    }

                    @Override
                    public void onReceiveVideoData(byte[] videoData, int length, int totalSize, int remainLength)
                    {
                        player.inputData(port, videoData, videoData.length);
                        recordData();
                    }

                    @Override
                    public void onReceiveVoiceData(byte[] voiceData, int length, int totalSize, int remainLength)
                    {

                    }
                }).setOnCameraStreamExceptionListener(new OnCameraStreamExceptionListener()
        {
            @Override
            public void onReceiveDataException(Throwable throwable)
            {

            }

            @Override
            public void onConnectException(Throwable throwable)
            {

            }

            @Override
            public void onHandleDataException(Throwable throwable)
            {

            }
        }).isDebug(true).open(socketIp, socketPort);
    }

    /**
     * 设备录像回调
     */
    private void recordData()
    {
        if (!isSetRecordSuccess)
        {
            isSetRecordSuccess = player.setPreRecordCallBack(port, new PlayerCallBack.PlayerPreRecordCB()
            {
                @Override
                public void onPreRecord(int port, byte[] data, int length)
                {
                    try
                    {
                        recordOutStream.write(data);
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 初始化播放器
     *
     * @param header 视频头
     */
    private void initPlayer(byte[] header)
    {
        stateChange(PLAYING);
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
        player.play(port, videoView.getHolder());
    }

    /**
     * 获取socket地址和端口
     */
    protected void getSocketIp(String beginTime, String endTime, int streamType)
    {
        stateChange(PREPARE);
        BaseTask socketAddressTask = new CameraSocketTask(context)
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                taskStack.pop();
                if (result.getError() == null)
                {
                    Map<String, Object> map = (Map<String, Object>) result.getResultData();
                    socketIp = (String) map.get("ip");
                    socketPort = (String) map.get("port");
                    initSocket(beginTime, endTime, streamType);
                }
                else
                {
                    if (onPlayStateListener != null)
                        onPlayStateListener.onState(FAIL, "获取socket地址失败");
                    stateChange(FAIL);
                }
            }
        };
        socketAddressTask.execute();
        taskStack.push(socketAddressTask);
    }


    /**
     * 播放历史
     *
     * @param mediaChannel 通道号
     * @param beginTime    开始时间
     * @param endTime      结束时间
     * @param streamType   清晰度
     */
    protected void play(String mediaChannel, String beginTime, String endTime, String streamType)
    {
        BaseTask playTask = new CameraPlayHKTask(context, mediaChannel, camera.getIp(), camera.getPort(),
                camera.getChannel(), camera.getUserName(), camera.getPassword(), streamType, beginTime, endTime)
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                taskStack.pop();
                if (result.getError() == null)
                {
                    Map<String, Object> map = (Map<String, Object>) result.getResultData();
                    session = (String) map.get("session");
                    startQueryProgress();
                }
                else
                {
                    stateChange(FAIL);
                }
            }
        };
        playTask.execute();
        taskStack.push(playTask);
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

        new CameraChangePositionTask(context, session, Long.toString(position))
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
            }
        }.execute();
    }

    /**
     * 查询进度
     */
    protected void queryProgress()
    {
        if (session == null)
            return;
        BaseTask queryProgressTask = new CameraQueryProgressTask(context, session)
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                taskStack.pop();
                super.onPostExecute(result);
                if (result.getError() == null)
                {

                }
            }
        };
        taskStack.push(queryProgressTask);
        queryProgressTask.execute();
    }

    /**
     * 停止播放
     */
    protected void stopPlay()
    {
        stateChange(STOP);
        if (session == null)
            return;

        BaseTask stopTask = new CameraStopTask(context, session)
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                taskStack.pop();
                super.onPostExecute(result);
                if (result.getError() != null)
                {
                    session = null;
                }
            }
        };
        stopTask.execute();
        taskStack.push(stopTask);
    }

    /**
     * 获取最近时间
     *
     * @param beginTime
     * @param endTime
     */
    protected void getRecord(String beginTime, String endTime)
    {
        new CameraQueryRecordTask(context, camera.getDeviceId(), camera.getParentId(), camera.getIp(), camera.getChannel(),
                camera.getUserName(), camera.getPassword(), beginTime, endTime, "Hikvision")
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                if (result.getError() == null)
                {
                    List<RecordItem> recordItems = (List<RecordItem>) result.getResultData();
                    Map<String, List<RecordItem>> recordMap = RecordHander.handleRecordList(recordItems);
                    onHistoryRecordListener.onData(recordMap);
                }
                else
                {
                    onHistoryRecordListener.onData(new HashMap<>());
                }
            }
        }.execute();
    }

    public void release()
    {
        //停止请求线程
        for (BaseTask task : taskStack)
        {
            if (!task.isCancelled())
            {
                task.cancel(true);
            }
        }
        //停止播放
        stopPlay();
        if(player != null)
            player.closeStream(port);
        //关闭socket连接
        if (cameraStreamSocketClient != null)
            cameraStreamSocketClient.close();
        //停止时间器
        if (queryProgressTimer != null)
            queryProgressTimer.cancel();
    }

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
     * 返回主线程处理界面
     */
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if (onPlayStateListener != null)
                onPlayStateListener.onState(msg.arg1, "");
            switch (msg.arg1)
            {
                case PREPARE:
                    addStateView(loadingView);
                    playerState = PREPARE;
                    break;
                case PLAYING:
                    removeAllStateView();
                    playerState = PLAYING;
                    break;
                case STOP:
                    playerState = STOP;
                    addStateView(stopView);
                    break;
                case PAUSE:
                    playerState = PAUSE;
                    addStateView(stopView);
                    break;
                case FAIL:
                    playerState = FAIL;
                    addStateView(failView);
                    break;
            }
        }
    };


    private void removeAllStateView()
    {
        llStateContent.removeAllViews();
    }

    private void addStateView(View view)
    {
        llStateContent.removeAllViews();
        llStateContent.addView(view);
    }

    /**
     * 状态更新
     *
     * @param state
     */
    private void stateChange(int state)
    {
        Message message = new Message();
        message.arg1 = state;
        handler.sendMessage(message);
    }


    public interface OnPlayStateListener
    {
        void onState(int state, String message);
    }

    public interface OnRecordListener
    {
        void onRecrodData(File file);
    }

    public interface OnPlayListener
    {
        void onPlay(View view);
    }

    public interface OnHistoryRecordListener
    {
        void onData(Map<String, List<RecordItem>> recordMap);
    }
}
