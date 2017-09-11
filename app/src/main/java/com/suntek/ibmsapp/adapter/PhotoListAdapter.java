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
import com.suntek.ibmsapp.widget.NoScrollGridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 相册列表adapter
 *
 * @author jimmy
 */
public class PhotoListAdapter extends BaseExpandableListAdapter
{

    private List<Photo> photoList;

    private Context context;

    public PhotoListAdapter(Context context, List<Photo> photos)
    {
        this.context = context;
        this.photoList = photos;
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
        holder.tvDate.setText(photoList.get(groupPosition).getDate());
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
        PhotoGridAdapter photoGridAdapter = new PhotoGridAdapter(context, paths1);
        holder.nsgPhoto.setAdapter(photoGridAdapter);
        holder.nsgPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(context, PhotoDetailActivity.class);
                intent.putExtra("photoPath",paths1.get(position));
                context.startActivity(intent);
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
}
