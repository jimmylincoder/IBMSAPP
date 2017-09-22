package com.suntek.ibmsapp.page.camera;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.dsw.calendar.component.MonthView;
import com.dsw.calendar.entity.CalendarInfo;
import com.dsw.calendar.views.GridCalendarView;
import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.model.RecordItem;
import com.suntek.ibmsapp.task.base.BaseTask;
import com.suntek.ibmsapp.task.camera.CameraAddHistoryTask;
import com.suntek.ibmsapp.task.camera.control.CameraChangePositionTask;
import com.suntek.ibmsapp.task.camera.control.CameraPauseTask;
import com.suntek.ibmsapp.task.camera.control.CameraPlayTask;
import com.suntek.ibmsapp.task.camera.control.CameraQueryProgressTask;
import com.suntek.ibmsapp.task.camera.control.CameraQueryRecordTask;
import com.suntek.ibmsapp.task.camera.control.CameraResumeTask;
import com.suntek.ibmsapp.task.camera.control.CameraStopTask;
import com.suntek.ibmsapp.util.FileUtil;
import com.suntek.ibmsapp.util.NiceUtil;
import com.suntek.ibmsapp.util.SizeUtil;
import com.suntek.ibmsapp.widget.HorizontalPicker;
import com.suntek.ibmsapp.widget.TimeSeekBarView;
import com.suntek.ibmsapp.widget.ToastHelper;
import com.tv.danmaku.ijk.media.widget.media.IjkVideoView;
import com.tv.danmaku.ijk.media.widget.media.OnVideoTouchListener;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
public class CameraPlayActivity extends BaseActivity implements Runnable
{
    //全屏
    private final int PLAYER_FULLSCREEN = 0;
    //非全屏
    private final int PLAYER_NOT_FULL = 1;
    //截图保存位置
    private final String PIC_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/ibms";
    //视频view
    @BindView(R.id.video_view)
    IjkVideoView ivvVideo;
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
    @BindView(R.id.hp_date_picker)
    HorizontalPicker hpDatePicker;
    @BindView(R.id.ll_loading)
    LinearLayout llLoading;
    @BindView(R.id.ll_fail)
    LinearLayout llFail;
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
    private CameraPlayTask cameraPlayTask;
    //存储录像对应时间段
    private Map<String, Object> recordMap = new HashMap<>();
    //是否为录像状态
    private boolean isRecorder = false;
    //日历选择
    GridCalendarView gcv;
    //记录录像开始时间
    private long recordBeginTime;
    private long changePosition;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_camera_play;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        camera = (Camera) getIntent().getExtras().getSerializable("camera");

        initVideoView();
        //getCameraAddress("2017-08-31 00:00:00", "2017-08-31 23:59:59");
        getCameraAddress(null, null);
        initTimeView();
        loadData();
        initTimeSeekBarView();
        initRecord();

