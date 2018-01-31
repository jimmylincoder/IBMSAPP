package com.suntek.ibmsapp.page.camera;

import android.content.Context;
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
import android.widget.PopupWindow;
import android.widget.TextView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.NetWorkStateReceiver;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.component.cache.ACache;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.page.photo.PhotoListActivity;
import com.suntek.ibmsapp.task.camera.CameraAddHistoryTask;
import com.suntek.ibmsapp.util.BitmapUtil;
import com.suntek.ibmsapp.util.FileUtil;
import com.suntek.ibmsapp.util.NiceUtil;
import com.suntek.ibmsapp.util.PermissionRequest;
import com.suntek.ibmsapp.util.PreviewUtil;
import com.suntek.ibmsapp.util.SizeUtil;
import com.suntek.ibmsapp.widget.StreamTypePopView;
import com.suntek.ibmsapp.widget.TalkView;
import com.suntek.ibmsapp.widget.ToastHelper;
import com.suntek.ibmsapp.widget.hkivisionview.AbstractControlView;
import com.suntek.ibmsapp.widget.hkivisionview.AbstractHkivisionVideoView;
import com.suntek.ibmsapp.widget.hkivisionview.FullOperController;
import com.suntek.ibmsapp.widget.hkivisionview.HikvisionVideoView;
import com.suntek.ibmsapp.widget.hkivisionview.NetSpeedController;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

import static com.suntek.ibmsapp.R.id.iv_play_state;
import static com.suntek.ibmsapp.page.camera.BaseCameraPlayActivity.STREAM_CLEAN;
import static com.suntek.ibmsapp.page.camera.BaseCameraPlayActivity.STREAM_FAST;
import static com.suntek.ibmsapp.widget.hkivisionview.AbstractHkivisionVideoView.FAIL;
import static com.suntek.ibmsapp.widget.hkivisionview.AbstractHkivisionVideoView.PAUSE;
import static com.suntek.ibmsapp.widget.hkivisionview.AbstractHkivisionVideoView.PLAYING;
import static com.suntek.ibmsapp.widget.hkivisionview.AbstractHkivisionVideoView.STOP;
import static com.suntek.ibmsapp.widget.hkivisionview.AbstractHkivisionVideoView.STREAM_FLUENT;
import static com.suntek.ibmsapp.widget.hkivisionview.AbstractHkivisionVideoView.STREAM_HIGH_QUALITY;

