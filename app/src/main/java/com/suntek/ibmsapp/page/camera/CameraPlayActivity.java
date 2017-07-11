package com.suntek.ibmsapp.page.camera;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.util.FileUtil;
import com.suntek.ibmsapp.util.NiceUtil;
import com.suntek.ibmsapp.util.SizeUtil;
import com.suntek.ibmsapp.widget.ToastHelper;
import com.tv.danmaku.ijk.media.widget.media.IRenderView;
import com.tv.danmaku.ijk.media.widget.media.IjkVideoView;
import com.tv.danmaku.ijk.media.widget.media.OnVideoTouchListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 摄像头播放
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

    @BindView(R.id.video_view)
    IjkVideoView ivvVideo;
    @BindView(R.id.iv_snapshot)
    ImageView ivSnapshot;
    @BindView(R.id.ll_head)
    LinearLayout llHead;
    @BindView(R.id.ll_oper)
    LinearLayout llOper;
    @BindView(R.id.ll_message)
    LinearLayout llMessage;
    @BindView(R.id.fl_video)
    FrameLayout flVideo;
    @BindView(R.id.tv_nowtime)
    TextView tvNowTime;
    private Handler timeHandler;

    private boolean mBackPressed;
    private int playerState = PLAYER_NOT_FULL;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_camera_play;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        initVideoView();
        initTimeView();
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
                if(tvNowTime!=null)
                    tvNowTime.setText((String) msg.obj);
            }
        };
        //时间线程
     //   new Thread(this).start();
    }

    /**
     * 初始化视频控件
     */
    private void initVideoView()
    {
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        ivvVideo.setRender(IjkVideoView.RENDER_TEXTURE_VIEW);
        ivvVideo.setVideoPath("rtmp://live.hkstv.hk.lxdns.com/live/hks");
        ivvVideo.start();

        ivvVideo.setOnVideoTouchListener(new OnVideoTouchListener()
        {
            @Override
            public void onTouchEvent(MotionEvent ev)
            {
                ivvVideo.toggleAspectRatio();
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
    }

    @OnClick(R.id.iv_back)
    public void back(View view)
    {
        finish();
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
    public void run()
    {
        try
        {
            while (true)
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
}
