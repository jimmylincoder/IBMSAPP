package com.suntek.ibmsapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.suntek.ibmsapp.R;

import java.util.List;
import java.util.Map;

/**
 *
 * 摄像头列表adapter
 *
 * @author jimmy
 */
public class CameraListAdapter extends BaseAdapter
{
    private Context context;

    private List<Map<String,Object>> cameraList;

    private ViewHolder viewHolder;

    public CameraListAdapter(Context context,List<Map<String,Object>> cameraList)
    {
        this.context = context;
        this.cameraList = cameraList;
    }

    @Override
    public int getCount()
    {
        return cameraList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return cameraList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_camera_list,null);
            viewHolder.ivPreView = (ImageView) convertView.findViewById(R.id.iv_preview);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_camera_name);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    private class ViewHolder
    {
        //摄像机预览
        private ImageView ivPreView;
        //摄像机名称
        private TextView tvName;
    }

    public List<Map<String, Object>> getCameraList()
    {
        return cameraList;
    }

    public void setCameraList(List<Map<String, Object>> cameraList)
    {
        this.cameraList = cameraList;
    }
}
