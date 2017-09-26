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
 * 摄像头列表adapter
 *
 * @author jimmy
 */
public class CameraListAdapter extends BaseAdapter
{
    private Context context;

    private List<Camera> cameraList;

    private ViewHolder viewHolder;

    public CameraListAdapter(Context context, List<Camera> cameraList)
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
        if (convertView != null)
        {
            holder = (CameraListAdapter.ViewHolder) convertView.getTag();

        }
        else
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_camera_list, null);
            holder = new CameraListAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.tvName.setText((String) cameraList.get(position).getName());
        holder.tvOrgName.setText(cameraList.get(position).getOrgName());
        String type = cameraList.get(position).getType();
        String status = cameraList.get(position).getIsUsed();
        // 1-球机  2-半球  3-固定枪机  4-遥控枪机
        if ("1".equals(type) || "2".equals(type))
        {
            if ("1".equals(status))
            {
                holder.ivCameraType.setBackgroundResource(R.mipmap.ic_ball_camera);
                holder.tvOnlineStatus.setText("在线");
                holder.tvOnlineStatus.setTextColor(context.getResources().getColor(R.color.col_1aa7f0));
            }
            else
            {
                holder.ivCameraType.setBackgroundResource(R.mipmap.ic_bad_ball);
                holder.tvOnlineStatus.setText("离线");
                holder.tvOnlineStatus.setTextColor(context.getResources().getColor(R.color.col_a5a5a5));
            }
        }
        else
        {
            if ("1".equals(status))
            {
                holder.ivCameraType.setBackgroundResource(R.mipmap.ic_camera_gun);
                holder.tvOnlineStatus.setText("在线");
                holder.tvOnlineStatus.setTextColor(context.getResources().getColor(R.color.col_1aa7f0));
            }
            else
            {
                holder.ivCameraType.setBackgroundResource(R.mipmap.ic_bad_gun);
                holder.tvOnlineStatus.setText("离线");
                holder.tvOnlineStatus.setTextColor(context.getResources().getColor(R.color.col_a5a5a5));
            }
        }
        return convertView;
    }

    static class ViewHolder
    {
        //摄像机预览
        @BindView(R.id.iv_preview)
        ImageView ivPreView;

        //摄像机类型
        @BindView(R.id.iv_camera_type)
        ImageView ivCameraType;

        //摄像机名称
        @BindView(R.id.tv_camera_name)
        TextView tvName;

        @BindView(R.id.tv_org_name)
        TextView tvOrgName;

        @BindView(R.id.tv_online_status)
        TextView tvOnlineStatus;

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