public class CameraPlayerActivity extends BaseActivity
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
    HikvisionVideoView hikvisionVideoView;
    @BindView(R.id.ll_head)
    LinearLayout llHead;
    @BindView(R.id.ll_oper)
    LinearLayout llOper;
    @BindView(R.id.fl_video)
    FrameLayout flVideo;
    @BindView(R.id.ll_take_pic)
    LinearLayout llTakePic;
    @BindView(R.id.iv_take_pic)
    ImageView ivTakePic;
    @BindView(R.id.ll_record)
    LinearLayout llRecord;
    @BindView(R.id.tv_record_time)
    TextView tvRecordTime;
    @BindView(R.id.iv_pot)
    ImageView ivPot;
    @BindView(R.id.iv_stream_type)
    ImageView ivStreamType;
    @BindView(R.id.tv_camera_name)
    TextView tvCameraName;
    @BindView(R.id.iv_play_state)
    ImageView ivPlayState;
    @BindView(R.id.iv_sounds)
    ImageView ivSounds;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.ll_talk)
    LinearLayout llTalk;
    @BindView(R.id.ll_oper_and_record)
    LinearLayout llOperAndRecord;
    @BindView(R.id.tav_talk)
    TalkView tavTalk;
    @BindView(R.id.iv_oper_record)
    ImageView ivRecord;
    StreamTypePopView streamTypePopView;

    private ACache aCache;

    //清晰度选择
    protected PopupWindow streamMenu;
    private int streamType = STREAM_FLUENT;

    //记录录像开始时间
    private long recordBeginTime;
    private Timer recordTimer;
    private Camera camera;
    private boolean isRecord = false;

    private NetSpeedController netSpeedView;
    private FullOperController fullOperView;

    private boolean isSounds = true;

    private NetWorkStateReceiver netWorkStateReceiver;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_player;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        netSpeedView = new NetSpeedController(this);
        fullOperView = new FullOperController(this);
        aCache = ACache.get(this);
        camera = (Camera) getIntent().getExtras().get("camera");
        tvCameraName.setText(camera.getName());
        hikvisionVideoView.initInfo(camera);
        hikvisionVideoView.setController(netSpeedView);
        hikvisionVideoView.setOnPlayListener(new AbstractHkivisionVideoView.OnPlayListener()
        {
            @Override
            public void onPlay(View view)
            {
                hikvisionVideoView.playReal(streamType);
            }
        });
        initClick();
        hikvisionVideoView.playReal(streamType);
        streamTypePopView = StreamTypePopView.getInstance(context);
        streamTypePopView.initSelected();
        //添加历史记录
        loadData();
    }

    private void initClick()
    {
        fullOperView.setOnItemClickListener(new FullOperController.OnItemClickListener()
        {
            @Override
            public void back(View view)
            {
                exitOper();
            }

            @Override
            public void stop(View view)
            {
                if (hikvisionVideoView.getState() == PLAYING)
                    hikvisionVideoView.stop();
                else
                    hikvisionVideoView.playReal(streamType);
            }

            @Override
            public void sounds(View view)
            {
                if (isSounds)
                {
                    ivSounds.setBackground(getDrawable(R.drawable.btn_oper_voice_off));
                    fullOperView.setIvSoundsView(getDrawable(R.drawable.btn_full_voice_off));
                }
                else
                {
                    ivSounds.setBackground(getDrawable(R.drawable.btn_oper_voice_on));
                    fullOperView.setIvSoundsView(getDrawable(R.drawable.btn_full_voice_on));
                }
                isSounds = !isSounds;
            }

            @Override
            public void takePic(View view)
            {
                CameraPlayerActivity.this.takePic();
            }

            @Override
            public void record(View view)
            {
                CameraPlayerActivity.this.record();
            }

            @Override
            public void streamType(View view)
            {
                setStreamType(view);
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
                        fullOperView.setIvStopView(getDrawable(R.drawable.btn_full_pause));
                        break;
                    case STOP:
                    case PAUSE:
                    case FAIL:
                        if (ivPlayState != null)
                            ivPlayState.setBackground(getDrawable(R.drawable.btn_oper_play));
                        fullOperView.setIvStopView(getDrawable(R.drawable.btn_full_play));
                        break;
                }
            }
        });
        hikvisionVideoView.setOnFailListener(new AbstractHkivisionVideoView.OnFailListener()
        {
            @Override
            public void onClick(View view)
            {
                hikvisionVideoView.playReal(streamType);
            }
        });
        tavTalk.setOnHangUpListener(new TalkView.OnHangUpListener()
        {
            @Override
            public void onHangUp(View view)
            {
                llTalk.setVisibility(View.GONE);
                llOperAndRecord.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void initToolBar()
    {

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        hikvisionVideoView.release();
    }

    @OnClick(R.id.iv_mutil_screen)
    public void mutilScreen(View view)
    {
        ToastHelper.getInstance(CameraPlayerActivity.this).shortShowMessage("多屏查看功能正在完善中...");
    }

    @OnClick(R.id.ll_play)
    public void play(View view)
    {
        if (hikvisionVideoView.getState() != PLAYING)
            hikvisionVideoView.playReal(AbstractHkivisionVideoView.STREAM_FLUENT);
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
        if (hikvisionVideoView.getState() != PLAYING)
            return;
        PermissionRequest.verifyStoragePermissions(this);
        Bitmap bitmap = hikvisionVideoView.takePic();
        Bitmap preview = BitmapUtil.centerSquareScaleBitmap(bitmap, 100);
        File localFile = new File(PIC_PATH);
        localFile.mkdirs();
        ivTakePic.setImageBitmap(preview);
        showPreview();
        if (bitmap != null)
            FileUtil.saveImageToGallery(this, bitmap);
        ToastHelper.getInstance(this).shortShowMessage("截图成功");
    }

    @OnClick(R.id.iv_take_pic)
    public void jumpToPhotoList(View view)
    {
        Intent intent = new Intent(CameraPlayerActivity.this, PhotoListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_btn_record)
    public void record(View view)
    {
        record();
    }

    @OnClick(R.id.ll_history)
    public void jumpToHistory(View view)
    {
        Intent intent = new Intent(CameraPlayerActivity.this, CameraPlayerHistoryActivity.class);
        intent.putExtra("camera", camera);
        startActivity(intent);
    }

    private void record()
    {
        if (hikvisionVideoView.getState() != PLAYING)
            return;
        if (isRecord)
        {
            long recordTime = new Date().getTime() - recordBeginTime;
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


    @OnClick(R.id.iv_back)
    public void back(View view)
    {
        exitOper();
    }

    @OnClick(R.id.ll_fullscreen)
    public void fullScreen(View view)
    {
        if (playerState == PLAYER_NOT_FULL)
        {
            hikvisionVideoView.setController(fullOperView);
            NiceUtil.hideActionBar(this);
            NiceUtil.scanForActivity(this).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setFullScreenLayout(View.GONE);
            playerState = PLAYER_FULLSCREEN;
        }
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
            //判断是否在录像中
            if (isRecord)
                record();
            //保存预览图
            savePreview();
            finish();
        }
    }

    private void savePreview()
    {
        Bitmap bitmap = hikvisionVideoView.takePic();
        if (bitmap != null)
            PreviewUtil.getInstance().savePreview(bitmap, camera.getId(), camera.getDeviceId());
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

    @OnClick(R.id.iv_sounds)
    public void sounds(View view)
    {
        if (isSounds)
        {
            view.setBackground(getDrawable(R.drawable.btn_oper_voice_off));
            fullOperView.setIvSoundsView(getDrawable(R.drawable.btn_full_voice_off));
        }
        else
        {
            view.setBackground(getDrawable(R.drawable.btn_oper_voice_on));
            fullOperView.setIvSoundsView(getDrawable(R.drawable.btn_full_voice_on));
        }
        isSounds = !isSounds;
    }

    @OnClick(R.id.ll_btn_talk)
    public void talk(View view)
    {
        llOperAndRecord.setVisibility(View.GONE);
        llTalk.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.ll_stream_type)
    public void streamType(View view)
    {
        setStreamType(view);
    }

    private void showRecordTag(boolean isShow)
    {
        if (isShow)
        {
            ivRecord.setBackground(getDrawable(R.mipmap.ic_oper_record_processing));
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
                                if (ivPot != null)
                                {
                                    if (ivPot.getVisibility() == View.VISIBLE)
                                        ivPot.setVisibility(View.INVISIBLE);
                                    else
                                        ivPot.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                }
            }, 0, 1000);
        }
        else
        {
            ivRecord.setBackground(getDrawable(R.drawable.btn_oper_record));
            llRecord.setVisibility(View.GONE);
            recordTimer.cancel();
            recordTimer = null;
        }
    }

    private void setStreamType(View view)
    {
        streamTypePopView.showUp(view);
        streamTypePopView.setOnItemClickListener(new StreamTypePopView.OnItemClickListener()
        {
            @Override
            public void OnHighQuality(View view)
            {
                ToastHelper.getInstance(CameraPlayerActivity.this).shortShowMessage("建议在6M以上带宽下播放高清模式");
                hikvisionVideoView.changeStreamType(STREAM_HIGH_QUALITY);
                ivStreamType.setBackground(getDrawable(R.drawable.btn_oper_high_quality));
                fullOperView.setIvStreamView(getDrawable(R.drawable.btn_full_high_quality));
            }

            @Override
            public void OnFluent(View view)
            {
                hikvisionVideoView.changeStreamType(STREAM_FLUENT);
                ivStreamType.setBackground(getDrawable(R.drawable.btn_oper_fluent));
                fullOperView.setIvStreamView(getDrawable(R.drawable.btn_full_fluent));
            }
        });
    }

    @Override
    public void loadData()
    {
        String cameraId = camera.getId();
        String userCode = aCache.getAsString("user");
        String cameraName = camera.getName();
        if (cameraName != null)
            tvCameraName.setText(cameraName);

        new CameraAddHistoryTask(this, userCode, cameraId)
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
                    ToastHelper.getInstance(CameraPlayerActivity.this).shortShowMessage(result.getError().getMessage());
                }
            }
        }.execute();
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
                        ToastHelper.getInstance(CameraPlayerActivity.this).longShowMessage("当前手机未连接网络");
                        hikvisionVideoView.release();
                    }
                    if(state == MOBILE_STATE)
                        ToastHelper.getInstance(CameraPlayerActivity.this).shortShowMessage("请注意:当前手机正在处于手机网络状态");
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

