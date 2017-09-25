package com.suntek.ibmsapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.model.Photo;
import com.suntek.ibmsapp.page.photo.PhotoDetailActivity;
import com.suntek.ibmsapp.util.DateUtil;
import com.suntek.ibmsapp.widget.NoScrollGridView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.entries;

/**
 * 相册列表adapter
 *
 * @author jimmy
 */
public class PhotoListAdapter extends BaseExpandableListAdapter
{

    private List<Photo> photoList;

    private Context context;

    private boolean isEdit = false;

    private Map<String, Object> adapterMap;

    public PhotoListAdapter(Context context, List<Photo> photos)
    {
        this.context = context;
        this.photoList = photos;
        this.adapterMap = new HashMap<>();
    }


    @Override
    public int getGroupCount()
    {
        return photoList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        int total = photoList.get(groupPosition).getPhotoPaths().size();
        int count = total / 4 + (total % 4);
        return count;
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return photoList.get(groupPosition).getDate();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return photoList.get(groupPosition).getPhotoPaths().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        PhotoListAdapter.GroupViewHolder holder;
        if (convertView != null)
        {
            holder = (PhotoListAdapter.GroupViewHolder) convertView.getTag();
        }
        else
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_photo_title, null);
            holder = new PhotoListAdapter.GroupViewHolder(convertView);
            convertView.setTag(holder);
        }

        String date = DateUtil.convertByFormat(new Date(), "yyyyMMdd");
        String groupDate = photoList.get(groupPosition).getDate();
        String dateYear = groupDate.substring(0,4);
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        if (date.equals(groupDate))
        {
            holder.tvDate.setText("今天");
        }
        else if(dateYear.equals(year + ""))
        {
            holder.tvDate.setText(groupDate.substring(4,6) + "/" + groupDate.substring(6,8));
        }else
        {
            holder.tvDate.setText(photoList.get(groupPosition).getDate());

        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        PhotoListAdapter.ChildViewHolder holder;
        if (convertView != null)
        {
            holder = (PhotoListAdapter.ChildViewHolder) convertView.getTag();
        }
        else
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_photo_content, null);
            holder = new PhotoListAdapter.ChildViewHolder(convertView);
            convertView.setTag(holder);
        }
        List<String> paths = photoList.get(groupPosition).getPhotoPaths();
        List<String> paths1 = new ArrayList<>();
        for (int i = childPosition * 4; i < childPosition * 4 + 4; i++)
        {
            if (i < paths.size())
                paths1.add(paths.get(i));
        }
        PhotoGridAdapter photoGridAdapter = (PhotoGridAdapter) adapterMap.get(groupPosition + "_" + childPosition);
        if (photoGridAdapter == null)
        {
            photoGridAdapter = new PhotoGridAdapter(context, paths1);
            adapterMap.put(groupPosition + "_" + childPosition, photoGridAdapter);
        }
        holder.nsgPhoto.setAdapter(photoGridAdapter);
        PhotoGridAdapter finalPhotoGridAdapter = photoGridAdapter;
        holder.nsgPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (!isEdit)
                {
                    Intent intent = new Intent(context, PhotoDetailActivity.class);
                    intent.putExtra("photoPath", paths1.get(position));
                    context.startActivity(intent);
                }
                else
                {
                    finalPhotoGridAdapter.selectOne(paths1.get(position));
                    finalPhotoGridAdapter.notifyDataSetChanged();
                }
            }
        });
        photoGridAdapter.notifyDataSetChanged();
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }

    public List<Photo> getPhotoList()
    {
        return photoList;
    }

    public void setPhotoList(List<Photo> photoList)
    {
        this.photoList = photoList;
    }

    static class GroupViewHolder
    {
        @BindView(R.id.tv_date)
        TextView tvDate;

        public GroupViewHolder(View view)
        {
            ButterKnife.bind(this, view);
        }
    }

    static class ChildViewHolder
    {
        @BindView(R.id.nsg_photo)
        NoScrollGridView nsgPhoto;

        public ChildViewHolder(View view)
        {
            ButterKnife.bind(this, view);
        }
    }

    public void setEdit(boolean edit)
    {
        isEdit = edit;
    }

    public boolean isEdit()
    {
        return isEdit;
    }

    public void clearChoose()
    {
        Iterator entries = adapterMap.entrySet().iterator();
        while (entries.hasNext())
        {
            Map.Entry entry = (Map.Entry) entries.next();
            PhotoGridAdapter photoGridAdapter = (PhotoGridAdapter) entry.getValue();
            photoGridAdapter.clearSelected();
        }
    }

    public List<String> getSelectedPath()
    {
        List<String> paths = new ArrayList<>();
        Iterator entries = adapterMap.entrySet().iterator();
        while (entries.hasNext())
        {
            Map.Entry entry = (Map.Entry) entries.next();
            PhotoGridAdapter photoGridAdapter = (PhotoGridAdapter) entry.getValue();
            paths.addAll(photoGridAdapter.getSelected());
        }
        return paths;
    }

    public void selectAll()
    {

        List<String> paths = new ArrayList<>();
        Iterator entries = adapterMap.entrySet().iterator();
        while (entries.hasNext())
        {
            Map.Entry entry = (Map.Entry) entries.next();
            PhotoGridAdapter photoGridAdapter = (PhotoGridAdapter) entry.getValue();
            photoGridAdapter.selectAll();
            photoGridAdapter.notifyDataSetChanged();
        }
    }
}
