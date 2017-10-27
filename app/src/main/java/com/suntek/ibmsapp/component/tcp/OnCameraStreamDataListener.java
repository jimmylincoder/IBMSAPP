package com.suntek.ibmsapp.component.tcp;

/**
 * 视频流监听器
 *
 * @author jimmy
 */
public interface OnCameraStreamDataListener
{
    void onReceiveMediaChannel(int mediaChannel);

    void onReceiveMediaHeader(byte[] header, int length, int totalSize, int remainLength);

    void onReceiveVideoData(byte[] videoData, int length, int totalSize, int remainLength);

    void onReceiveVoiceData(byte[] voiceData, int length, int totalSize, int remainLength);
}
