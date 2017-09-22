package com.suntek.ibmsapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.model.Camera;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 摄像机搜索adapter
 *
 * @author jimmy
 */
public class CameraSearchAdapter extends BaseAdapter
{
    private Context context;

    private List<Camera> cameraList;

    public CameraSearchAdapter(Context context, List<Camera> cameraList)
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
        CameraHistoryAdapter.ViewHolder holder;
        if (view != null)
        {
            holder = (CameraHistoryAdapter.ViewHolder) view.getTag();
        }
        else
        {
            view = LayoutInflater.from(context).inflate(R.layout.item_camera_search, null);
            holder = new CameraHistoryAdapter.ViewHolder(view);
            view.setTag(holder);
        }
        holder.tvCameraName.setText(cameraList.get(i).getName());
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
            ButterKnife.bind(this, view);
        }
    }

    public List<Camera> getCameraList()
    {
        return cameraList;
    }

    public void setCameraList(List<Camera> cameraList)
    {
        this.cameraList = cameraList;
    }
}
