package com.suntek.ibmsapp.widget.hkivisionview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import org.MediaPlayer.PlayM4.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 海康播放view
 *
 * @author jimmy
 */
public class HikvisionVideoView extends AbstractHkivisionVideoView implements IControlAction
{
    public HikvisionVideoView(@NonNull Context context)
    {
        super(context);
    }

    public HikvisionVideoView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public HikvisionVideoView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void playReal(int streamType)
    {
        if (socketIp == null && socketPort == null)
            getSocketIp(null, null, streamType);
        else
            initSocket(null,null,streamType);
    }

    @Override
    public void playHistory(String beginTime, String endTime, int streamType)
    {
        if (socketIp == null && socketPort == null)
            getSocketIp(beginTime, endTime, streamType);
        else
            initSocket(beginTime,endTime,streamType);
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void stop()
    {
        release();
    }

    @Override
    public void resume()
    {

    }

    @Override
    public Bitmap takePic()
    {
        byte[] bitmapByte = new byte[bitmapWidth * bitmapHeight * 3 / 2];
        Player.MPInteger mpInteger = new Player.MPInteger();
        if (player != null)
        {
            player.getJPEG(port, bitmapByte, bitmapLen, mpInteger);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapLen);
            return bitmap;
        }
        return null;
    }

    @Override
    public void startRecord(String path)
    {
        if (getState() == PLAYING)
        {
            recordFile = new File(path);
            try
            {
                if (!recordFile.exists())
                    recordFile.createNewFile();
                recordOutStream = new FileOutputStream(path);
                if (player != null)
                    player.setPreRecordFlag(port, true);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public File stopRecord()
    {
        if (player != null)
        {
            player.setPreRecordFlag(port, false);
            return recordFile;
        }
        return null;
    }

    @Override
    public int getState()
    {
        return playerState;
    }

    @Override
    public void setController(AbstractControlView controller)
    {
        initController(controller);
    }

    public void setOnPlayListener(OnPlayListener onPlayListener)
    {
        this.onPlayListener = onPlayListener;
    }

    public void initSurfaceView()
    {
        videoView.setStart_Top(-1);
        videoView.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                videoView.setFatherW_H(llVideoView.getRight(), llVideoView.getBottom());
                videoView.setFatherTopAndBottom(llVideoView.getRight(), llVideoView.getBottom());
            }
        }, 100);
    }

}