        List<HorizontalPicker.PickerItem> textItems = new ArrayList<>();
        for (int i = 1; i <= 25; i++)
        {
            textItems.add(new HorizontalPicker.TextItem("S" + i));
        }
        hpDatePicker.setItems(textItems, 1);

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
                    ToastHelper.getInstance(CameraPlayActivity.this).shortShowMessage(result.getError().getMessage());
                }
            }
        }.execute();
    }

    private void getCameraAddress(String startTime, String endTime)
    {
        if (cameraPlayTask != null)
            cameraPlayTask.cancel(true);
        cameraPlayTask = new CameraPlayTask(this, camera.getDeviceId(), camera.getParentId(), camera.getIp(), camera.getChannel(), camera.getUserName(),
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
                            ivvVideo.setVideoPath(address);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                            ToastHelper.getInstance(CameraPlayActivity.this).shortShowMessage(e.getMessage());
                        }
                        ivvVideo.start();

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
        cameraPlayTask.execute();
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
            }

            @Override
            public void onStartValueChange(Date date)
            {

            }

            @Override
            public void onStopValueChange(Date date)
            {
                //TODO 移动到最终位置后，如果该位置没有录像的话，就移向前面有录像的一段，如果时间大于当前时间的话，则播放实时视频
                //如果大于当前时间则反回当前时间
                if (date.getTime() > new Date().getTime())
                {
                    timeSeekBar.setValue(new Date().getTime());
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
                        ToastHelper.getInstance(CameraPlayActivity.this).shortShowMessage("该时间点没有录像");
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
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            //String beginTime = format.format(date);
                            //String endTime = format1.format(new Date().getTime() - 1000 * 60 * 10);
                            String beginTime = chooseDate + " 00:00:00";
                            String endTime = chooseDate + " 23:59:59";
                            reloadVideoView(beginTime, endTime);

                            changePosition = (date.getTime() - dayBegin.getTime())/1000;
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
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        chooseDate = format1.format(new Date());
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
        timeHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if (tvNowTime != null)
                    tvNowTime.setText((String) msg.obj);
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
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        ivvVideo.setRender(IjkVideoView.RENDER_TEXTURE_VIEW);
        ivvVideo.setOnVideoTouchListener(new OnVideoTouchListener()
        {
            @Override
            public void onSingleTouchEvent(MotionEvent ev)
            {
                if (llOper.getVisibility() == View.GONE)
                {
                    llOper.setVisibility(View.VISIBLE);
                    llTime.setVisibility(View.GONE);
                }
                else
                {
                    llOper.setVisibility(View.GONE);
                    llTime.setVisibility(View.VISIBLE);
                }


                if (playerState == PLAYER_FULLSCREEN)
                {
                    if (llHead.getVisibility() == View.GONE)
                        llHead.setVisibility(View.VISIBLE);
                    else
                        llHead.setVisibility(View.GONE);
                }
            }

            @Override
            public void onDoubleTouchEvent(MotionEvent ev)
            {
                ivvVideo.toggleAspectRatio();
            }
        });

        llFail.setVisibility(View.GONE);
        llLoading.setVisibility(View.VISIBLE);
        ivvVideo.setOnInfoListener(new IMediaPlayer.OnInfoListener()
        {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1)
            {
                if (i == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START)
                {
                    llLoading.setVisibility(View.GONE);
                    if (seekBarTimer == null && !isRecorder)
                        startSeekBarTimer();
                    if(isRecorder)
                        changePosition(changePosition);
                }
                return false;
            }
        });
        ivvVideo.setOnErrorListener(new IMediaPlayer.OnErrorListener()
        {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1)
            {
                llLoading.setVisibility(View.GONE);
                llFail.setVisibility(View.VISIBLE);
                return false;
            }
        });

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

        if (mBackPressed || !ivvVideo.isBackgroundPlayEnabled())
        {
            ivvVideo.stopPlayback();
            ivvVideo.release(true);
            ivvVideo.stopBackgroundPlay();
        }
        else
        {
            ivvVideo.enterBackground();
        }
        IjkMediaPlayer.native_profileEnd();

        isBackGround = true;
    }

    @Override
    protected void onDestroy()
    {
        isTimeRun = false;
        if (seekBarTimer != null)
            seekBarTimer.cancel();
        if (queryProgressTimer != null)
            queryProgressTimer.cancel();
        stopPlay();
        //  menu.dismiss();
        cameraPlayTask.cancel(true);
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
        File localFile = new File(PIC_PATH);
        localFile.mkdirs();
        try
        {
            Bitmap bitmap = ivvVideo.takePicture();
            FileUtil.saveImageToGallery(this, bitmap);
            ToastHelper.getInstance(this).shortShowMessage("截图成功");
        } catch (Exception e)
        {
            e.printStackTrace();
        }

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
                    Intent intent = new Intent(CameraPlayActivity.this, CameraDownloadActivity.class);
                    startActivity(intent);
                }
            });

            llInfo.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    menu.dismiss();
                    Intent intent = new Intent(CameraPlayActivity.this, CameraInfoActivity.class);
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
        if (isBackGround)
        {
            initVideoView();
            isBackGround = false;
        }
    }

    @Override
    public void run()
    {
        try
        {
            while (isTimeRun)
            {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss");
                String str = sdf.format(new Date());
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
        ivvVideo.stopPlayback();
        ivvVideo.release(true);
        ivvVideo.stopBackgroundPlay();
        IjkMediaPlayer.native_profileEnd();
        if (!cameraPlayTask.isCancelled())
            cameraPlayTask.cancel(true);
        stopPlay();
        initVideoView();
        if (llFail.getVisibility() == View.VISIBLE)
            llFail.setVisibility(View.GONE);
        llLoading.setVisibility(View.VISIBLE);
        getCameraAddress(startTime, endTime);
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
                    ToastHelper.getInstance(CameraPlayActivity.this).shortShowMessage("暂停成功");
                }
                else
                {
                    ToastHelper.getInstance(CameraPlayActivity.this).shortShowMessage(result.getError().getMessage());
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
                    ToastHelper.getInstance(CameraPlayActivity.this).shortShowMessage("恢复播放成功");
                }
                else
                {
                    ToastHelper.getInstance(CameraPlayActivity.this).shortShowMessage(result.getError().getMessage());
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
                            ToastHelper.getInstance(CameraPlayActivity.this).shortShowMessage("该时间无录像");
                        }
                        else
                        {
                            isRecorder = true;
                            initTimeSeekRecord(chooseDate);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            RecordItem recordItem = getEarliestTime(recordItems);
                            cancelSeekBarTimer();
                            timeSeekBar.setValue(recordItem.getStartTime());
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
                camera.getUserName(), camera.getPassword(), beginTime, endTime)
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
                    ToastHelper.getInstance(CameraPlayActivity.this).shortShowMessage(result.getError().getMessage());
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
}
