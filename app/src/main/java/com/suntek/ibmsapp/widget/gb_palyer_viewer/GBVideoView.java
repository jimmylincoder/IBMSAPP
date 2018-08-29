package com.suntek.ibmsapp.widget.gb_palyer_viewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.model.RecordItem;
import com.suntek.ibmsapp.task.base.BaseTask;
import com.suntek.ibmsapp.task.camera.control.CameraChangePositionTask;
import com.suntek.ibmsapp.task.camera.control.CameraPauseTask;
import com.suntek.ibmsapp.task.camera.control.CameraPlayGB28181Task;
import com.suntek.ibmsapp.task.camera.control.CameraQueryProgressTask;
import com.suntek.ibmsapp.task.camera.control.CameraQueryRecordTask;
import com.suntek.ibmsapp.task.camera.control.CameraResumeTask;
import com.suntek.ibmsapp.task.camera.control.CameraStopTask;
import com.suntek.ibmsapp.util.RecordHander;
import com.suntek.ibmsapp.widget.hkivisionview.AbstractControlView;
import com.suntek.ibmsapp.widget.hkivisionview.AbstractHkivisionVideoView;
import com.suntek.ibmsapp.widget.ijkmedia.media.IjkVideoView;
import com.suntek.ibmsapp.widget.ijkmedia.media.OnVideoTouchListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 国标rtmp播放view
 *
 * @author jimmy
 */
public class GBVideoView extends FrameLayout
{
    //播放状态
    public static final int PLAYING = 100;
    public static final int PAUSE = 101;
    public static final int STOP = 102;
    public static final int FAIL = 103;
    public static final int PREPARE = 104;

    //摄像机信息
    private Camera camera;
    private Context context;
    //状态显示容器
    private LinearLayout llStateContent;
    //加载中view
    private View loadingView;
    //播放失败view
    private View failView;
    //暂停时view
    private View stopView;
    //视频播放view
    private IjkVideoView videoView;

    protected OnPlayStateListener onPlayStateListener;

    protected OnPlayListener onPlayListener;
    protected OnFailListener onFailListener;
    protected OnHistoryRecordListener onHistoryRecordListener;


    //当前播放状态
    protected int playerState = STOP;

    //请求任务栈
    private Stack<BaseTask> taskStack;

    protected Timer queryProgressTimer;

    private String session;

    protected AbstractControlView controlView;
    private boolean isShowController = false;

    public GBVideoView(@NonNull Context context)
    {
        super(context);
        init(context);
    }

