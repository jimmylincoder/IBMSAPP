package com.suntek.ibmsapp.page.video;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseFragment;
import com.tv.danmaku.ijk.media.widget.media.IjkVideoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.OnClick;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 视频滑动页
 *
 * @author jimmy
 */
public class VideoPagerFragment extends BaseFragment
{
    private String path;

    @BindView(R.id.iv_preview)
    ImageView ivPreview;

    @BindView(R.id.ijk_video)
    IjkVideoView ijkVideoView;

    @BindView(R.id.fl_preview)
    FrameLayout flPreview;

    @BindView(R.id.ll_video)
    LinearLayout llVideo;

    public static VideoPagerFragment newInstance(String path)
    {
        VideoPagerFragment videoPagerFragment = new VideoPagerFragment();

        Bundle args = new Bundle();
        args.putString("path", path);
        videoPagerFragment.setArguments(args);

        return videoPagerFragment;
    }

    @Override
    public int getLayoutId()
    {
        return R.layout.fragment_video_pager;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        path = getArguments() != null ? getArguments().getString("path") : null;
    }


    @Override
    public void initViews(Bundle savedInstanceState)
    {
        File file = new File(path);
        String thumbnail = file.getParentFile().getAbsolutePath() + "/thumbnail/" + file.getName() + "_thumbnail.jpg";
        Bitmap bitmap = getBitmapByPath(thumbnail);
        ivPreview.setImageBitmap(bitmap);
    }

    @OnClick(R.id.ll_play)
    public void play(View view)
    {
        MediaController mediaController = new MediaController(getActivity());
        flPreview.setVisibility(View.GONE);
        llVideo.setVisibility(View.VISIBLE);
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        ijkVideoView.setRender(IjkVideoView.RENDER_TEXTURE_VIEW);
        ijkVideoView.setVideoPath(path);
        ijkVideoView.start();

        ijkVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer)
            {
                flPreview.setVisibility(View.VISIBLE);
                llVideo.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (ijkVideoView != null)
        {
            ijkVideoView.stopPlayback();
            ijkVideoView.release(true);
        }
    }

    private Bitmap getBitmapByPath(String path)
    {
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(path);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        // ImageLoader.getInstance().displayImage("file://" + photoPath, givPhoto);
        return bitmap;
    }
}
