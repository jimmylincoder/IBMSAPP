package com.suntek.ibmsapp.page.camera;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.page.photo.PhotoListActivity;
import com.suntek.ibmsapp.util.BitmapUtil;
import com.suntek.ibmsapp.util.FileUtil;
import com.suntek.ibmsapp.util.NiceUtil;
import com.suntek.ibmsapp.util.PermissionRequest;
import com.suntek.ibmsapp.util.PreviewUtil;
import com.suntek.ibmsapp.util.SizeUtil;
import com.suntek.ibmsapp.widget.StreamTypePopView;
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

import static com.suntek.ibmsapp.page.camera.BaseCameraPlayActivity.STREAM_CLEAN;
import static com.suntek.ibmsapp.page.camera.BaseCameraPlayActivity.STREAM_FAST;
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
    @BindView(R.id.tv_stream_type)
    TextView tvStreamType;

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
        camera = (Camera) getIntent().getExtras().get("camera");
        hikvisionVideoView.initInfo(camera);
        hikvisionVideoView.setController(netSpeedView);
        hikvisionVideoView.setOnPlayListener(new AbstractHkivisionVideoView.OnPlayListener()
        {
            @Override
            public void onPlay(View view)
            {
                hikvisionVideoView.playReal(STREAM_HIGH_QUALITY);
            }
        });
        initClick();
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
                ToastHelper.getInstance(CameraPlayerActivity.this).shortShowMessage("声音");
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
        Intent intent = new Intent(CameraPlayerActivity.this, PhotoListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_btn_record)
    public void record(View view)
    {
        record();
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

    @OnClick(R.id.ll_stream_type)
    public void streamType(View view)
    {
        setStreamType(view);
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

    private void setStreamType(View view)
    {
        StreamTypePopView streamTypePopView = StreamTypePopView.getInstance(context);
        streamTypePopView.showUp(view);
        streamTypePopView.setOnItemClickListener(new StreamTypePopView.OnItemClickListener()
        {
            @Override
            public void OnHighQuality(View view)
            {
                //hikvisionVideoView.release();
                hikvisionVideoView.play(STREAM_HIGH_QUALITY + "");
                tvStreamType.setText("高清");
            }

            @Override
            public void OnFluent(View view)
            {
                //hikvisionVideoView.release();
                hikvisionVideoView.play(STREAM_FLUENT + "");
                tvStreamType.setText("流畅");
            }
        });
    }

}

