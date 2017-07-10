package com.suntek.ibmsapp.page.test;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.widget.media.MediaController;
import com.suntek.ibmsapp.widget.media.VideoPlayerView;

import butterknife.BindView;
import tv.danmaku.ijk.media.player.IMediaPlayer;


/**
 * Created by jimmy on 17/7/6.
 */
public class TestActivity extends BaseActivity
{
    @BindView(R.id.vpv_video)
    VideoPlayerView mPlayerView;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_test;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        initMediaPlayer();
    }

    @SuppressLint("UseSparseArrays")
    private void initMediaPlayer() {
        //配置播放器
        MediaController mMediaController = new MediaController(this);
        mMediaController.setTitle("title");
        mPlayerView.setMediaController(mMediaController);
        mPlayerView.requestFocus();
        mPlayerView.requestFocus();
        mPlayerView.setOnInfoListener(onInfoListener);
        mPlayerView.setOnSeekCompleteListener(onSeekCompleteListener);
        mPlayerView.setOnCompletionListener(onCompletionListener);
        mPlayerView.setOnControllerEventsListener(onControllerEventsListener);
        mPlayerView.setVideoPath("rtmp://live.hkstv.hk.lxdns.com/live/hks");
        mPlayerView.start();
    }

    /**
     * 视频缓冲事件回调
     */
    private IMediaPlayer.OnInfoListener onInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {

            return true;
        }
    };

    /**
     * 视频跳转事件回调
     */
    private IMediaPlayer.OnSeekCompleteListener onSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {

        @Override
        public void onSeekComplete(IMediaPlayer mp) {

        }
    };

    /**
     * 视频播放完成事件回调
     */
    private IMediaPlayer.OnCompletionListener onCompletionListener = new IMediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(IMediaPlayer mp) {

            mPlayerView.pause();
        }
    };

    /**
     * 控制条控制状态事件回调
     */
    private VideoPlayerView.OnControllerEventsListener onControllerEventsListener = new VideoPlayerView.OnControllerEventsListener() {

        @Override
        public void onVideoPause() {

        }


        @Override
        public void OnVideoResume() {

        }
    };

    @Override
    public void initToolBar()
    {

    }
}
