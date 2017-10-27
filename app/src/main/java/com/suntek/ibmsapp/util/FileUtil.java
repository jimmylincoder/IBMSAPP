package com.suntek.ibmsapp.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.path;

/**
 * 文件工具类
 *
 * @author jimmy
 */
public class FileUtil
{
    public static void saveImage(String path, String name, Bitmap bmp)
    {
        File appDir = new File(path);
        if (!appDir.exists())
        {
            appDir.mkdir();
        }
        String fileName = name + ".jpg";
        File file = new File(appDir, fileName);
        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void saveImageToGallery(Context context, Bitmap bmp)
    {
        // 首先保存图片
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat format1 = new SimpleDateFormat("HHmmss");
        File dir = new File("/sdcard/DCIM/Camera/ibms/");
        if (!dir.exists())
            dir.mkdir();
        String path = "/sdcard/DCIM/Camera/ibms/" + "IMG_" + format.format(new Date()) + "_" + format1.format(new Date()) + ".jpg";
        File file = new File(path);
        try
        {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
//        try
//        {
//            MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                    file.getAbsolutePath(), fileName, null);
//        } catch (FileNotFoundException e)
//        {
//            e.printStackTrace();
//        }
        // 最后通知图库更新
        //       context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
    }
}
