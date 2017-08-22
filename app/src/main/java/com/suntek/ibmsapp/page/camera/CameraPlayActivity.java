package com.suntek.ibmsapp.page.camera;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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


import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.task.camera.CameraAddHistoryTask;
import com.suntek.ibmsapp.task.camera.control.CameraPlayTask;
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

    PopupWindow menu;
    //当前时间handler
    private Handler timeHandler;
    private boolean mBackPressed;
    //是否停止当前时间线程
    private boolean isTimeRun = true;
    //全屏状态
    private int playerState = PLAYER_NOT_FULL;
    //定时器
    private Timer timer;

    private String session;

    private boolean isBackGround = false;

    private List<Map<String, Object>> recordList;

    private Camera camera;

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
        initTimeView();
        loadData();
        initTimeSeekBarView();

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
        new CameraPlayTask(this, camera.getDeviceId(), camera.getParentId(), camera.getIp(), camera.getChannel(), camera.getUserName(),
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
                    ivvVideo.setVideoPath(address);
                    ivvVideo.start();
                }
                else
                {
                    llLoading.setVisibility(View.GONE);
                    llFail.setVisibility(View.VISIBLE);
                    ToastHelper.getInstance(CameraPlayActivity.this).shortShowMessage("播放失败，请检查后台摄像头配置");
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
                    tvPlayType.setText("回放");
                }
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String beginTime = format.format(date);
                String endTime = "2017-08-22 23:00:00";
            }
        });
        try
        {
            recordList = new ArrayList<>();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendarObj = Calendar.getInstance();
            timeSeekBar.setValue(new Date().getTime());
            Map<String, Object> time1 = new HashMap<>();
            calendarObj.setTime(format.parse("2017-08-03 13:00:00"));
            time1.put("beginTime", calendarObj.getTimeInMillis() / 1000);
            calendarObj.setTime(format.parse("2017-08-03 13:20:00"));
            time1.put("endTime", calendarObj.getTimeInMillis() / 1000);

            Map<String, Object> time2 = new HashMap<>();
            calendarObj.setTime(format.parse("2017-08-03 13:30:00"));
            time2.put("beginTime", calendarObj.getTimeInMillis() / 1000);
            calendarObj.setTime(format.parse("2017-08-03 13:40:00"));
            time2.put("endTime", calendarObj.getTimeInMillis() / 1000);

            recordList.add(time1);
            recordList.add(time2);
            timeSeekBar.setRecordList(recordList);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        timer = new Timer();
        timer.schedule(new TimerTask()
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
     * 判断是否已在录像内
     *
     * @return
     */
    private boolean isInRecord(Date date)
    {
        boolean isContain = false;
        for (Map<String, Object> map : recordList)
        {
            long beginTime = (long) map.get("beginTime");
            long endTime = (long) map.get("endTime");
            long nowTime = date.getTime() / 1000;
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
        List<Map<String, Object>> lastNowTime = new ArrayList<>();
        long nowTime = date.getTime() / 1000;
        long theLastedTime = 0;
        long theLast = 0;
        for (Map<String, Object> map : recordList)
        {
            long endTime = (long) map.get("endTime");
            if (nowTime > endTime)
            {
                lastNowTime.add(map);
            }
        }

        for (Map<String, Object> map : lastNowTime)
        {
            long endTime = (long) map.get("endTime");
            long beginTime = (long) map.get("beginTime");
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

        return new Date(theLastedTime * 1000);
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
        //getCameraAddress("2017-08-22 01:00:00","2017-08-22 13:00:00");
        getCameraAddress(null, null);
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
                }
                return false;
            }
        });
        ivvVideo.setOnErrorListener(new IMediaPlayer.OnErrorListener()
        {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1)
            {
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
        timer.cancel();
        stopPlay();
        super.onDestroy();
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
        }
        else
        {
            finish();
        }
        menu.dismiss();
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
            LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.view_pop_menu, null);
            menu = new PopupWindow(linearLayout, ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            //menu.setAnimationStyle(R.style.Popupwindow);
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
            menu.dismiss();
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
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) SizeUtil.getRawSize(this, TypedValue.COMPLEX_UNIT_DIP, 300));
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

                }
            }
        }.execute();
    }
}
