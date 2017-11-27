package com.suntek.ibmsapp.widget.hkivisionview;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 负责播放请求控制http请求
 *
 * @author jimmy
 */
public interface IControlAction
{
    void playReal(int streamType);

    void playHistory(String beginTime, String endTime, int streamType);

    void pause();

    void stop();

    void resume();

    Bitmap takePic();

    void startRecord(String path) throws FileNotFoundException;

    File stopRecord();

    int getState();

    void setController(AbstractControlView controller);

    void getRecordByDate(String date, AbstractHkivisionVideoView.OnHistoryRecordListener onHistoryRecordListener);

    void seekTo(long position);
}
