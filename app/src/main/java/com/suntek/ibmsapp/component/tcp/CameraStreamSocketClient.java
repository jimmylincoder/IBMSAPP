package com.suntek.ibmsapp.component.tcp;

import android.util.Log;

import com.suntek.ibmsapp.page.camera.CameraPlayHKActivity;
import com.suntek.ibmsapp.util.ByteArrayConveter;


import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 视频流接收Client
 *
 * @author jimmy
 */
public class CameraStreamSocketClient
{
    private static CameraStreamSocketClient cameraStreamSocketClient;

    //socket连接
    private Socket socket;

    //保活定时器
    private Timer keepLiveTimer;

    private final String TAG = CameraStreamSocketClient.class.getName();

    //是打印调试信息
    private boolean isDebug = true;

    //ping包间隔时间 秒
    private int liveIntervalTime = 20;

    //ping包
    private byte[] ping = new byte[]{5, 33, 0, 0, 0, 0};

    //流接收监听器
    private OnCameraStreamDataListener onCameraStreamDataListener;

    //异常监听器
    private OnCameraStreamExceptionListener onCameraStreamExceptionListener;

    //socket连接线程
    private Thread socketThread;

    //输入流
    private InputStream inputStream;

    //字节流头标识 字节长度
    private final int HEADER_LENGTH = 2;

    //数据长度标识 字节长度
    private final int DATA_LENGTH = 4;

    //数据流头标识 字节长度
    private final int DATA_HEADER_LEGNTH = 1;

    //接收的字节数组
    private byte[] receiveData;

    //是否停止接收流
    private boolean isStop = true;

    //mediaChannel字节标识
    private final byte[] MEDIA_CHANNEL_HEADER = new byte[]{5, 32};

    //数据流字节标识
    private final byte[] STREAM_HEADER = new byte[]{19, 20};

    //视频第一个字标类型标识
    //视频头
    private final int VIDEO_HEADER_TYPE = 1;

    //视频数据
    private final int VIDEO_TYPE = 2;

    private final int BUFFER_SIZE = 1024 * 2;
    private byte[] headerDataByte = new byte[HEADER_LENGTH];
    private byte[] dataLengthByte = new byte[DATA_LENGTH];
    private byte[] bufferByte = new byte[BUFFER_SIZE];

    private String recordFilePath = "/sdcard/";
    private FileOutputStream fileOutputStream;

    private CameraStreamSocketClient()
    {

    }


    public static CameraStreamSocketClient getInstance()
    {
        if (cameraStreamSocketClient == null)
            cameraStreamSocketClient = new CameraStreamSocketClient();
        return cameraStreamSocketClient;
    }

    /**
     * 打开连接
     *
     * @param ip
     * @param port
     * @return
     * @throws CameraStreamException
     */
    public CameraStreamSocketClient open(String ip, String port)
    {
        isStop = true;
        socketThread = new SocketThread(ip, port);
        socketThread.start();
        return this;
    }

    /**
     * 接收流数据
     */
    private void receiveData()
    {
        while (isStop)
        {
            //读取头
            headerDataByte = read(HEADER_LENGTH);
            if (headerDataByte == null)
                break;
            handleHeader(headerDataByte);
        }
    }

    /**
     * 根据头处理流数据
     *
     * @param header
     */
    private void handleHeader(byte[] header)
    {
        //获取数据长度
        dataLengthByte = read(DATA_LENGTH);
        int dataLength = 0;
        if (dataLengthByte != null)
            dataLength = ByteArrayConveter.getInt(dataLengthByte, 0);

        //获取通道头
        if (Arrays.equals(header, MEDIA_CHANNEL_HEADER))
        {
            receiveData = read(dataLength);
            //接收通道号
            int mediaChannel = -1;
            if (receiveData != null && receiveData.length > 0)
                mediaChannel = ByteArrayConveter.getInt(receiveData, 0);
            if (onCameraStreamDataListener != null && mediaChannel != -1)
                onCameraStreamDataListener.onReceiveMediaChannel(mediaChannel);
        }
        else if (Arrays.equals(header, STREAM_HEADER))
        {
            //读取第一字节类型
            receiveData = read(1);
            //处理流数据
            if (receiveData != null && receiveData.length > 0)
                handleBigData(receiveData[0], dataLength - 1);
        }
    }

    /**
     * 读取指定大小字节
     *
     * @return
     */
    private byte[] read()
    {
        try
        {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            if (isStop)
            {
                int readLength = 0;
                if (bufferByte != null)
                {
                    readLength = inputStream.read(bufferByte);
                    readLength = readLength == -1 ? 0 : readLength;
                    outputStream.write(bufferByte, 0, readLength);
                    if (BUFFER_SIZE - readLength != 0)
                    {
                        byte[] b = read(BUFFER_SIZE - readLength);
                        if (b != null && b.length == (BUFFER_SIZE - readLength))
                            outputStream.write(b);
                    }
                }
            }
            return outputStream.toByteArray();

        } catch (IOException e)
        {
            log("read()  " + e.getMessage());
            onCameraStreamExceptionListener.onReceiveDataException(e);
            return null;
        }
    }

