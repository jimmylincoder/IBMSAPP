package com.suntek.ibmsapp.page.camera;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.model.RecordItem;
import com.suntek.ibmsapp.page.photo.PhotoListActivity;
import com.suntek.ibmsapp.util.BitmapUtil;
import com.suntek.ibmsapp.util.DateUtil;
import com.suntek.ibmsapp.util.FileUtil;
import com.suntek.ibmsapp.util.NiceUtil;
import com.suntek.ibmsapp.util.PermissionRequest;
import com.suntek.ibmsapp.util.PreviewUtil;
import com.suntek.ibmsapp.util.RecordHander;
import com.suntek.ibmsapp.util.SizeUtil;
import com.suntek.ibmsapp.widget.SelectDateView;
import com.suntek.ibmsapp.widget.TimeSeekBarView;
import com.suntek.ibmsapp.widget.ToastHelper;
import com.suntek.ibmsapp.widget.hkivisionview.AbstractHkivisionVideoView;
import com.suntek.ibmsapp.widget.hkivisionview.FullRecordOperController;
import com.suntek.ibmsapp.widget.hkivisionview.HikvisionVideoView;
import com.suntek.ibmsapp.widget.hkivisionview.NetSpeedController;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

import static com.suntek.ibmsapp.widget.hkivisionview.AbstractHkivisionVideoView.FAIL;
import static com.suntek.ibmsapp.widget.hkivisionview.AbstractHkivisionVideoView.PAUSE;
import static com.suntek.ibmsapp.widget.hkivisionview.AbstractHkivisionVideoView.PLAYING;
import static com.suntek.ibmsapp.widget.hkivisionview.AbstractHkivisionVideoView.STOP;
import static com.suntek.ibmsapp.widget.hkivisionview.AbstractHkivisionVideoView.STREAM_HIGH_QUALITY;

/**
 * 播放历史界面
 *
 * @author jimmy
 */
public class CameraPlayerHistoryActivity extends BaseActivity
{
    //全屏
    protected final int PLAYER_FULLSCREEN = 0;
    //非全屏
    protected final int PLAYER_NOT_FULL = 1;
    //全屏状态
    protected int playerState = PLAYER_NOT_FULL;
    //截图保存位置
    protected final String PIC_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/ibms";
    protected String recordFilePath = "/sdcard/DCIM/Camera/ibms/";

    private Camera camera;

    @BindView(R.id.hvv_video)
    HikvisionVideoView hikvisionVideoView;
    @BindView(R.id.sdv_date)
    SelectDateView selectDateView;
    @BindView(R.id.ta_time)
    TimeSeekBarView seekBarView;
    @BindView(R.id.ll_time)
    LinearLayout llSeekBar;
    @BindView(R.id.ll_time_loading)
    LinearLayout llTimeLoading;
    @BindView(R.id.ll_no_record)
    LinearLayout llNoRecord;
    @BindView(R.id.fl_video)
    FrameLayout flVideo;
    @BindView(R.id.ll_head)
    LinearLayout llHead;
    @BindView(R.id.ll_oper)
    LinearLayout llOper;
    @BindView(R.id.iv_take_pic)
    ImageView ivTakePic;
    @BindView(R.id.ll_take_pic)
    LinearLayout llTakePic;
    @BindView(R.id.ll_record)
    LinearLayout llRecord;
    @BindView(R.id.tv_record_time)
    TextView tvRecordTime;
    @BindView(R.id.iv_pot)
    ImageView ivPot;
    @BindView(R.id.iv_play_state)
    ImageView ivPlayState;
    @BindView(R.id.iv_sounds)
    ImageView ivSounds;

    private NetSpeedController netSpeedView;
    private FullRecordOperController fullRecordOperController;

    private Map<String, List<RecordItem>> recordMap;

    private String chooseDate;
    private Date nowDate;

    //记录录像开始时间
    private long recordBeginTime;
    private Timer recordTimer;
    private boolean isRecord = false;

