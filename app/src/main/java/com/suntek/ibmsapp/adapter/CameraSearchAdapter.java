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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 摄像头搜索列表
 *
 * @author jimmy
 */
public class CameraSearchAdapter extends BaseAdapter
{
    private Context context;

    private List<Map<String,Object>> cameraList;

    public CameraSearchAdapter(Context context,List<Map<String,Object>> cameraList)
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
    public Object getItem(int i)
    {
        return cameraList.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        CameraSearchAdapter.ViewHolder holder;
        if(view != null)
        {
            holder = (CameraSearchAdapter.ViewHolder) view.getTag();
        }
        else
        {
            view = LayoutInflater.from(context).inflate(R.layout.item_camera_search,null);
            holder = new CameraSearchAdapter.ViewHolder(view);
            view.setTag(holder);
        }
        holder.tvCameraName.setText(cameraList.get(i).get("name") + "");
        return view;
    }

    static class ViewHolder
    {
        @BindView(R.id.iv_camera_type)
        ImageView ivCameraType;

        @BindView(R.id.tv_camera_name)
        TextView tvCameraName;

        public ViewHolder(View view)
        {
            ButterKnife.bind(this,view);
        }
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
