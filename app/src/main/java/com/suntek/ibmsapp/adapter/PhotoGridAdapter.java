package com.suntek.ibmsapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.widget.SquareBitmapDisplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import wseemann.media.FFmpegMediaMetadataRetriever;


/**
 * 表格相册adapter
 *
 * @author jimmy
 */
public class PhotoGridAdapter extends BaseAdapter
{
    private List<String> photoPaths = new ArrayList<>();

    private Context context;

    private Map<String, Boolean> chooseMap;

    private ImageLoader imageLoader;

    private Map<String, Bitmap> videioBitmapMap;

    public PhotoGridAdapter(Context context, List<String> photoPaths)
    {
        this.context = context;
        this.photoPaths = photoPaths;
        this.chooseMap = new HashMap<>();
        this.videioBitmapMap = new HashMap<>();
        DisplayImageOptions options = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true).cacheOnDisc(true).displayer(new SquareBitmapDisplay()).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(context)
                .defaultDisplayImageOptions(options)
                .build();

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
        for (String path : photoPaths)
        {
            chooseMap.put(path, false);
        }
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

        if (photoPaths.get(position).endsWith(".jpg"))
        {
            imageLoader.displayImage("file://" + photoPaths.get(position), holder.ivPhoto);
        }
        else
        {
//            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(photoPaths.get(position), MICRO_KIND);
//            holder.ivPhoto.setImageBitmap(bitmap);
            String filePath = photoPaths.get(position);
            Bitmap preview = videioBitmapMap.get(filePath);
            if (preview == null)
            {
                FFmpegMediaMetadataRetriever mediaMetadataRetriever = new FFmpegMediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(photoPaths.get(position));
                Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime();
                videioBitmapMap.put(filePath, bitmap);
                holder.ivPhoto.setImageBitmap(bitmap);
            }
            else
            {
                holder.ivPhoto.setImageBitmap(preview);
            }
        }
        boolean isChoose = chooseMap.get(photoPaths.get(position));
        if (isChoose)
        {
            holder.llChoose.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.llChoose.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder
    {
        @BindView(R.id.iv_photo)
        ImageView ivPhoto;

        @BindView(R.id.ll_choose)
        LinearLayout llChoose;

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

    public void selectOne(String key)
    {
        boolean isSelected = chooseMap.get(key);
        if (isSelected)
        {
            chooseMap.put(key, false);
        }
        else
        {
            chooseMap.put(key, true);
        }
    }

    public void clearSelected()
    {
        for (String path : photoPaths)
        {
            chooseMap.put(path, false);
        }
    }

    public List<String> getSelected()
    {
        List<String> paths = new ArrayList<>();
        Iterator entries = chooseMap.entrySet().iterator();
        while (entries.hasNext())
        {
            Map.Entry entry = (Map.Entry) entries.next();
            boolean isSelected = (boolean) entry.getValue();
            if (isSelected)
                paths.add((String) entry.getKey());
        }
        return paths;
    }

    public void selectAll()
    {
        List<String> paths = new ArrayList<>();
        Iterator entries = chooseMap.entrySet().iterator();
        while (entries.hasNext())
        {
            Map.Entry entry = (Map.Entry) entries.next();
            chooseMap.put((String) entry.getKey(), true);
        }
    }
}
