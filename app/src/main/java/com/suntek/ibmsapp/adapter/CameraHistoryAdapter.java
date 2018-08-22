package com.suntek.ibmsapp.adapter;

import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.page.camera.CameraPlayerActivity;
import com.suntek.ibmsapp.task.base.BaseTask;
import com.suntek.ibmsapp.task.camera.CameraDelHistoryTask;
import com.suntek.ibmsapp.util.BitmapUtil;
import com.suntek.ibmsapp.util.DateUtil;
import com.suntek.ibmsapp.util.PreviewUtil;
import com.suntek.ibmsapp.util.SaveDataWithSharedHelper;
import com.suntek.ibmsapp.widget.ToastHelper;

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

    private OnDeleteListening onDeleteListening;

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
        String playCountStr = cameraList.get(i).getPlayCount();
        if (playCountStr != null)
        {
            int playCount = Integer.parseInt(playCountStr);
            if (playCount > 99)
                holder.tvPlayCount.setText("99+");
            else
                holder.tvPlayCount.setText(playCount + "");
        }
        else
        {
            holder.tvPlayCount.setText("");
        }
        Camera camera = cameraList.get(i);
        Bitmap bitmap = PreviewUtil.getInstance().getPreview(camera.getId(), camera.getDeviceId());
        if (bitmap != null)
            holder.ivPreview.setImageBitmap(BitmapUtil.zoomBitmap(bitmap, ivPreviewWidth, ivPreviewHeight));
        else
            holder.ivPreview.setImageBitmap(null);

        holder.btnDel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onDeleteListening.onClick(camera);
            }
        });
        holder.llContent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, CameraPlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("camera", camera);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        holder.swMore.quickClose();

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

        @BindView(R.id.tv_play_count)
        TextView tvPlayCount;

        @BindView(R.id.btn_del)
        Button btnDel;

        @BindView(R.id.ll_content)
        LinearLayout llContent;

        @BindView(R.id.sml_more)
        SwipeMenuLayout swMore;

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

    public OnDeleteListening getOnDeleteListening()
    {
        return onDeleteListening;
    }

    public void setOnDeleteListening(OnDeleteListening onDeleteListening)
    {
        this.onDeleteListening = onDeleteListening;
    }

    public interface OnDeleteListening
    {
        void onClick(Camera camera);
    }
}