    private boolean isSounds = true;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_camera_player_history;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        netSpeedView = new NetSpeedController(this);
        fullRecordOperController = new FullRecordOperController(this);
        camera = (Camera) getIntent().getExtras().get("camera");
        chooseDate = DateUtil.convertYYYY_MM_DD(new Date());
        hikvisionVideoView.initInfo(camera);
        hikvisionVideoView.setController(netSpeedView);
        initListener();
    }

    private void initListener()
    {
        hikvisionVideoView.setOnPlayListener(new AbstractHkivisionVideoView.OnPlayListener()
        {
            @Override
            public void onPlay(View view)
            {
                List<RecordItem> recordItems = null;
                if (recordMap != null)
                    recordItems = recordMap.get(chooseDate);
                if (recordItems == null || recordItems.isEmpty())
                {
                    ToastHelper.getInstance(CameraPlayerHistoryActivity.this).shortShowMessage("该摄像机无录像");
                }
                else
                {
                    RecordItem recordItem = RecordHander.getEarliestTime(recordItems);
                    String beginTime = DateUtil.convertYYYY_MM_DD_HH_MM_SS(new Date(recordItem.getStartTime()));
                    String endTime = chooseDate + " 23:59:59";
                    hikvisionVideoView.playHistory(beginTime, endTime, STREAM_HIGH_QUALITY);
                }
            }
        });
        hikvisionVideoView.getRecordByDate(chooseDate, new AbstractHkivisionVideoView.OnHistoryRecordListener()
        {
            @Override
            public void onData(Map<String, List<RecordItem>> map)
            {
                recordMap = map;
                changeTimeBarState();
            }
        });
        selectDateView.setOnDateListener(new SelectDateView.OnDateListener()
        {
            @Override
            public void onChange(String date)
            {
                chooseDate = date;
                getRecordData();
            }
        });
        seekBarView.setOnValueChangeListener(new TimeSeekBarView.OnValueChangeListener()
        {
            @Override
            public void onValueChange(Date date)
            {

            }

            @Override
            public void onStartValueChange(Date date)
            {

            }

            @Override
            public void onStopValueChange(Date date)
            {
                nowDate = date;
                fullRecordOperController.setValue(date.getTime());
                long position = (date.getTime() -
                        RecordHander.getEarliestTime(recordMap.get(chooseDate)).getStartTime()) / 1000;
                hikvisionVideoView.seekTo(position);
            }
        });
        fullRecordOperController.setOnItemClickListener(new FullRecordOperController.OnItemClickListener()
        {
            @Override
            public void back(View view)
            {
                exitOper();
            }

            @Override
            public void stop(View view)
            {
                hikvisionVideoView.release();
            }

            @Override
            public void sounds(View view)
            {
                if (isSounds)
                {
                    ivSounds.setBackground(getDrawable(R.drawable.btn_oper_voice_off));
                    fullRecordOperController.setIvSoundsView(getDrawable(R.drawable.btn_full_voice_off));
                }
                else
                {
                    ivSounds.setBackground(getDrawable(R.drawable.btn_oper_voice_on));
                    fullRecordOperController.setIvSoundsView(getDrawable(R.drawable.btn_full_voice_on));
                }
                isSounds = !isSounds;
            }

            @Override
            public void takePic(View view)
            {
                CameraPlayerHistoryActivity.this.takePic();
            }

            @Override
            public void record(View view)
            {
                CameraPlayerHistoryActivity.this.record();
            }
        });

        fullRecordOperController.setOnDateChangeListener(new FullRecordOperController.OnDateChangeListener()
        {
            @Override
            public void onValueChange(Date date)
            {

            }

            @Override
            public void onStartValueChange(Date date)
            {

            }

            @Override
            public void onStopValueChange(Date date)
            {
                nowDate = date;
                seekBarView.setValue(date.getTime());
                long position = (date.getTime() -
                        RecordHander.getEarliestTime(recordMap.get(chooseDate)).getStartTime()) / 1000;
                hikvisionVideoView.seekTo(position);
            }
        });
        hikvisionVideoView.setOnPlayStateListener(new AbstractHkivisionVideoView.OnPlayStateListener()
        {
            @Override
            public void onState(int state, String message)
            {
                switch (state)
                {
                    case PLAYING:
                        if (ivPlayState != null)
                            ivPlayState.setBackground(getDrawable(R.drawable.btn_oper_pause));
                        fullRecordOperController.setIvStopView(getDrawable(R.drawable.btn_full_pause));
                        break;
                    case STOP:
                    case PAUSE:
                    case FAIL:
                        if (ivPlayState != null)
                            ivPlayState.setBackground(getDrawable(R.drawable.btn_oper_play));
                        fullRecordOperController.setIvStopView(getDrawable(R.drawable.btn_full_play));
                        break;
                }
            }
        });
        selectDateView.setOnDateClickListener(new SelectDateView.OnDateClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(CameraPlayerHistoryActivity.this, CameraDateActivity.class);
                intent.putExtra("camera", camera);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1)
        {
            chooseDate = data.getStringExtra("choose_date");
            getRecordData();
        }
    }

    private void getRecordData()
    {
        hikvisionVideoView.release();
        llNoRecord.setVisibility(View.GONE);
        llSeekBar.setVisibility(View.GONE);
        llTimeLoading.setVisibility(View.VISIBLE);
        hikvisionVideoView.getRecordByDate(chooseDate, new AbstractHkivisionVideoView.OnHistoryRecordListener()
        {
            @Override
            public void onData(Map<String, List<RecordItem>> map)
            {
                recordMap = map;
                changeTimeBarState();
            }
        });
    }

    @OnClick(R.id.ll_play)
    public void play(View view)
    {
        String beginTime = DateUtil.convertYYYY_MM_DD_HH_MM_SS(
                new Date(RecordHander.getEarliestTime(recordMap.get(chooseDate)).getStartTime()));
        String endTime = chooseDate + " 23:59:59";
        if (hikvisionVideoView.getState() != PLAYING)
            hikvisionVideoView.playHistory(beginTime, endTime, STREAM_HIGH_QUALITY);
        else
            hikvisionVideoView.release();

    }


    @OnClick(R.id.ll_snapshot)
    public void takePic(View view)
    {
        takePic();
    }

    private void takePic()
    {
        PermissionRequest.verifyStoragePermissions(this);
        Bitmap bitmap = hikvisionVideoView.takePic();
        Bitmap preview = BitmapUtil.centerSquareScaleBitmap(bitmap, 100);
        File localFile = new File(PIC_PATH);
        localFile.mkdirs();
        ivTakePic.setImageBitmap(preview);
        showPreview();
        FileUtil.saveImageToGallery(this, bitmap);
        ToastHelper.getInstance(this).shortShowMessage("截图成功");
    }

    @OnClick(R.id.iv_take_pic)
    public void jumpToPhotoList(View view)
    {
        Intent intent = new Intent(CameraPlayerHistoryActivity.this, PhotoListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.iv_back)
    public void back(View view)
    {
        exitOper();
    }

    @OnClick(R.id.ll_fullscreen)
    public void fullScreen(View view)
    {
        hikvisionVideoView.setController(fullRecordOperController);
        if (playerState == PLAYER_NOT_FULL)
        {
            NiceUtil.hideActionBar(this);
            NiceUtil.scanForActivity(this).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setFullScreenLayout(View.GONE);
            playerState = PLAYER_FULLSCREEN;
        }
    }

    private void exitOper()
    {
        hikvisionVideoView.initSurfaceView();
        hikvisionVideoView.setController(netSpeedView);
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

    @OnClick(R.id.ll_btn_record)
    public void record(View view)
    {
        record();
    }

    @OnClick(R.id.iv_sounds)
    public void sounds(View view)
    {
        if (isSounds)
        {
            ivSounds.setBackground(getDrawable(R.drawable.btn_oper_voice_off));
            fullRecordOperController.setIvSoundsView(getDrawable(R.drawable.btn_full_voice_off));
        }
        else
        {
            ivSounds.setBackground(getDrawable(R.drawable.btn_oper_voice_on));
            fullRecordOperController.setIvSoundsView(getDrawable(R.drawable.btn_full_voice_on));
        }
        isSounds = !isSounds;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == event.KEYCODE_BACK)
        {
            exitOper();
        }
        return true;
    }

    private void setFullScreenLayout(int viewType)
    {
        hikvisionVideoView.initSurfaceView();
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
    }

    /**
     * 改变时间轴状态
     */
    private void changeTimeBarState()
    {
        List<RecordItem> recordItems = recordMap.get(chooseDate);
        if (recordItems == null || recordItems.isEmpty())
        {
            llSeekBar.setVisibility(View.GONE);
            llTimeLoading.setVisibility(View.GONE);
            llNoRecord.setVisibility(View.VISIBLE);
        }
        else
        {
            List<RecordItem> changeRecordItems = new ArrayList<>();
            for (RecordItem recordItem : recordItems)
            {
                RecordItem recordItem1 = new RecordItem();
                recordItem1.setStartTime(recordItem.getStartTime() / 1000);
                recordItem1.setEndTime(recordItem.getEndTime() / 1000);
                changeRecordItems.add(recordItem1);
            }
            long nowTime = RecordHander.getEarliestTime(recordItems).getStartTime();
            seekBarView.setRecordList(changeRecordItems);
            seekBarView.setValue(nowTime);
            fullRecordOperController.setRecordList(changeRecordItems);
            fullRecordOperController.setValue(nowTime);
            llTimeLoading.setVisibility(View.GONE);
            llNoRecord.setVisibility(View.GONE);
            llSeekBar.setVisibility(View.VISIBLE);
        }
    }

    private void showPreview()
    {
        llTakePic.setVisibility(View.VISIBLE);
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

    private void record()
    {
        if (isRecord)
        {
            File recordFile = hikvisionVideoView.stopRecord();
            if (recordFile != null)
            {
                showRecordTag(false);
                Bitmap bitmap = hikvisionVideoView.takePic();
                isRecord = false;
                PreviewUtil.getInstance().saveVideoPreview(bitmap, recordFile.getName() + "_thumbnail");
                ivTakePic.setImageBitmap(bitmap);
                showPreview();
            }
        }
        else
        {
            showRecordTag(true);
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat format1 = new SimpleDateFormat("HHmmss");
            String fileName = "VID_" + format.format(new Date()) + "_" + format1.format(new Date()) + ".mp4";
            hikvisionVideoView.startRecord(recordFilePath + fileName);
            isRecord = true;
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        hikvisionVideoView.release();
    }

    @Override
    public void initToolBar()
    {

    }

    private void showRecordTag(boolean isShow)
    {
        if (isShow)
        {
            if (recordTimer == null)
                recordTimer = new Timer();
            llRecord.setVisibility(View.VISIBLE);
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
        }
        else
        {
            llRecord.setVisibility(View.GONE);
            recordTimer.cancel();
        }
    }
}
