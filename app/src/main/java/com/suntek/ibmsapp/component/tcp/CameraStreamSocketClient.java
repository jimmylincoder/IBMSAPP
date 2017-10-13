package com.suntek.ibmsapp.component.tcp;

import android.util.Log;

import com.suntek.ibmsapp.page.camera.CameraPlayHKActivity;
import com.suntek.ibmsapp.util.ByteArrayConveter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
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
    private int liveIntervalTime = 50;

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
    private boolean isStop;

    //mediaChannel字节标识
    private final byte[] MEDIA_CHANNEL_HEADER = new byte[]{5, 32};

    //数据流字节标识
    private final byte[] STREAM_HEADER = new byte[]{19, 20};

    //视频第一个字标类型标识
    //视频头
    private final int VIDEO_HEADER_TYPE = 1;

    //视频数据
    private final int VIDEO_TYPE = 2;

    private CameraStreamSocketClient()
    {

    }

    public CameraStreamSocketClient getInstance()
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
            receiveData = read(HEADER_LENGTH);
            handleHeader(receiveData);
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
        receiveData = read(DATA_LENGTH);
        int dataLength = ByteArrayConveter.getInt(receiveData, 0);
        //获取通道头
        if (header == MEDIA_CHANNEL_HEADER)
        {
            //接收通道号
            receiveData = read(dataLength);
            int mediaChannel = ByteArrayConveter.getInt(receiveData, 0);
            onCameraStreamDataListener.onReceiveMediaChannel(mediaChannel);
        }
        else if (header == STREAM_HEADER)
        {
            //接收数据
            receiveData = read(dataLength);
            //处理流数据
            handData(receiveData);
        }
    }

    /**
     * 处理视频数据
     *
     * @param data
     */
    private void handData(byte[] data)
    {
        receiveData = Arrays.copyOfRange(data, 1, data.length);
        switch (data[0])
        {
            case VIDEO_HEADER_TYPE:
                onCameraStreamDataListener.onReceiveMediaHeader(receiveData, receiveData.length);
                break;

            case VIDEO_TYPE:
                onCameraStreamDataListener.onReceiveVideoData(receiveData, receiveData.length);
                break;
        }
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
            while ((ret = inputStream.read()) != -1)
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
            onCameraStreamExceptionListener.onReceiveDataException(e);
        }
        return outputStream.toByteArray();
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
                    OutputStream os = socket.getOutputStream();
                    os.write(ping);
                    os.flush();
                    Log.e(CameraPlayHKActivity.class.getName(), "发送保活心跳!!");
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
                    receiveData();
                    keepLive();
                }
                else
                    onCameraStreamExceptionListener.onConnectException(new Exception("连接失败"));
            } catch (IOException e)
            {
                onCameraStreamExceptionListener.onConnectException(e);
            }
        }
    }

    public void close()
    {
        try
        {
            socket.close();
            isStop = false;
            inputStream.close();
            socketThread.interrupt();
            keepLiveTimer.cancel();
        } catch (IOException e)
        {
            onCameraStreamExceptionListener.onConnectException(e);
        }
    }
}
