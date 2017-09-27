package com.suntek.ibmsapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.model.Area;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 区域列表
 * @author jimmy
 */
public class AreaListAdapter extends BaseAdapter
{
    private Context context;

    private List<Area> cameraList;

    public AreaListAdapter(Context context,List<Area> cameraList)
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
        AreaListAdapter.ViewHolder holder;
        if(view != null)
        {
            holder = (AreaListAdapter.ViewHolder) view.getTag();
        }
        else
        {
            view = LayoutInflater.from(context).inflate(R.layout.item_area_list,null);
            holder = new AreaListAdapter.ViewHolder(view);
            view.setTag(holder);
        }

        holder.tvAreaName.setText(cameraList.get(i).getName());
        return view;
    }

    static class ViewHolder
    {
        @BindView(R.id.ll_next)
        LinearLayout llNext;

        @BindView(R.id.tv_area_name)
        TextView tvAreaName;

        public ViewHolder(View view)
        {
            ButterKnife.bind(this,view);
        }
    }
}
