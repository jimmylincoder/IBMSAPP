package com.suntek.ibmsapp.util;

import android.graphics.Bitmap;
import android.icu.util.TimeUnit;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * 视频缩略图
 *
 * @author jimmy
 */
public class ThumbnailUtil
{
    private static ThumbnailUtil thumbnailUtil;

    private FFmpegMediaMetadataRetriever mediaMetadataRetriever;

    private ThreadPoolExecutor threadPoolExecutor;

    private Map<String, Bitmap> thumbnailCache;

    private OnGetThumbnail onGetThumbnail;

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            String filePath = msg.getData().getString("filePath");
            Bitmap bitmap = thumbnailCache.get(filePath);
            onGetThumbnail.onThumbnail(bitmap);
        }
    };

    private ThumbnailUtil()
    {
        mediaMetadataRetriever = new FFmpegMediaMetadataRetriever();
        threadPoolExecutor = new ThreadPoolExecutor(5, 10, 1, java.util.concurrent.TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(4));
        thumbnailCache = new HashMap<>();
    }

    public static ThumbnailUtil getInstance()
    {
        if (thumbnailUtil == null)
            thumbnailUtil = new ThumbnailUtil();
        return thumbnailUtil;
    }

    public Bitmap getBitmap(String fileName)
    {
        mediaMetadataRetriever.setDataSource(fileName);
        Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime();
        return bitmap;
    }

    public void display(String filePath, OnGetThumbnail onGetThumbnail)
    {
        this.onGetThumbnail = onGetThumbnail;
        threadPoolExecutor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                Bitmap bitmap1 = thumbnailCache.get(filePath);
                if (bitmap1 == null)
                {
                    mediaMetadataRetriever.setDataSource(filePath);
                    Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime();
                    thumbnailCache.put(filePath, bitmap);
                }
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("filePath", filePath);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });
    }


    public static interface OnGetThumbnail
    {
        void onThumbnail(Bitmap bitmap);
    }
}