    public GBVideoView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init(context);

    }

    public GBVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        this.context = context;
        this.taskStack = new Stack<>();
        //设置背景色
        setBackground(context.getDrawable(R.color.C0C0C0E));
        videoView = new IjkVideoView(context);
        addView(videoView);

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

        initVideoView();
        initClick();
    }

    public int getState()
    {
        return playerState;
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
        failView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onFailListener != null)
                    onFailListener.onClick(v);
            }
        });
    }

    /**
     * 初始化视频控件
     */
    private void initVideoView()
    {
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        videoView.setRender(IjkVideoView.RENDER_TEXTURE_VIEW);
        videoView.setOnVideoTouchListener(new OnVideoTouchListener()
        {
            @Override
            public void onSingleTouchEvent(MotionEvent ev)
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

            @Override
            public void onDoubleTouchEvent(MotionEvent ev)
            {
                videoView.toggleAspectRatio();
            }
        });

        videoView.setOnInfoListener(new IMediaPlayer.OnInfoListener()
        {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1)
            {
                if (i == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START)
                {
                    stateChange(PLAYING);
                }
                return false;
            }
        });
        videoView.setOnErrorListener(new IMediaPlayer.OnErrorListener()
        {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1)
            {
                stateChange(FAIL);
                return false;
            }
        });
    }

    public void initController(AbstractControlView controller)
    {
        removeView(controlView);
        this.controlView = controller;
    }

    public void getRecordByDate(String date, OnHistoryRecordListener onHistoryRecordListener)
    {
        this.onHistoryRecordListener = onHistoryRecordListener;
        String beginTime = date + " 00:00:00";
        String endTime = date + " 23:59:59";
        getRecord(beginTime, endTime);
    }

    public void seekTo(long position)
    {
        changePosition(position);
    }

    /**
     * 播放实时视频
     */
    public void playReal()
    {
        play(null, null);
    }

    /**
     * 播放历史视频
     *
     * @param beginTime 开始时间
     * @param endTime   结束时间
     */
    public void playHistory(String beginTime, String endTime)
    {
        play(beginTime, endTime);
    }

    public Bitmap takePic()
    {
        Bitmap bitmap = videoView.takePicture();
        return bitmap;
    }

    public void pause()
    {
        pausePlay();
    }

    public void resume()
    {
        resumePlay();
    }

    public void stop()
    {
        //停止请求线程
        for (BaseTask task : taskStack)
        {
            if (!task.isCancelled())
            {
                task.cancel(true);
            }
        }
        if (videoView != null)
        {
            videoView.stopPlayback();
            //videoView.release(true);
            videoView.stopBackgroundPlay();
        }
        //停止播放
        stopPlay();
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
        if (videoView != null)
        {
            videoView.stopPlayback();
            videoView.release(true);
            videoView.stopBackgroundPlay();
            IjkMediaPlayer.native_profileEnd();
            videoView = null;
        }
        //停止播放
        stopPlay();
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

        BaseTask changePostionTask = new CameraChangePositionTask(context, session, Long.toString(position))
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
            }
        };
        changePostionTask.execute();
        taskStack.push(changePostionTask);
    }

    /**
     * 获取最近时间
     *
     * @param beginTime
     * @param endTime
     */
    protected void getRecord(String beginTime, String endTime)
    {
        BaseTask recordTask = new CameraQueryRecordTask(context, camera.getDeviceId(), camera.getParentId(),
                beginTime, endTime, "GB28181")
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
        };
        recordTask.execute();
        taskStack.push(recordTask);
    }


    private void resumePlay()
    {
        if (session == null)
            return;
        new CameraResumeTask(context, session)
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                if (result.getError() == null)
                {
                    stateChange(PLAYING);
                }
                else
                {
                    stateChange(FAIL);
                }
            }
        }.execute();
    }

    private void pausePlay()
    {
        if (session == null)
            return;
        new CameraPauseTask(context, session)
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                if (result.getError() == null)
                {
                    stateChange(PAUSE);
                }
                else
                {
                    stateChange(FAIL);
                }
            }
        }.execute();
    }

    /**
     * 停止播放视频
     */
    private void stopPlay()
    {
        if (session == null)
            return;
        stateChange(STOP);
        BaseTask stopTask = new CameraStopTask(context, session)
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
        };
        stopTask.execute();
        taskStack.push(stopTask);
    }


    private void play(String beginTime, String endTime)
    {
        stateChange(PREPARE);
        BaseTask playTask = new CameraPlayGB28181Task(context, camera.getDeviceId(), camera.getParentId(), beginTime, endTime)
        {
            @Override
            protected void onPostExecute(TaskResult result)
            {
                super.onPostExecute(result);
                taskStack.pop();
                if (result.getError() == null)
                {
                    Map<String, Object> map = (Map<String, Object>) result.getResultData();
                    String address = (String) map.get("address");
                    session = (String) map.get("session");
                    videoView.setVideoPath(address);
                    videoView.start();
                    //startQueryProgress();
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

    public void setController(AbstractControlView controller)
    {
        initController(controller);
    }

    public void setOnPlayStateListener(OnPlayStateListener onPlayStateListener)
    {
        this.onPlayStateListener = onPlayStateListener;
    }

    public void setOnPlayListener(OnPlayListener onPlayListener)
    {
        this.onPlayListener = onPlayListener;
    }

    public void setOnFailListener(OnFailListener onFailListener)
    {
        this.onFailListener = onFailListener;
    }

    public void setOnHistoryRecordListener(OnHistoryRecordListener onHistoryRecordListener)
    {
        this.onHistoryRecordListener = onHistoryRecordListener;
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

    public interface OnPlayStateListener
    {
        void onState(int state, String message);
    }

    public interface OnPlayListener
    {
        void onPlay(View view);
    }

    public interface OnFailListener
    {
        void onClick(View view);
    }

    public interface OnHistoryRecordListener
    {
        void onData(Map<String, List<RecordItem>> recordMap);
    }
}
