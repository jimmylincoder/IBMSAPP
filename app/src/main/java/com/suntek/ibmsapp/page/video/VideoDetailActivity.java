package com.suntek.ibmsapp.page.video;

import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.widget.UnityDialog;
import com.tv.danmaku.ijk.media.widget.media.IjkVideoView;

import org.MediaPlayer.PlayM4.Player;


import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 视频播放界面
 *
 * @author jimmy
 */
public class VideoDetailActivity extends BaseActivity
{
    @BindView(R.id.sv_video)
    IjkVideoView videoView;

    private final String TAG = VideoDetailActivity.class.getName();

    private Player player;

    private String filePath;

    private int port;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_video_detail;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        filePath = getIntent().getStringExtra("photoPath");
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        videoView.setRender(IjkVideoView.RENDER_TEXTURE_VIEW);
        videoView.setVideoPath(filePath);
        videoView.start();
    }

    @Override
    public void initToolBar()
    {

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (videoView != null)
        {
            videoView.stopPlayback();
            videoView.release(true);
        }
    }

    @OnClick(R.id.ll_back)
    public void back(View view)
    {
        finish();
    }

    @OnClick(R.id.iv_del)
    public void delete(View view)
    {
        new UnityDialog(this)
                .setTitle("是否确认删除")
                .setHint("是否确认删除该视频")
                .setCancel("取消", new UnityDialog.OnCancelDialogListener()
                {
                    @Override
                    public void cancel(UnityDialog unityDialog)
                    {
                        unityDialog.dismiss();
                    }
                })
                .setConfirm("确定", new UnityDialog.OnConfirmDialogListener()
                {
                    @Override
                    public void confirm(UnityDialog unityDialog, String content)
                    {
                        File file = new File(filePath);
                        if (file.exists())
                        {
                            file.delete();
                        }
                        unityDialog.dismiss();
                        finish();
                    }
                }).show();
    }
}
