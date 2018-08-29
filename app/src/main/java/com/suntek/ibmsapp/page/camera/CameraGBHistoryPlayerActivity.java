package com.suntek.ibmsapp.page.camera;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.NetWorkStateReceiver;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.model.RecordItem;
import com.suntek.ibmsapp.page.photo.PhotoListActivity;
import com.suntek.ibmsapp.util.BitmapUtil;
import com.suntek.ibmsapp.util.DateUtil;
import com.suntek.ibmsapp.util.FileUtil;
import com.suntek.ibmsapp.util.NiceUtil;
import com.suntek.ibmsapp.util.PermissionRequest;
import com.suntek.ibmsapp.util.RecordHander;
import com.suntek.ibmsapp.util.SizeUtil;
import com.suntek.ibmsapp.widget.DateChoosePopView;
import com.suntek.ibmsapp.widget.SelectDateView;
import com.suntek.ibmsapp.widget.TimeSeekBarView;
import com.suntek.ibmsapp.widget.ToastHelper;
import com.suntek.ibmsapp.widget.gb_palyer_viewer.GBVideoView;
import com.suntek.ibmsapp.widget.hkivisionview.FullRecordOperController;
import com.suntek.ibmsapp.widget.hkivisionview.NetSpeedController;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.suntek.ibmsapp.widget.hkivisionview.AbstractHkivisionVideoView.FAIL;
import static com.suntek.ibmsapp.widget.hkivisionview.AbstractHkivisionVideoView.PAUSE;
import static com.suntek.ibmsapp.widget.hkivisionview.AbstractHkivisionVideoView.PLAYING;
import static com.suntek.ibmsapp.widget.hkivisionview.AbstractHkivisionVideoView.STOP;

/**
 * 国标历史播放界面
 *
 * @author
 */
