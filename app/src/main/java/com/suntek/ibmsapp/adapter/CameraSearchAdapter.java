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
        CameraSearchAdapter.ViewHolder holder;
        if (view != null)
        {
            holder = (CameraSearchAdapter.ViewHolder) view.getTag();
        }
        else
        {
            view = LayoutInflater.from(context).inflate(R.layout.item_camera_search, null);
            holder = new CameraSearchAdapter.ViewHolder(view);
            view.setTag(holder);
        }
        holder.tvCameraName.setText(cameraList.get(i).getName());
        String type = cameraList.get(i).getType();
        String status = cameraList.get(i).getIsUsed();
        // 1-球机  2-半球  3-固定枪机  4-遥控枪机
        if ("1".equals(type) || "2".equals(type))
        {
            if ("1".equals(status))
            {
                holder.ivCameraType.setBackgroundResource(R.mipmap.ic_ball_camera);
            }
            else
            {
                holder.ivCameraType.setBackgroundResource(R.mipmap.ic_bad_ball);
            }
        }
        else
        {
            if ("1".equals(status))
            {
                holder.ivCameraType.setBackgroundResource(R.mipmap.ic_camera_gun);
            }
            else
            {
                holder.ivCameraType.setBackgroundResource(R.mipmap.ic_bad_gun);
            }
        }
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
