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
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * 摄像头列表adapter
 *
 * @author jimmy
 */
public class CameraListAdapter extends BaseAdapter
{
    private Context context;

    private List<Camera> cameraList;

    private ViewHolder viewHolder;

    public CameraListAdapter(Context context,List<Camera> cameraList)
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
        CameraListAdapter.ViewHolder holder;
        if(convertView != null)
        {
            holder = (CameraListAdapter.ViewHolder) convertView.getTag();

        }
        else
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_camera_list,null);
            holder = new CameraListAdapter.ViewHolder(convertView);
            convertView.setTag(holder);        }

        holder.tvName.setText((String) cameraList.get(position).getName());
        return convertView;
    }

    static class ViewHolder
    {
        //摄像机预览
        @BindView(R.id.iv_preview)
        ImageView ivPreView;

        //摄像机名称
        @BindView(R.id.tv_camera_name)
        TextView tvName;

        public ViewHolder(View view)
        {
            ButterKnife.bind(this,view);
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
