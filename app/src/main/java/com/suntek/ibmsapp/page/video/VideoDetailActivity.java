package com.suntek.ibmsapp.page.video;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.model.Photo;
import com.suntek.ibmsapp.widget.UnityDialog;
import com.suntek.ibmsapp.widget.ijkmedia.media.IjkVideoView;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 视频播放界面
 *
 * @author jimmy
 */
public class VideoDetailActivity extends BaseActivity
{
//    @BindView(R.id.sv_video)
//    IjkVideoView videoView;

    @BindView(R.id.vp_video)
    ViewPager vpVideo;

    private final String TAG = VideoDetailActivity.class.getName();

    private String filePath;

    private List<String> pathList;

    @Override
    public int getLayoutId()
    {
        return R.layout.activity_video_detail;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        pathList = new ArrayList<>();
        filePath = getIntent().getStringExtra("photoPath");
        List<Photo> paths = (List<Photo>) getIntent().getSerializableExtra("paths");
        for (Photo photo : paths)
        {
            pathList.addAll(photo.getPhotoPaths());
        }
        vpVideo.setAdapter(new VideoPagerAdapter(getSupportFragmentManager()));
        vpVideo.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                filePath = pathList.get(position);
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
        int index = pathList.indexOf(filePath);
        vpVideo.setCurrentItem(index);
//        IjkMediaPlayer.loadLibrariesOnce(null);
//        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
//        videoView.setRender(IjkVideoView.RENDER_TEXTURE_VIEW);
//        videoView.setVideoPath(filePath);
//        videoView.start();
    }

    @Override
    public void initToolBar()
    {

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
//        if (videoView != null)
//        {
//            videoView.stopPlayback();
//            videoView.release(true);
//        }
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

    private class VideoPagerAdapter extends FragmentStatePagerAdapter
    {
        public VideoPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            return VideoPagerFragment.newInstance(pathList.get(position));
        }

        @Override
        public int getCount()
        {
            return pathList.size();
        }
    }
}
