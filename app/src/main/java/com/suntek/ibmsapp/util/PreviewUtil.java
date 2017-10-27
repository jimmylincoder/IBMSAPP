package com.suntek.ibmsapp.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


/**
 * 预图工具类
 *
 * @author jimmy
 */
public class PreviewUtil
{
    private static PreviewUtil previewUtil = null;

    private static final String previewPath = "/sdcard/DCIM/Camera/ibms/thumbnail/";

    private PreviewUtil()
    {

    }

    public static PreviewUtil getInstance()
    {
        if (previewUtil == null)
            previewUtil = new PreviewUtil();
        return previewUtil;
    }

    /**
     * 保存预览图
     *
     * @param bitmap
     * @param cameraId
     * @param deviceId
     */
    public void savePreview(Bitmap bitmap, String cameraId, String deviceId)
    {
        FileUtil.saveImage(previewPath, cameraId + "_" + deviceId, bitmap);
    }

    public Bitmap getVideoPreview(String fileName)
    {
        String filePath = previewPath + fileName + ".jpg";
        return BitmapFactory.decodeFile(filePath);
    }

    /**
     * 保存视频预览图
     *
     * @param bitmap
     * @param fileName
     */
    public void saveVideoPreview(Bitmap bitmap, String fileName)
    {
        FileUtil.saveImage(previewPath, fileName, bitmap);
    }

    /**
     * 获取预览图
     *
     * @param cameraId
     * @param deviceId
     * @return
     */
    public Bitmap getPreview(String cameraId, String deviceId)
    {
        String filePath = previewPath + cameraId + "_" + deviceId + ".jpg";
        return BitmapFactory.decodeFile(filePath);
    }
}
