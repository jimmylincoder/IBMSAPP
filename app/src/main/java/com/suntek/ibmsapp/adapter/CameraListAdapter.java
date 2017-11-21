package com.suntek.ibmsapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.cache.ACache;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.page.camera.CameraHKHistoryActivity;
import com.suntek.ibmsapp.page.camera.CameraPlayHKActivity;
import com.suntek.ibmsapp.util.BitmapUtil;
import com.suntek.ibmsapp.util.PreviewUtil;
import com.suntek.ibmsapp.widget.UnityDialog;

import java.io.Serializable;
import java.util.ArrayList;
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

    private ACache aCache;

    private int ivPreviewWidth = 200;

    private int ivPreviewHeight = 110;

    private List<Camera> topCameras;

    public CameraListAdapter(Context context, List<Camera> cameraList)
    {
        this.context = context;
        this.cameraList = cameraList;
        this.aCache = ACache.get(context);
        topList();
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
        Camera camera = cameraList.get(position);
        Bitmap bitmap = PreviewUtil.getInstance().getPreview(camera.getId(), camera.getDeviceId());
        if (bitmap != null)
            holder.ivPreView.setImageBitmap(BitmapUtil.zoomBitmap(bitmap, ivPreviewWidth, ivPreviewHeight));
        else
            holder.ivPreView.setImageBitmap(null);

        if (topCameras.contains(camera))
        {
            holder.llContent.setBackgroundColor(context.getResources().getColor(R.color.clo_e8f9ff));
            holder.btnTop.setText("取消");
        }
        else
        {
            holder.btnTop.setText("置顶");
            holder.llContent.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        holder.llContent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, CameraPlayHKActivity.class);
                Camera camera = cameraList.get(position);
                if (camera.getIsUsed().equals("1"))
                {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("camera", camera);
                    intent.putExtras(bundle);
                    intent.putExtra("cameraId", cameraList.get(position).getId());
                    intent.putExtra("cameraName", cameraList.get(position).getName());
                    context.startActivity(intent);
                }
                else
                {
                    new UnityDialog(context)
                            .setTitle("温馨提示")
                            .setHint("该摄像头已离线")
                            .setConfirm("确定", new UnityDialog.OnConfirmDialogListener()
                            {
                                @Override
                                public void confirm(UnityDialog unityDialog, String content)
                                {
                                    unityDialog.dismiss();
                                }
                            }).show();
                }
            }
        });

        holder.btnTop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                List<String> cameraIds = (List<String>) aCache.getAsObject("camera_top");
                String id = camera.getId();
                if (cameraIds == null)
                {
                    cameraIds = new ArrayList<String>();
                    cameraIds.add(id);
                }
                else
                {
                    int index = cameraIds.indexOf(id);
                    if (!cameraIds.contains(id))
                    {
                        if (index != -1)
                            cameraIds.remove(index);
                        cameraIds.add(0, id);
                    }
                    else
                    {
                        cameraIds.remove(id);
                    }
                }
                aCache.put("camera_top", (Serializable) cameraIds);
                topList();
                holder.swlMore.quickClose();
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private void topList()
    {
        List<String> cameraIds = (List<String>) aCache.getAsObject("camera_top");
        if (cameraIds == null)
        {
            topCameras = new ArrayList<>();
            return;
        }
        List<Camera> topCamera = new ArrayList<>();
        for (Camera camera : cameraList)
        {
            for (String cameraId : cameraIds)
            {
                if (camera.getId().equals(cameraId))
                {
                    topCamera.add(camera);
                }
            }
        }
        this.topCameras = topCamera;
        for (Camera camera : topCamera)
        {
            cameraList.remove(camera);
            cameraList.add(0, camera);
        }
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

        @BindView(R.id.ll_content)
        LinearLayout llContent;

        @BindView(R.id.btn_top)
        Button btnTop;

        @BindView(R.id.sml_more)
        SwipeMenuLayout swlMore;

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
        topList();
    }
}
