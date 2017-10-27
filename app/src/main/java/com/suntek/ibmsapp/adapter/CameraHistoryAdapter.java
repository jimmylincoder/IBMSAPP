package com.suntek.ibmsapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.util.BitmapUtil;
import com.suntek.ibmsapp.util.DateUtil;
import com.suntek.ibmsapp.util.PreviewUtil;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 摄像头搜索列表
 *
 * @author jimmy
 */
public class CameraHistoryAdapter extends BaseAdapter
{
    private Context context;

    private List<Camera> cameraList;

    private int ivPreviewWidth = 200;

    private int ivPreviewHeight = 110;

    public CameraHistoryAdapter(Context context, List<Camera> cameraList)
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
            view = LayoutInflater.from(context).inflate(R.layout.item_camera_history, null);
            holder = new CameraHistoryAdapter.ViewHolder(view);
            view.setTag(holder);
        }
        holder.tvCameraName.setText(cameraList.get(i).getName());
        holder.tvPlayTime.setText(DateUtil.convertYYYY_MM_DD_HH_MM_SS(
                new Date(cameraList.get(i).getPlayTime())));
        String status = cameraList.get(i).getIsUsed();
        if ("1".equals(status))
        {
            holder.tvOnlineStatus.setText("在线");
            holder.tvOnlineStatus.setTextColor(context.getResources().getColor(R.color.col_1aa7f0));
        }
        else
        {
            holder.tvOnlineStatus.setText("离线");
            holder.tvOnlineStatus.setTextColor(context.getResources().getColor(R.color.col_a5a5a5));
        }

//        String photoBase64 = cameraList.get(i).getPhotoBase64();
//        String id = cameraList.get(i).getId();
//        holder.ivPreview.setTag(id);
//        if (photoBase64 != null)
//        {
//            if (holder.ivPreview.getTag() != null && holder.ivPreview.getTag().equals(id))
//            {
//                Bitmap bitmap = BitmapUtil.base64ToBitmap(photoBase64);
//                holder.ivPreview.setImageBitmap(BitmapUtil.zoomBitmap(bitmap,
//                        ivPreviewWidth, ivPreviewHeight));
//            }
//        }
//        else
//        {
//            holder.ivPreview.setImageBitmap(null);
//        }
        Camera camera = cameraList.get(i);
        Bitmap bitmap = PreviewUtil.getInstance().getPreview(camera.getId(), camera.getDeviceId());
        if (bitmap != null)
            holder.ivPreview.setImageBitmap(BitmapUtil.zoomBitmap(bitmap, ivPreviewWidth, ivPreviewHeight));
        else
            holder.ivPreview.setImageBitmap(null);
        return view;
    }

    static class ViewHolder
    {
        @BindView(R.id.iv_preview)
        ImageView ivPreview;

        @BindView(R.id.tv_play_time)
        TextView tvPlayTime;

        @BindView(R.id.tv_camera_name)
        TextView tvCameraName;

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
