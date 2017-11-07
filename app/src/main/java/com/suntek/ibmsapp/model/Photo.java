package com.suntek.ibmsapp.model;

import android.support.annotation.NonNull;

import com.suntek.ibmsapp.util.DateUtil;

import java.io.Serializable;
import java.util.List;

/**
 * 相册
 *
 * @author jimmy
 */
public class Photo implements Serializable
{
    private String date;

    private List<String> photoPaths;

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public List<String> getPhotoPaths()
    {
        return photoPaths;
    }

    public void setPhotoPaths(List<String> photoPaths)
    {
        this.photoPaths = photoPaths;
    }

}