public class CameraGBHistoryPlayerActivity extends BaseActivity
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

    @BindView(R.id.hvv_video)
    GBVideoView videoView;
    @BindView(R.id.sdv_date)
    SelectDateView selectDateView;
    @BindView(R.id.ta_time)
    TimeSeekBarView seekBarView;
    @BindView(R.id.tv_change_time)
    TextView tvChangeTime;
    @BindView(R.id.iv_play_state)
    ImageView ivPlayState;
    @BindView(R.id.iv_sounds)
    ImageView ivSounds;
    @BindView(R.id.ll_no_record)
    LinearLayout llNoRecord;
    @BindView(R.id.ll_time)
    LinearLayout llSeekBar;
    @BindView(R.id.ll_time_loading)
    LinearLayout llTimeLoading;
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


    private String chooseDate;
    private Date nowDate;
    private Camera camera;
    private DateChoosePopView dateChoosePopView;
    private Map<String, List<RecordItem>> recordMap;

    private NetSpeedController netSpeedView;
    private FullRecordOperController fullRecordOperController;

    private boolean isSounds = true;

    private NetWorkStateReceiver netWorkStateReceiver;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_camera_history_player_gb;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        netSpeedView = new NetSpeedController(this);
        fullRecordOperController = new FullRecordOperController(this);
        camera = (Camera) getIntent().getExtras().get("camera");

        chooseDate = DateUtil.convertYYYY_MM_DD(new Date());
        videoView.initInfo(camera);
        videoView.setController(netSpeedView);
        dateChoosePopView = DateChoosePopView.getInstance(context, camera);
        initListener();
    }

    private void initListener()
    {
        videoView.setOnPlayListener(new GBVideoView.OnPlayListener()
        {
            @Override
            public void onPlay(View view)
            {
                List<RecordItem> recordItems = null;
                if (recordMap != null)
                    recordItems = recordMap.get(chooseDate);
                if (recordItems == null || recordItems.isEmpty())
                {
                    ToastHelper.getInstance(CameraGBHistoryPlayerActivity.this).shortShowMessage("该摄像机无录像");
                }
                else
                {
                    RecordItem recordItem = RecordHander.getEarliestTime(recordItems);
                    String beginTime = DateUtil.convertYYYY_MM_DD_HH_MM_SS(new Date(recordItem.getStartTime()));
                    String endTime = chooseDate + " 23:59:59";
                    videoView.playHistory(beginTime, endTime);
                }
            }
        });
        videoView.getRecordByDate(chooseDate, new GBVideoView.OnHistoryRecordListener()
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
                tvChangeTime.setText(DateUtil.convertHH_MM_SS(date));
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
                videoView.seekTo(position);
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
                if (videoView.getState() == PLAYING)
                    videoView.release();
                else if (videoView.getState() == STOP)
                    playNowDate();
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
                CameraGBHistoryPlayerActivity.this.takePic();
            }

            @Override
            public void record(View view)
            {

            }
        });

        fullRecordOperController.setOnDateChangeListener(new FullRecordOperController.OnDateChangeListener()
        {
            @Override
            public void onValueChange(Date date)
            {
                tvChangeTime.setText(DateUtil.convertHH_MM_SS(date));
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
                videoView.seekTo(position);
            }
        });
        videoView.setOnPlayStateListener(new GBVideoView.OnPlayStateListener()
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
                dateChoosePopView.showCenter(view);
            }
        });
        dateChoosePopView.setOnDateSelectedListener(new DateChoosePopView.OnDateSelectedListener()
        {
            @Override
            public void onDateSelected(String date)
            {
                chooseDate = date;
                getRecordData();
            }
        });
        videoView.setOnFailListener(new GBVideoView.OnFailListener()
        {
            @Override
            public void onClick(View view)
            {
                playNowDate();
            }
        });
    }

    @OnClick(R.id.ll_snapshot)
    public void takePic(View view)
    {
        takePic();
    }

    private void takePic()
    {
        if (videoView.getState() != PLAYING)
            return;
        PermissionRequest.verifyStoragePermissions(this);
        Bitmap bitmap = videoView.takePic();
        Bitmap preview = BitmapUtil.centerSquareScaleBitmap(bitmap, 100);
        File localFile = new File(PIC_PATH);
        localFile.mkdirs();
        ivTakePic.setImageBitmap(preview);
        showPreview();
        if (bitmap != null)
            FileUtil.saveImageToGallery(this, bitmap);
        ToastHelper.getInstance(this).shortShowMessage("截图成功");
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

    @OnClick(R.id.iv_take_pic)
    public void jumpToPhotoList(View view)
    {
        Intent intent = new Intent(CameraGBHistoryPlayerActivity.this, PhotoListActivity.class);
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
        videoView.setController(fullRecordOperController);
        if (playerState == PLAYER_NOT_FULL)
        {
            NiceUtil.hideActionBar(this);
            NiceUtil.scanForActivity(this).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setFullScreenLayout(View.GONE);
            playerState = PLAYER_FULLSCREEN;
        }
    }

    @OnClick(R.id.ll_btn_record)
    public void record(View view)
    {

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


    private void exitOper()
    {
        videoView.setController(netSpeedView);
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
    }

    private void getRecordData()
    {
        videoView.release();
        llNoRecord.setVisibility(View.GONE);
        llSeekBar.setVisibility(View.GONE);
        llTimeLoading.setVisibility(View.VISIBLE);
        videoView.getRecordByDate(chooseDate, new GBVideoView.OnHistoryRecordListener()
        {
            @Override
            public void onData(Map<String, List<RecordItem>> map)
            {
                recordMap = map;
                changeTimeBarState();
            }
        });
    }

    /**
     * 改变时间轴状态
     */
    private void changeTimeBarState()
    {
        List<RecordItem> recordItems = recordMap.get(chooseDate);
        if (recordItems == null || recordItems.isEmpty())
        {
            if (llSeekBar != null)
                llSeekBar.setVisibility(View.GONE);
            if (llTimeLoading != null)
                llTimeLoading.setVisibility(View.GONE);
            if (llNoRecord != null)
                llNoRecord.setVisibility(View.VISIBLE);
        }
        else
        {
            selectDateView.setNowTimeText(chooseDate);
            List<RecordItem> changeRecordItems = new ArrayList<>();
            for (RecordItem recordItem : recordItems)
            {
                RecordItem recordItem1 = new RecordItem();
                recordItem1.setStartTime(recordItem.getStartTime() / 1000);
                recordItem1.setEndTime(recordItem.getEndTime() / 1000);
                changeRecordItems.add(recordItem1);
            }
            long nowTime = RecordHander.getEarliestTime(recordItems).getStartTime();
            tvChangeTime.setText(DateUtil.convertHH_MM_SS(new Date(nowTime)));
            seekBarView.setRecordList(changeRecordItems);
            seekBarView.setValue(nowTime);
            fullRecordOperController.setRecordList(changeRecordItems);
            fullRecordOperController.setValue(nowTime);
            llTimeLoading.setVisibility(View.GONE);
            llNoRecord.setVisibility(View.GONE);
            llSeekBar.setVisibility(View.VISIBLE);
        }
    }

    private void playNowDate()
    {
        List<RecordItem> recordItems = null;
        if (recordMap != null)
            recordItems = recordMap.get(chooseDate);
        RecordItem recordItem = RecordHander.getEarliestTime(recordItems);
        String beginTime = DateUtil.convertYYYY_MM_DD_HH_MM_SS(new Date(recordItem.getStartTime()));
        String endTime = chooseDate + " 23:59:59";
        videoView.playHistory(beginTime, endTime);
    }

    @Override
    public void initToolBar()
    {

    }

    //在onResume()方法注册
    @Override
    protected void onResume()
    {
        if (netWorkStateReceiver == null)
        {
            netWorkStateReceiver = new NetWorkStateReceiver();
            netWorkStateReceiver.setOnNetworkStateLisener(new NetWorkStateReceiver.OnNetworkStateLisener()
            {
                @Override
                public void onState(int state)
                {
                    if(state == NO_NETWORK_STATE)
                    {
                        ToastHelper.getInstance(CameraGBHistoryPlayerActivity.this).longShowMessage("当前手机未连接网络");
                        videoView.release();
                    }
                    if(state == MOBILE_STATE)
                        ToastHelper.getInstance(CameraGBHistoryPlayerActivity.this).shortShowMessage("请注意:当前手机正在处于手机网络状态");
                }
            });
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);
        super.onResume();
    }

    //onPause()方法注销
    @Override
    protected void onPause()
    {
        unregisterReceiver(netWorkStateReceiver);
        super.onPause();
    }
}
