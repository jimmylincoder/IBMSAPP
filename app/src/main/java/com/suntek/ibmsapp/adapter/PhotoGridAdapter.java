package com.suntek.ibmsapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.suntek.ibmsapp.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 表格相册adapter
 *
 * @author jimmy
 */
public class PhotoGridAdapter extends BaseAdapter
{
    private List<String> photoPaths = new ArrayList<>();

    private Context context;

    public PhotoGridAdapter(Context context, List<String> photoPaths)
    {
        this.context = context;
        this.photoPaths = photoPaths;
    }

    @Override
    public int getCount()
    {
        return photoPaths.size();
    }

    @Override
    public Object getItem(int position)
    {
        return photoPaths.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        PhotoGridAdapter.ViewHolder holder;
        if (convertView != null)
        {
            holder = (PhotoGridAdapter.ViewHolder) convertView.getTag();
        }
        else
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_photo_grid, null);
            holder = new PhotoGridAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        }
        FileInputStream fileInputStream = null;

        if (photoPaths.get(position).endsWith(".jpg"))
        {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
            ImageLoader.getInstance().init(config);
            ImageLoader.getInstance().displayImage("file://" + photoPaths.get(position), holder.ivPhoto);
        }
        else
        {

        }

        return convertView;
    }

    static class ViewHolder
    {
        @BindView(R.id.iv_photo)
        ImageView ivPhoto;

        public ViewHolder(View view)
        {
            ButterKnife.bind(this, view);
        }
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