    /**
     * 处理视频数据
     *
     * @param type
     */
    private void handData(int type, byte[] data, int totalSize, int remainLength)
    {
        if (onCameraStreamDataListener == null || data == null)
            return;
        switch (type)
        {
            case VIDEO_HEADER_TYPE:
                onCameraStreamDataListener.onReceiveMediaHeader(data, data.length, totalSize, remainLength);
                break;

            case VIDEO_TYPE:
                onCameraStreamDataListener.onReceiveVideoData(data, data.length, totalSize, remainLength);
                break;
        }
    }

    /**
     * 视频流较大时分块接收后通知
     *
     * @param type
     * @param length
     */
    private void handleBigData(int type, int length)
    {
        int count = length / BUFFER_SIZE;
        int more = length % BUFFER_SIZE;

        long st = new Date().getTime();
        for (int i = 0; i < count; i++)
        {
            bufferByte = read();
            handData(type, bufferByte, length, length - BUFFER_SIZE * i);
        }
        int reaminLength = length - BUFFER_SIZE * count;
        if (more != 0)
        {
            bufferByte = read(more);
            handData(type, bufferByte, length, reaminLength - more);
        }
        long et = new Date().getTime();
        log("处理包结束，长度为:" + length + " 花费时间:" + (et - st) + "ms");
    }


    /**
     * 阻塞式接收固定长度字节数组
     *
     * @param length
     * @return
     * @throws IOException
     */
    private byte[] read(int length)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int ret;
        try
        {
            while (isStop && (ret = inputStream.read()) != -1)
            {
                byte b = (byte) ret;
                outputStream.write(b);
                if (outputStream.toByteArray().length == length)
                {
                    break;
                }
            }
        } catch (IOException e)
        {
            log("read(int length)  " + e.getMessage());
            onCameraStreamExceptionListener.onReceiveDataException(e);
        }
        if (outputStream.toByteArray().length == length)
            return outputStream.toByteArray();
        else
            return null;
    }

    /**
     * 定时ping包
     */
    private void keepLive()
    {
        keepLiveTimer = new Timer();
        keepLiveTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    if (socket != null && !socket.isClosed())
                    {
                        OutputStream os = socket.getOutputStream();
                        os.write(ping);
                        os.flush();
                        Log.e(CameraPlayHKActivity.class.getName(), "发送保活心跳!!");
                    }
                    else
                    {
                        this.cancel();
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }, liveIntervalTime * 1000, liveIntervalTime * 1000);
    }

    /**
     * 设置是否打印调试信息
     *
     * @param isDebug
     * @return
     */
    public CameraStreamSocketClient isDebug(boolean isDebug)
    {
        this.isDebug = isDebug;
        return this;
    }

    /**
     * 设置ping包时间
     *
     * @param intervalTime
     * @return
     */
    public CameraStreamSocketClient setIntervalTime(int intervalTime)
    {
        this.liveIntervalTime = intervalTime;
        return this;
    }

    /**
     * 设置ping包内容
     *
     * @param ping
     * @return
     */
    public CameraStreamSocketClient setPing(byte[] ping)
    {
        this.ping = ping;
        return cameraStreamSocketClient;
    }

    /**
     * 设置数据监听器
     *
     * @param onCameraStreamDataListener
     * @return
     */
    public CameraStreamSocketClient setOnCameraStreamDataListener(OnCameraStreamDataListener onCameraStreamDataListener)
    {
        this.onCameraStreamDataListener = onCameraStreamDataListener;
        return this;
    }

    /**
     * 异常监听器
     *
     * @param onCameraStreamExceptionListener
     * @return
     */
    public CameraStreamSocketClient setOnCameraStreamExceptionListener(OnCameraStreamExceptionListener onCameraStreamExceptionListener)
    {
        this.onCameraStreamExceptionListener = onCameraStreamExceptionListener;
        return this;
    }

    /**
     * 打印日志
     *
     * @param message
     */
    private void log(String message)
    {
        if (isDebug)
            Log.i(TAG, message);
    }

    class SocketThread extends Thread
    {
        private String ip;

        private String port;

        public SocketThread(String ip, String port)
        {
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run()
        {
            super.run();
            try
            {
                socket = new Socket(ip, Integer.parseInt(port));
                if (socket.isConnected())
                {
                    inputStream = socket.getInputStream();
                    keepLive();
                    receiveData();
                }
                else
                    onCameraStreamExceptionListener.onConnectException(new Exception("连接失败"));
            } catch (IOException e)
            {
                log("run()  " + e.getMessage());
                onCameraStreamExceptionListener.onConnectException(e);
            }
        }
    }

    public void close()
    {
        try
        {
            isStop = false;
            if (inputStream != null)
                inputStream.close();
            if (socket != null)
            {
                socket.close();
                socket.shutdownInput();
            }
            if (socketThread != null)
                socketThread.interrupt();
            if (keepLiveTimer != null)
                keepLiveTimer.cancel();

            socket = null;
            socketThread = null;
            keepLiveTimer = null;
        } catch (IOException e)
        {
            onCameraStreamExceptionListener.onConnectException(e);
        }
    }
}
